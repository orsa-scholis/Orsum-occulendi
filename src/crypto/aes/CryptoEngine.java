package crypto.aes;

import java.util.Base64;

public class CryptoEngine {
	private byte[] key;
	
	public CryptoEngine(byte[] key) {
		super();
		this.key = key;
	}
	
	public String encrypt(String input) {
		AES aes = new AES(input.getBytes(), key);
		aes.encrypt();
		
		if (aes.getError() != AESError.noErr) {
			System.out.println("Can't encrypt: " + aes.getError());
			return null;
		}
		
		return new String(Base64.getEncoder().encode(aes.getOutput()));
	}
	
	public String decrypt(String encrypted) {
		byte[] input = Base64.getDecoder().decode(encrypted.getBytes());
		AES aes = new AES(input, key);
		aes.decrypt();
		
		if (aes.getError() != AESError.noErr) {
			System.out.println("Can't decrypt: " + aes.getError());
			return null;
		}
		
		return new String(aes.getOutput());
	}
}
