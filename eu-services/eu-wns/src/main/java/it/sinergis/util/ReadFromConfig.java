package it.sinergis.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ReadFromConfig {
	
	private static Logger LOGGER = Logger.getLogger(ReadFromConfig.class);
	private static Properties properties;
	
	private static Properties readFromConfig() {
		InputStream inputStream = ReadFromConfig.class.getClassLoader().getResourceAsStream("config.properties");
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
		if (properties==null) {
			properties = readFromConfig();
		} 
		return (String) properties.get(property);
	}
}
