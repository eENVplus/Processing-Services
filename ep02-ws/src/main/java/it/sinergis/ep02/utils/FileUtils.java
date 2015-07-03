package it.sinergis.ep02.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.Part;

import org.apache.log4j.Logger;

public class FileUtils {
	
	private static final Logger LOGGER = Logger.getLogger(FileUtils.class);
	
	/**
	 * metodo che trova il nome di un file che termina con una data estensione termina appena ne trova uno
	 * 
	 * @param folderName
	 * @param ext
	 */
	public static String getFirstFileInFolderWithExtension(String folderName, String ext) {
		File dir = new File(folderName);
		if (dir.isDirectory() == false) {
			return null;
		}
		
		String[] fileNameList = dir.list();
		for (String fileName : fileNameList) {
			if (fileName.endsWith(ext)) {
				return fileName;
			}
		}
		return null;
	}
	
	/**
	 * metodo che effettua l'upload sul server
	 * 
	 * @param filePart
	 * @param path
	 * @param fileName
	 * @throws IOException
	 */
	public static boolean uploadFile(Part filePart, String uploadPathFile) throws IOException {
		OutputStream out = null;
		InputStream filecontent = null;
		checkFolder(uploadPathFile);
		String fileName = getFileNameByPart(filePart);
		try {
			if (filePart.getContentType() != null
					&& (filePart.getContentType().equals(Constants.MIME_APPLICATION_ZIP)
							|| filePart.getContentType().equals(Constants.MIME_APPLICATION_X_ZIP_COMPRESSED) || filePart
							.getContentType().equals(Constants.MIME_APPLICATION_X_COMPRESSED))) {
				
				unZipFile(filePart.getInputStream(), uploadPathFile);
				
			}
			else {
				out = new FileOutputStream(new File(uploadPathFile + fileName));
				filecontent = filePart.getInputStream();
				int read = 0;
				final byte[] bytes = new byte[1024];
				
				while ((read = filecontent.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
			}
			
			LOGGER.info("file " + fileName + " uploaded to " + uploadPathFile);
			return true;
		}
		catch (Exception e) {
			LOGGER.error("Problems during file upload " + fileName, e);
			return false;
		}
		finally {
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}
		}
		
	}
	
	/**
	 * metodo che controlla se esiste la cartella e in caso la crea
	 * 
	 * @param fileName
	 */
	public static void checkFolder(String pathFile) {
		//tolgo l'estensione dal nome per creare la cartella se non c'è
		File f = new File(pathFile);
		if (!f.exists()) {
			LOGGER.debug("crata cartella " + pathFile);
			f.mkdirs();
		}
	}
	
	/**
	 * restituisce il file name da un tipo part
	 * 
	 * @param part
	 * @return
	 */
	public static String getFileNameByPart(final Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				if (content.lastIndexOf(File.separator) != -1) {
					return content.substring(content.lastIndexOf(File.separator) + 1).trim().replace("\"", "");
				}
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
	
	/**
	 * metodo che fa l'unzip dell'inputStream
	 * 
	 * @param fis
	 * @param pathFile
	 */
	public static void unZipFile(InputStream fis, String pathFile) {
		try {
			final int BUFFER = 2048;
			BufferedOutputStream dest = null;
			CheckedInputStream checksum = new CheckedInputStream(fis, new Adler32());
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				LOGGER.info("Extracting " + entry);
				int count;
				byte data[] = new byte[BUFFER];
				// write the files to the disk
				FileOutputStream fos = new FileOutputStream(pathFile + entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		}
		catch (Exception e) {
			LOGGER.error("errore nell'estrazione del file " + pathFile, e);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * metodo che scrive il report
	 * 
	 * @param report
	 * @param reportPath
	 *            path del file su cui scrivere
	 * @throws IOException
	 */
	public static void writeReportFile(String report, String reportPath) throws IOException {
		File file = new File(reportPath);
		
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(report);
		bw.close();
		
	}
	
	public static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				LOGGER.debug("cancellazione del file " + c.getName());
				delete(c);
			}
		}
		if (!f.delete()) {
			throw new FileNotFoundException("Failed to delete file: " + f);
		}
	}
	
	public static String readFile(String file) {
		BufferedReader br = null;
		String fileContent = "";
		
		try {
			
			String sCurrentLine;
			
			br = new BufferedReader(new FileReader(file));
			
			while ((sCurrentLine = br.readLine()) != null) {
				fileContent += sCurrentLine;
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null)
					br.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return fileContent;
	}
}
