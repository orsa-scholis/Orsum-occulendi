package crypto.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import crypto.aes.AES;

public class CryptoTestingCLI {
	public void main() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			System.out.println("Encrypt (e) or decrypt (d) or quit (q)?");
			String method = br.readLine().toLowerCase();

			if (!method.equals("e") && !method.equals("d") && !method.equals("q")) {
				System.out.println("Unknown method!");
				continue;
			} else if (method.equals("q")) {
				break;
			}

			System.out.println("Enter text to en/decrypt: ");
			String plaintext = br.readLine();

			System.out.println("Enter key: ");
			String key = br.readLine();

			AES aes = new AES(plaintext.getBytes(), key.getBytes());

			if (method.equals("e")) {
				aes.encrypt();
			} else {
				aes.decrypt();
			}

			System.out.println("Output: ");
			System.out.println(new String(aes.output));
			System.out.println("----------");
		}
	}
}
