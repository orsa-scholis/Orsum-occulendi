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

/**
 * Hauptinterface für Krypto-Services
 * @author Lukas
 *
 */
public class CryptoEngine {
	public static final String BASE_KEY_DIR = "save/";
	
	private byte[] key;
	private CryptoEngineEnvType type;
	private String privateKeyFile;
	private String publicKeyFile;
	private KeyPair keyPair;
	
	/**
	 * Erstellt einen neuen Kryptographiemotor
	 * @param type	Der Typ. Kann entweder Server oder Client sein (Wichtig für RSA und die Rollenverteilung)
	 */
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
	
	/**
	 * Testet, ob ein Private- und ein Public-Key bereits existieren
	 * @return	Ja/Nein
	 */
	private boolean areKeysPresent() {
		File privateKey = new File(privateKeyFile);
	    File publicKey = new File(publicKeyFile);
	    
	    return privateKey.exists() && publicKey.exists();
	}
	
	/**
	 * Verschlüsselt einen gegebenen Byte-Block mit RSA
	 * @param input	Der Eingabe-Byte-Block
	 * @param publicKey	Der Public-Key
	 * @return	Ein Base64 String mit dem verschlüsselten Block
	 */
	public String rsaEncrypt(byte[] input, PublicKey publicKey) {
		byte[] encrypted = RSAUtil.encrypt(input, publicKey);
		
		return new String(Base64.getEncoder().encode(encrypted));
	}
	
	/**
	 * Entschlüsselt einen Byte-Block
	 * @param encrypted	Der Eingabe-Byte-Block
	 * @param privateKey Der Private-Key
	 * @return	Die entschlüsselte Nachricht
	 */
	public byte[] rsaDecrypt(String encrypted, PrivateKey privateKey) {
		byte[] input = Base64.getDecoder().decode(encrypted.getBytes());
		return RSAUtil.decrypt(input, privateKey);
	}
	
	/**
	 * Verschlüsselt einen String mit AES
	 * @param input	Der String, der verschlüsselt werden soll
	 * @return	Ein Base64 String mit dem verschlüsselten Output
	 */
	public String encrypt(String input) {
		AES aes = new AES(input.getBytes(), key);
		aes.encrypt();
		
		if (aes.getError() != AESError.noErr) {
			System.out.println("Can't encrypt: " + aes.getError());
			return null;
		}
		
		return new String(Base64.getEncoder().encode(aes.getOutput()));
	}
	
	/**
	 * Entschlüsselt einen String mit AES
	 * @param encrypted	Der Base64 enkodierte, verschlüsselte String
	 * @return	Der entschlüsselte String
	 */
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
	
	public byte[] generateRandomAESKey() {
		this.key = KeyGen.genAESKey();
		return this.key;
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}
}
