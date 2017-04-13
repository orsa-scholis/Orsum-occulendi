package crypto.rsa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSAUtil {
	/**
	   * String to hold name of the encryption algorithm.
	   */
	  public static final String ALGORITHM = "RSA";

	  /**
	   * Generate key which contains a pair of private and public key using 1024
	   * bytes. Store the set of keys in Prvate.key and Public.key files.
	   *
	   * @throws NoSuchAlgorithmException
	   * @throws IOException
	   * @throws FileNotFoundException
	   */
	  public static KeyPair generateKey(String publicKeyFilePath, String privateKeyFilePath) {
		  try {
			  final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
			  keyGen.initialize(1024);
			  final KeyPair key = keyGen.generateKeyPair();

			  File privateKeyFile = new File(privateKeyFilePath);
			  File publicKeyFile = new File(publicKeyFilePath);
			  
			  // Create files to store public and private key
		      if (privateKeyFile.getParentFile() != null) {
		    	  privateKeyFile.getParentFile().mkdirs();
		      }
		      privateKeyFile.createNewFile();

		      if (publicKeyFile.getParentFile() != null) {
		    	  publicKeyFile.getParentFile().mkdirs();
		      }
		      publicKeyFile.createNewFile();

		      // Saving the Public key in a file
		      ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
		      publicKeyOS.writeObject(key.getPublic());
		      publicKeyOS.close();

		      // Saving the Private key in a file
		      ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
		      privateKeyOS.writeObject(key.getPrivate());
		      privateKeyOS.close();
		      
		      return key;
		  } catch (Exception e) {
			  e.printStackTrace();
			  
			  return null;
		  }
	  }

	  /**
	   * Encrypt the plain text using public key.
	   *
	   * @param text
	   *          : original plain text
	   * @param key
	   *          :The public key
	   * @return Encrypted text
	   * @throws java.lang.Exception
	   */
	  public static byte[] encrypt(String text, PublicKey key) {
	    byte[] cipherText = null;
	    try {
	    	// get an RSA cipher object and print the provider
	    	final Cipher cipher = Cipher.getInstance(ALGORITHM);
	    	// encrypt the plain text using the public key
	    	cipher.init(Cipher.ENCRYPT_MODE, key);
	    	cipherText = cipher.doFinal(text.getBytes());
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return cipherText;
	  }

	  /**
	   * Decrypt text using private key.
	   *
	   * @param text
	   *          :encrypted text
	   * @param key
	   *          :The private key
	   * @return plain text
	   * @throws java.lang.Exception
	   */
	  public static String decrypt(byte[] text, PrivateKey key) {
		  byte[] dectyptedText = null;
		  try {
			// get an RSA cipher object and print the provider
		    final Cipher cipher = Cipher.getInstance(ALGORITHM);

		    // decrypt the text using the private key
		    cipher.init(Cipher.DECRYPT_MODE, key);
		    dectyptedText = cipher.doFinal(text);
		      
		  } catch (Exception ex) {
			  ex.printStackTrace();
		  }

		  return new String(dectyptedText);
	  }
	  
	  public static PrivateKey loadPrivateKey(String privateKeyFilePath) throws FileNotFoundException, IOException, ClassNotFoundException {
		  ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(privateKeyFilePath));
	      final PrivateKey privKey = (PrivateKey) inputStream.readObject();
	      inputStream.close();
	      
	      return privKey;
	  }
	  
	  public static PublicKey loadPublicKey(String publicKeyFilePath) throws FileNotFoundException, IOException, ClassNotFoundException {
		  ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(publicKeyFilePath));
	      final PublicKey pubKey = (PublicKey) inputStream.readObject();
	      inputStream.close();
	      
	      return pubKey;
	  }
	  
	  /** Write the object to a Base64 string. */
		public static String exportKey(PrivateKey kPrivateKey) throws IOException {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(baos);
		    oos.writeObject(kPrivateKey);
		    oos.close();
		    return Base64.getEncoder().encodeToString(baos.toByteArray()); 
		}
		
		public static String exportKey(PublicKey pKey) throws IOException {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(baos);
		    oos.writeObject(pKey);
		    oos.close();
		    return Base64.getEncoder().encodeToString(baos.toByteArray()); 
		}
		
		public static PrivateKey privateKeyFromString(String exported) throws IOException, ClassNotFoundException {
			byte [] data = Base64.getDecoder().decode(exported);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			PrivateKey o = (PrivateKey)ois.readObject();
			ois.close();
			return o;
		}
		
		public static PublicKey publicKeyFromString(String exported) throws IOException, ClassNotFoundException {
			byte [] data = Base64.getDecoder().decode(exported);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			PublicKey o = (PublicKey)ois.readObject();
			ois.close();
			return o;
		}
}
