package crypto.aes;

public class AES {
	public byte[] input;
	public byte[] output;
	public byte[] key;
	public AESError error;
	
	public AES(byte[] input, byte[] key) {
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

	public byte[] getInput() {
		return input;
	}

	public void setInput(byte[] input) {
		this.input = input;
	}

	public byte[] getOutput() {
		return output;
	}

	public void setOutput(byte[] output) {
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
