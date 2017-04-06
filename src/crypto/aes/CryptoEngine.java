package crypto.aes;

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
		
		return new String(aes.getOutput());
	}
	
	public String decrypt(String encrypted) {
		AES aes = new AES(encrypted.getBytes(), key);
		aes.decrypt();
		
		if (aes.getError() != AESError.noErr) {
			System.out.println("Can't decrypt: " + aes.getError());
			return null;
		}
		
		return new String(aes.getOutput());
	}
}
