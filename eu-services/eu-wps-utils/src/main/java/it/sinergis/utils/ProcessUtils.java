package it.sinergis.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class ProcessUtils {
	
	private static Logger LOGGER = Logger.getLogger(ProcessUtils.class);
	
	public ProcessUtils() {
		
	}
	
	/**
	 * esecuzione del comando gdal o py
	 * @param command comando da eseguire
	 * @param getOutput true se si vuole che venga restiutuito l'output
	 * @return l'output derivante dall'esecuzione del comando
	 */
	public String exec(String command, boolean getOutput) throws Exception {
		
		Runtime run = Runtime.getRuntime();
		LOGGER.info("inizio comando da eseguire " + command);
		Process pp = run.exec(command);
		
		return execOutput(pp, getOutput);
	}
	
	/**
	 * metodo che restituisce l'ouput dell'esecuzione del comando
	 * @param pp
	 * @param getOutput
	 * @return
	 * @throws Exception
	 */
	public String execOutput(Process pp, boolean getOutput) throws Exception {
		StringBuffer response = new StringBuffer();
		BufferedReader streamReader = null;
		if (getOutput) {
			//se il processo restituisce in output delle informazioni e non solo degli errori (vedi zonal_stat.py)
			streamReader = new BufferedReader(new InputStreamReader(pp.getInputStream()));
		}
		else {
			streamReader = new BufferedReader(new InputStreamReader(pp.getErrorStream()));
		}
		
		for (String line; (line = streamReader.readLine()) != null;) {
			LOGGER.debug("log " + line);
			if (!getOutput && (line.toLowerCase().contains("warning") || !line.toLowerCase().contains("error"))) {
				continue;
			}
			response.append(line + System.getProperty("line.separator"));
		}
		
		if (!getOutput) {
			if (!response.toString().equals("")) {
				LOGGER.error("errore " + response.toString());
				throw new Exception(response.toString());
			}
		}
		
		LOGGER.info("fine comando ");
		return response.toString();
	}
	
	/**
	 * esecuzione del comando gdal o py
	 * @param command in un array di stringhe
	 * @param getOutput true se si vuole che venga restiutuito l'output
	 * @return l'output derivante dall'esecuzione del comando
	 * @throws Exception
	 */
	public String execArrayStr(String[] command, boolean getOutput) throws Exception {
		
		Runtime run = Runtime.getRuntime();
		String commandStr = "";
		for (int i = 0; i < command.length; i++) {
			commandStr += command[i] + " ";
		}
		LOGGER.info("inizio comando da eseguire " + commandStr);
		Process pp = run.exec(command);
		LOGGER.info("fine comando da eseguire " + commandStr);
		
		return execOutput(pp, getOutput);
	}
	
}
