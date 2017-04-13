package crypto;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

import crypto.aes.*;
import crypto.rsa.*;

public class CryptoEngine {
	public static final String BASE_KEY_DIR = "save/";
	
	private byte[] key;
	private CryptoEngineEnvType type;
	private String privateKeyFile;
	private String publicKeyFile;
	private KeyPair keyPair;
	
	public CryptoEngine(CryptoEngineEnvType type) {
		super();
		this.type = type;
		setup();
		
		if (!areKeysPresent()) {
			generatePrivPubKey();
		} else {
			try {
				keyPair = new KeyPair(RSAUtil.loadPublicKey(publicKeyFile), RSAUtil.loadPrivateKey(privateKeyFile));
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void setup() {
		switch (type) {
		case client:
			privateKeyFile = BASE_KEY_DIR + "client/private.key";
			publicKeyFile = BASE_KEY_DIR + "client/public.key";
			break;
			
		case server:
			privateKeyFile = BASE_KEY_DIR + "server/private.key";
			publicKeyFile = BASE_KEY_DIR + "server/public.key";
			break;
		}
	}
	
	private void generatePrivPubKey() {
		keyPair = RSAUtil.generateKey(publicKeyFile, privateKeyFile);
	}
	
	private boolean areKeysPresent() {
		File privateKey = new File(privateKeyFile);
	    File publicKey = new File(publicKeyFile);
	    
	    return privateKey.exists() && publicKey.exists();
	}
	
	public String rsaEncrypt(String input, PublicKey publicKey) {
		byte[] encrypted = RSAUtil.encrypt(input, publicKey);
		
		return new String(Base64.getEncoder().encode(encrypted));
	}
	
	public byte[] rsaDecrypt(String encrypted, PrivateKey privateKey) {
		byte[] input = Base64.getDecoder().decode(encrypted.getBytes());
		return RSAUtil.decrypt(input, privateKey);
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
		
		byte[] aesOutput = aes.getOutput();
		ArrayList<Byte> output = new ArrayList<>();
		for (int i = 0; i < aesOutput.length; ++i) {
			output.add(aesOutput[i]);
		}
		
		for (int i = output.size() - 1; i >= 0; i--) {
			if (output.get(i) == 0x0) {
				output.remove(i);
			} else {
				break;
			}
		}
		
		byte[] finalOutput = new byte[output.size()];
		for (int i = 0; i < output.size(); i++) {
			finalOutput[i] = output.get(i);
		}
		return new String(finalOutput);
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}
	
	public String exportPublicKey() throws IOException {
		return RSAUtil.exportKey(this.keyPair.getPublic());
	}
	
	public PublicKey publicKeyFromString(String encoded) throws ClassNotFoundException, IOException {
		return RSAUtil.publicKeyFromString(encoded);
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}
}
