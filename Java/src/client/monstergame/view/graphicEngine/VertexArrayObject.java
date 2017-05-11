package client.monstergame.view.graphicEngine;

import static client.monstergame.view.utils.Utilities.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
*
* Diese Hilfsklasse steht eine Stufe näher bei dem Grafikprozessor als die Object-Klasse.
*
*/
public class VertexArrayObject {

	public static final int VERTEX_ATTRIB = 0;

	private int vaoID;

	public VertexArrayObject(float[] vertices, byte[] indices) {
		createArrayObject(vertices, indices);
	}

	public void createArrayObject(float[] vertices, byte[] indices) {
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		createVerticesBuffer(vertices);
		createIndicesBuffer(indices);

		glBindVertexArray(0);
	}


	private void createVerticesBuffer(float[] vertices){
		int vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, createFloatBuffer(vertices), GL_STATIC_DRAW);
		glVertexAttribPointer(VERTEX_ATTRIB, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private void createIndicesBuffer(byte[] indices){
		int ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, createByteBuffer(indices), GL_STATIC_DRAW);
	}

	public int getVaoID(){
		return vaoID;
	}

}
