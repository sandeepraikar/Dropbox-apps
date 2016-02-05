package edu.uta.cse.dropbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;


public class GoogleDriveUpload {

	private static String CLIENT_ID = "292314252163-4tg4u70joh4skk715d3iijecv4me6h0t.apps.googleusercontent.com";
	private static String CLIENT_SECRET = "P49q0HndIWcfX3GZ6Rkjc4am";

	private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

	public static void uploadFileToGoogleDrive(String encryptedFilePath) throws IOException{
		java.io.File fileContent = new java.io.File(encryptedFilePath);
		
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
				Arrays.asList(DriveScopes.DRIVE)).setAccessType("offline")
				.setApprovalPrompt("auto").build();

		String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI)
				.build();

		System.out
				.println("Please open the following URL in your browser then type the authorization code:");
		System.out.println("  " + url);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String code = br.readLine();

		GoogleTokenResponse response = flow.newTokenRequest(code)
				.setRedirectUri(REDIRECT_URI).execute();
		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(httpTransport).setJsonFactory(jsonFactory)
				.setClientSecrets(CLIENT_ID, CLIENT_SECRET).build()
				.setFromTokenResponse(response);
		String accessToken = credential.getAccessToken();
		String refreshToken = credential.getRefreshToken();

		GoogleCredential credential1 = new GoogleCredential.Builder()
				.setJsonFactory(jsonFactory).setTransport(httpTransport)
				.setClientSecrets(CLIENT_ID, CLIENT_SECRET).build();
		credential1.setAccessToken(accessToken);
		credential1.setRefreshToken(refreshToken);

		// Create a new authorized API client
		Drive service = new Drive.Builder(httpTransport, jsonFactory,
				credential).build();

		// Insert a file
		File body = new File();

		body.setTitle(fileContent.getName());
		FileContent mediaContent = new FileContent("text/plain", fileContent);
		File file = service.files().insert(body, mediaContent).execute();

		System.out.println("File ID: " + file.getId());

	}
}
