package crypto.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

import crypto.aes.AES;

public class CryptoTestingCLI {
	public void main() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			System.out.println("Encrypt (e) or decrypt (d) or test (t) or quit (q)?");
			String method = br.readLine().toLowerCase();

			if (!method.equals("e") && !method.equals("d") && !method.equals("q") && !method.equals("t")) {
				System.out.println("Unknown method!");
				continue;
			} else if (method.equals("q")) {
				break;
			}

			if (method.equals("t")) {
				test();
			} else {
				System.out.println("Enter text to en/decrypt: ");
				String plaintext = br.readLine();

				System.out.println("Enter key: ");
				String key = br.readLine();

				AES aes = new AES(plaintext.getBytes(), key.getBytes());

				if (method.equals("e")) {
					aes.encrypt();
				} else {
					aes.setInput(Base64.getDecoder().decode(plaintext));
					aes.decrypt();
				}

				System.out.println("Output: ");
				if (method.equals("e")) {
					System.out.println(Base64.getEncoder().encodeToString(aes.getOutput()));
				} else {
					System.out.println(new String(aes.getOutput()));
				}
				System.out.println("----------");
			}
		}
	}

	private void test() {
		/*byte[] input = new byte[] {
				(byte)0x00, (byte)0x11, (byte)0x22, (byte)0x33,
				(byte)0x44, (byte)0x55, (byte)0x66, (byte)0x77,
				(byte)0x88, (byte)0x99, (byte)0xaa, (byte)0xbb,
				(byte)0xcc, (byte)0xdd, (byte)0xee, (byte)0xff
		};*/

		byte[] key = new byte[] {
				(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03,
			    (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
			    (byte)0x08, (byte)0x09, (byte)0x5a, (byte)0x0b,
			    (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f
		};

		AES aes = new AES("Polter".getBytes(), key);
		aes.encrypt();

		System.out.println("Expanded Key: ");
		System.out.println("length: " + aes.getExpandedKey().length);
		StringBuilder sb = new StringBuilder();
	    for (byte b : aes.getExpandedKey()) {
	        sb.append(String.format("%02X ", b).toLowerCase());
	    }
	    System.out.println(sb.toString());

	    System.out.println("Output: ");
		System.out.println("length: " + aes.getOutput().length);
		sb = new StringBuilder();
	    for (byte b : aes.getOutput()) {
	        sb.append(String.format("%02X ", b).toLowerCase());
	    }
	    System.out.println(sb.toString());

		System.out.println("Error: " + aes.getError());
		System.out.println("Output: " + new String(aes.getOutput()));

		System.out.println("---------------------------------------------------");

		aes.setInput(aes.getOutput());
		aes.setKey(key);
		aes.decrypt();

		System.out.println("Output: ");
		System.out.println("length: " + aes.getOutput().length);
		sb = new StringBuilder();
	    for (byte b : aes.getOutput()) {
	        sb.append(String.format("%02X ", b).toLowerCase());
	    }
	    System.out.println(sb.toString());

		System.out.println("Error: " + aes.getError());
		System.out.println("Output: " + new String(aes.getOutput()));

		System.out.println("---------------------------------------------------");

	}
}
