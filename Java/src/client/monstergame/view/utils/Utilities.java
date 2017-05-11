package client.monstergame.view.utils;

import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.annotation.Resource;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
*
* Dies ist eine OpenGl-Hilfsklasse.
*
*/
public class Utilities {

	public static int loadShader(InputStream input, int type){
		BufferedReader reader = null;
		StringBuilder result = new StringBuilder();
		try{
			reader = new BufferedReader(new InputStreamReader(input));
			String buffer = "";
			while((buffer = reader.readLine()) != null){
				result.append(buffer);
				result.append("\n");
			}
		}
		catch( IOException e){
			e.printStackTrace();
		}
		finally{
			try {
				reader.close();
			} catch (IOException e) {
				System.out.println("The reader never existed!");
			}
		}
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, result.toString());
		glCompileShader(shaderID);
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE){
			System.err.println(glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader.");
		}
		return shaderID;
	}

	public static FloatBuffer createFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public static ByteBuffer createByteBuffer(byte[] data){
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
