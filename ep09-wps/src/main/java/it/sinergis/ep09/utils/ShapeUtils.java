package it.sinergis.ep09.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class ShapeUtils {
	
	private static final Logger LOGGER = Logger.getLogger(ShapeUtils.class);
	
	public static String zipShpResult(String path) throws Exception {
		String shapePath = path.substring(0, path.lastIndexOf(File.separator));
		File dir = new File(shapePath);
		String shapeName = path.substring(path.lastIndexOf(File.separator) + 1);
		
		String name = shapeName.substring(0, shapeName.indexOf(".shp"));
		String files_accepted = name + ".shx," + name + ".shp," + name + ".prj," + name + ".fix," + name + ".dbf";
		File zip = new File(shapePath + File.separator + name + ".zip");
		String[] files = dir.list();
		List<File> list = new ArrayList<File>();
		for (String fileN : files) {
			if (files_accepted.indexOf(fileN) != -1) {
				File file = new File(shapePath + File.separator + fileN);
				list.add(file);
			}
		}
		
		addFilesToExistingZip(zip, list);
		for (File f : list) {
			FileUtils.deleteQuietly(f);
		}
		
		return zip.getAbsolutePath();
	}
	
	public static void addFilesToExistingZip(File zipFile, List<File> files) throws IOException {
		
		byte[] buf = new byte[1024];
		
		if (!zipFile.exists()) {
			zipFile.createNewFile();
		}
		String zipPath = zipFile.getAbsolutePath();
		String path = zipPath.substring(0, zipPath.lastIndexOf(File.separator));
		String namezip = zipPath.substring(zipPath.lastIndexOf(File.separator) + 1);
		File tempFile = new File(path + File.separator + "temp_" + namezip);
		FileUtils.copyFile(zipFile, tempFile);
		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		
		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			boolean notInFiles = true;
			for (File f : files) {
				if (f.getName().equals(name)) {
					notInFiles = false;
					break;
				}
			}
			if (notInFiles) {
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(name));
				// Transfer bytes from the ZIP file to the output file
				int len;
				while ((len = zin.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
			entry = zin.getNextEntry();
		}
		// Close the streams        
		zin.close();
		// Compress the files
		for (int i = 0; i < files.size(); i++) {
			InputStream in = new FileInputStream(files.get(i));
			// Add ZIP entry to output stream.
			out.putNextEntry(new ZipEntry(files.get(i).getName()));
			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			// Complete the entry
			out.closeEntry();
			in.close();
		}
		// Complete the ZIP file
		out.close();
		tempFile.delete();
	}
}
