package crypto.aes;

public class AES {
	public String input;
	public String output;
	public byte[] key;
	public AESError error;
	
	public AES(String input, byte[] key) {
		this.input = input;
		this.key = key;
		this.error = AESError.noErr;
	}
	
	public void encrypt() {
		this.output = input; // temporary!!!
	}
	
	public void decrypt() {
		this.output = input; // temp
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public AESError getError() {
		return error;
	}

	public void setError(AESError error) {
		this.error = error;
	}
}
