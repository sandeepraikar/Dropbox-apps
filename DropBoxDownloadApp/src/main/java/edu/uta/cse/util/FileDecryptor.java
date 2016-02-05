package edu.uta.cse.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

public class FileDecryptor {

	private static Logger LOGGER = LoggerFactory.getLogger(FileDecryptor.class);

	public static void decrypt(String fileName) {

		LOGGER.info("Requested File Name : "+fileName);

		LOGGER.info("insude decrypt : "+fileName);
		String password = Constants.PASSWORD;
		
		try {
			// reading the salt
			// user should have secure mechanism to transfer the
			// salt, iv and password to the recipient
			FileInputStream saltFis = new FileInputStream(Constants.SALT_IV_DIRECTORY+"/"+"salt.enc");
			byte[] salt = new byte[8];
			saltFis.read(salt);
			saltFis.close();

			// reading the iv
			FileInputStream ivFis = new FileInputStream(Constants.SALT_IV_DIRECTORY+"/"+"iv.enc");
			byte[] iv = new byte[16];
			ivFis.read(iv);
			ivFis.close();

			SecretKeyFactory factory = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
					65536, 256);
			SecretKey tmp = factory.generateSecret(keySpec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			// file decryption
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			FileInputStream fis = new FileInputStream(Constants.DECRYPT_TEMP_FOLDER+"/"+fileName);
			FileOutputStream fos = new FileOutputStream(Constants.DROPBOX_DOWNLOAD_FOLDER+"/"+fileName);
			byte[] in = new byte[64];
			int read;
			while ((read = fis.read(in)) != -1) {
				byte[] output = cipher.update(in, 0, read);
				if (output != null)
					fos.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				fos.write(output);
			fis.close();
			fos.flush();
			fos.close();
			LOGGER.info("File Decrypted Successfully!!");
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
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
