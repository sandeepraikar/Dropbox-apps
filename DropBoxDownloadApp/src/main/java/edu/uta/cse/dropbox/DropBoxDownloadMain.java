package edu.uta.cse.dropbox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;

import edu.uta.cse.util.Constants;
import edu.uta.cse.util.FileDecryptor;

public class DropBoxDownloadMain {

	private static Logger LOGGER = LoggerFactory.getLogger(DropBoxDownloadMain.class);

	private static DbxClient client;

	public static void main(String[] args) throws Exception {
		LOGGER.info("Dropbox API implementation - Download Files from Dropbox");

		LOGGER.info("Getting the App info, from the APP_KEY and APP_SECRET_KEY Provided");
		// DbxAppInfo appInfo = new DbxAppInfo(Constants.APP_KEY,
		// Constants.APP_SECRET_KEY);

		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());
		client = new DbxClient(config, Constants.ACCESS_TOKEN);

		LOGGER.info("Dropbox Account name of the user : "
				+ client.getAccountInfo().displayName);

		List<String> fileInDbox = new ArrayList<String>();
		DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
		for (DbxEntry child : listing.children) {
			fileInDbox.add(child.name);
		}
		
		LOGGER.info("List of files present in the App root folder :  "+ fileInDbox);
		LOGGER.info("Please enter the filename to download from Dropbox account !");
		String requestedFileName = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

		if(fileInDbox.contains(requestedFileName)){
			download(requestedFileName.trim());
		}else{
			LOGGER.error("The requested file is not present in your DropBox account");
		}
	}

	private static void download(String requestedFileName) {

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(
					Constants.DECRYPT_TEMP_FOLDER + "/"
							+ requestedFileName);

			DbxEntry.File downloadedFile = client.getFile("/"
					+ requestedFileName, null, outputStream);
			LOGGER.info("Metadata: " + downloadedFile.toString());
			FileDecryptor.decrypt(requestedFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
