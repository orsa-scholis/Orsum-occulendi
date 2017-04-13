package crypto.rsa;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyGen {
	public static byte[] genAESKey() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			SecretKey secretKey = keyGen.generateKey();
			
			return secretKey.getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
}
