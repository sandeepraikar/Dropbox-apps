package edu.uta.cse.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileEncryptor {

	private static Logger LOGGER = LoggerFactory.getLogger(FileEncryptor.class);

	public static String encryptFile(String fileName) {

		// Encrypted File Location
		File encryptedFile = new File(Constants.ENCRYPTED_FILES_DIRECTORY+ "/" + fileName.substring(0,fileName.length()-4)+"."+"enc");

		try {

			// file to be encrypted
			FileInputStream inFile = new FileInputStream(
					Constants.DROPBOX_UPLOAD_FOLDER + "/" + fileName);

			// encrypted file
			FileOutputStream outFile = new FileOutputStream(encryptedFile);

			// password to encrypt the file
			String password = Constants.PASSWORD;

			// password, iv and salt should be transferred to the other end
			// in a secure manner

			// salt is used for encoding
			// writing it to a file
			// salt should be transferred to the recipient securely
			// for decryption
			byte[] salt = new byte[8];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(salt);
			FileOutputStream saltOutFile = new FileOutputStream(Constants.SALT_IV_DIRECTORY+"/"+"salt.enc");
			saltOutFile.write(salt);
			saltOutFile.close();

			SecretKeyFactory factory = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
					65536, 256);
			SecretKey secretKey = factory.generateSecret(keySpec);
			SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

			//
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();

			// iv adds randomness to the text and just makes the mechanism more
			// secure
			// used while initializing the cipher
			// file to store the iv
			FileOutputStream ivOutFile = new FileOutputStream(Constants.SALT_IV_DIRECTORY+"/"+"iv.enc");
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			ivOutFile.write(iv);
			ivOutFile.close();

			// file encryption
			byte[] input = new byte[64];
			int bytesRead;

			while ((bytesRead = inFile.read(input)) != -1) {
				byte[] output = cipher.update(input, 0, bytesRead);
				if (output != null)
					outFile.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				outFile.write(output);

			inFile.close();
			outFile.flush();
			outFile.close();

			LOGGER.info("File Encrypted successfully!!");

		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidParameterSpecException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return encryptedFile.getAbsolutePath();
	}

}
