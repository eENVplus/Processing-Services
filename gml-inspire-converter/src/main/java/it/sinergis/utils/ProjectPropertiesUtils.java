package it.sinergis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ProjectPropertiesUtils {
	
	private static Logger LOGGER = Logger.getLogger(ProjectPropertiesUtils.class);
	private static Properties properties;
	private static Properties messages;
	
	/**
	 * config file reader
	 * 
	 * @return
	 */
	private static Properties readFromConfig() {
		InputStream inputStream = ProjectPropertiesUtils.class.getClassLoader().getResourceAsStream(
				"gml_converter.properties");
		properties = new Properties();
		try {
			properties.load(inputStream);
		}
		catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("errore nel caricamento del file di configurazione", e);
		}
		return properties;
	}
	
	public static String loadByName(String property) {
		if (properties == null) {
			properties = readFromConfig();
		}
		return (String) properties.get(property);
	}
	
	/**
	 * messages file reader
	 * 
	 * @return
	 */
	private static Properties readFromMessage() {
		InputStream inputStream = ProjectProperties.class.getClassLoader().getResourceAsStream(
				"messages_converter.properties");
		messages = new Properties();
		try {
			messages.load(inputStream);
		}
		catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("errore nel caricamento del file di messages", e);
		}
		return messages;
	}
	
	public static String loadMessageByName(String property) {
		if (messages == null) {
			messages = readFromMessage();
		}
		return (String) messages.get(property);
	}
	
}
