package chasqui.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

@SuppressWarnings("restriction")
public class Encrypter {

	private String ALGORITHM;
	private String KEY;

	public String encrypt(String value) throws Exception {
		Key key = generateKey();
		Cipher cipher = Cipher.getInstance(this.ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
		String encryptedValue64 = Base64.getMimeEncoder().encodeToString(encryptedByteValue);
		return encryptedValue64;

	}

	public String decrypt(String value) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Key key = generateKey();
		Cipher cipher = Cipher.getInstance(this.ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedValue64 = Base64.getDecoder().decode(value);
		byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
		String decryptedValue = new String(decryptedByteValue, "utf-8");
		return decryptedValue;

	}

	public String encryptURL(String value) throws Exception {
		Key key = generateKey();
		Cipher cipher = Cipher.getInstance(this.ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
		String encryptedValue64 = Base64.getMimeEncoder().encodeToString(encryptedByteValue);
		return encryptedValue64.replace('/', '_');

	}

	public String decryptURL(String value) throws Exception {
		Key key = generateKey();
		Cipher cipher = Cipher.getInstance(this.ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedValue64 = Base64.getDecoder().decode(value.replace('_', '/'));
		byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
		String decryptedValue = new String(decryptedByteValue, "utf-8");
		return decryptedValue;

	}

	private Key generateKey() {
		Key key = new SecretKeySpec(this.KEY.getBytes(), this.ALGORITHM);
		return key;
	}

	public String getALGORITHM() {
		return ALGORITHM;
	}

	public void setALGORITHM(String aLGORITHM) {
		ALGORITHM = aLGORITHM;
	}

	public String getKEY() {
		return KEY;
	}

	public void setKEY(String kEY) {
		KEY = kEY;
	}

}
