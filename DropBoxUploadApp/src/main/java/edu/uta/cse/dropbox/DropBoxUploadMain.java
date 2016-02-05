package edu.uta.cse.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;

import edu.uta.cse.util.Constants;
import edu.uta.cse.util.FileEncryptor;

public class DropBoxUploadMain {

	private static Logger LOGGER = LoggerFactory.getLogger(DropBoxUploadMain.class);

	public static void main(String[] args) throws IOException, DbxException {

		LOGGER.info("Dropbox API implementation - Drop-in your files now! ");
		//System.out.println();
		LOGGER.info("Getting the App info, from the APP_KEY and APP_SECRET_KEY Provided");
		// DbxAppInfo appInfo = new DbxAppInfo(Constants.APP_KEY,
		// Constants.APP_SECRET_KEY);

		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());
		DbxClient client = new DbxClient(config, Constants.ACCESS_TOKEN);

		LOGGER.info("Dropbox Account name of the user : "
				+ client.getAccountInfo().displayName);

		LOGGER.info("Monitor the directory for files,which needs to be uploaded");
		String encryptedFilePath = null;
		try {
			Path faxFolder = Paths.get(Constants.DROPBOX_UPLOAD_FOLDER);
			WatchService watchService = FileSystems.getDefault()
					.newWatchService();
			faxFolder.register(watchService,
					StandardWatchEventKinds.ENTRY_CREATE);
			boolean valid = true;
			do {
				WatchKey watchKey = watchService.take();

				for (@SuppressWarnings("rawtypes")
				WatchEvent event : watchKey.pollEvents()) {
					@SuppressWarnings({ "unused", "rawtypes" })
					WatchEvent.Kind kind = event.kind();
					if (StandardWatchEventKinds.ENTRY_CREATE.equals(event
							.kind())) {
						Thread.sleep(1000);
						LOGGER.info("filename "+ event.context().toString());
						encryptedFilePath = FileEncryptor.encryptFile(event.context().toString());
						Thread.sleep(1000);
						uploadFileToDropBox(encryptedFilePath, client);
						Thread.sleep(1000);
						GoogleDriveUpload.uploadFileToGoogleDrive(encryptedFilePath);
					}
				}
				valid = watchKey.reset();

			} while (valid);

		} catch (IOException e) {
			LOGGER.info("IOException occured : " + e);
		} catch (InterruptedException e) {
			LOGGER.info("InterruptedException occured : " + e);
		} catch (Exception e) {
			LOGGER.info("Exception occured : " + e);
		}		

}		
	
	private static void uploadFileToDropBox(String encryptedFilePath,
			DbxClient client) {
		FileInputStream inputStream = null;
		try {
			File inputFile = new File(encryptedFilePath);
			inputStream = new FileInputStream(encryptedFilePath);
			DbxEntry.File uploadedFile = client.uploadFile(
					"/" + inputFile.getName(), DbxWriteMode.add(),
					inputFile.length(), inputStream);
			LOGGER.info("Uploaded: " + uploadedFile.toString());
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
