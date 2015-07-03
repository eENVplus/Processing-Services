package it.sinergis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ProjectProperties {
	
	private static Logger LOGGER = Logger.getLogger(ProjectProperties.class);
	
	private Properties properties;
	private Properties messages;
	
	public ProjectProperties(String projectPropertiesName, String messagesPropertiesName) {
		initProjectProperties(projectPropertiesName);
		initMessagesProperties(messagesPropertiesName);
	}
	
	/**
	 * inizializzazione del project.properties
	 * 
	 * @return
	 */
	private void initProjectProperties(String projectPropertiesName) {
		InputStream inputStream = ProjectProperties.class.getClassLoader().getResourceAsStream(projectPropertiesName);
		properties = new Properties();
		try {
			properties.load(inputStream);
		}
		catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("errore nel caricamento del file di configurazione", e);
		}
	}
	
	/**
	 * lettura di una property dal file di configurazione
	 * 
	 * @param property
	 * @param properties
	 * @return
	 */
	public String loadByNameFromProject(String property) {
		return (String) properties.get(property);
	}
	
	/**
	 * inizializzazione del file di messages
	 * 
	 * @return
	 */
	private void initMessagesProperties(String messagesPropertiesName) {
		InputStream inputStream = ProjectProperties.class.getClassLoader().getResourceAsStream(messagesPropertiesName);
		messages = new Properties();
		try {
			messages.load(inputStream);
		}
		catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("errore nel caricamento del file di messages", e);
		}
	}
	
	/**
	 * lettura di una property dal file di messages
	 * 
	 * @param property
	 * @param messages
	 * @return
	 */
	public String loadByNameFromMessages(String property) {
		return (String) messages.get(property);
	}
}
