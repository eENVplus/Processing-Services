package it.sinergis.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class ProcessUtils {
	
	private static Logger LOGGER = Logger.getLogger(ProcessUtils.class);
	
	public static boolean exec(String command) throws Exception {
		
		boolean success = true;
		
		Runtime run = Runtime.getRuntime();
		Process pp = run.exec(command);
		
		BufferedReader errStreamReader = new BufferedReader(new InputStreamReader(pp.getErrorStream()));
		
		StringBuffer error = new StringBuffer();
		
		for (String line; (line = errStreamReader.readLine()) != null;) {
			LOGGER.debug("log di gdal "+line);
			if (line.toLowerCase().contains("warning") || !line.toLowerCase().contains("error")) {
				continue;
			}
			error.append(line);
		}
		
		if (!error.toString().equals("")) {
			LOGGER.error("errore di gdal "+error.toString());
			success = false;
			throw new Exception(error.toString());
		}
		return success;
	}
	
}
