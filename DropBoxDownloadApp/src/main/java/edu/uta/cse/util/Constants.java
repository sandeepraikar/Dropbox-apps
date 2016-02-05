package edu.uta.cse.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Sandeep_Raikar
 * 
 */
public class Constants {

	public static final String APP_KEY;
	public static final String APP_SECRET_KEY;
	public static final String ACCESS_TOKEN;
	public static final String PASSWORD;
	public static final String DROPBOX_DOWNLOAD_FOLDER;
	public static final String DECRYPT_TEMP_FOLDER;
	public static final String SALT_IV_DIRECTORY;

	static {

		Configuration config = null;
		try {
			config = new PropertiesConfiguration("src/main/resources/config.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		APP_KEY = config.getString("dropbox.app.key");
		APP_SECRET_KEY = config.getString("dropbox.app.secret.key");
		ACCESS_TOKEN = config.getString("dropbox.access.token");
		PASSWORD = config.getString("aes.password");
		DROPBOX_DOWNLOAD_FOLDER = config.getString("dropbox.download.folder.location");
		DECRYPT_TEMP_FOLDER= config.getString("dropbox.decrypt.folder.location");
		SALT_IV_DIRECTORY=config.getString("aes.salt.iv.location");
	}
}
