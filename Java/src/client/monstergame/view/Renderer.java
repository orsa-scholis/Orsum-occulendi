package client.monstergame.view;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import client.monstergame.logic.Index2D;
import client.monstergame.logic.Quad;
import client.monstergame.logic.graph.Monster;
import client.monstergame.logic.graph.Player;
import client.monstergame.main.Driver;
import client.monstergame.view.controller.Object;
import client.monstergame.view.shaders.Shader;

/**
*
* Diese Klasse zeichnet die einzelnen Elemente, hierbei wird die Hilfsklasse Object verwendet.
* Jedes zu zeichnende Objekt (Monster, Player, Hindernisse) bekommt ein Object, dass an den angegebenen Koordinaten ein Quadrat renderet.
*
*/
public class Renderer {

	private Driver driver;

	public Renderer(Driver driver) {
		this.driver = driver;
	}

	/**
	 * Jeder Quad, sowie der Spieler und das Monster werden gerendert.
	 */
	public void render() {

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		for (Quad quady : getQuads()) {

			Index2D indy = quady.getIndex();

			if (quady.isObstacle()) {
				float x = (float)indy.getX() / (float)driver.getGame().getQuadController().getWidth();
				float y = (float)indy.getY() / (float)driver.getGame().getQuadController().getHeight();
				drawWithShader(driver.getShaderMan().shaderObstacle, calcVertices(getFieldSize(), x, y));
			} else {
				float x = (float)indy.getX() / (float)driver.getGame().getQuadController().getWidth();
				float y = (float)indy.getY() / (float)driver.getGame().getQuadController().getHeight();
				(new Object(calcVertices(getFieldSize(), x, y))).draw();
			}
		}

		Player player = driver.getGame().getPlayer();
		float playerSize = 0.05f;
		drawWithShader(driver.getShaderMan().shaderPlayer, calcVertices(playerSize, player.getPoint().getFX(), player.getPoint().getFY()));

		Monster monster = driver.getGame().getMonster();
		float monsterSize = 0.05f;
		drawWithShader(driver.getShaderMan().shaderMonster, calcVertices(monsterSize, monster.getPoint().getFX(), monster.getPoint().getFY()));


	}

	/**
	 * Rendert ein Object mit einem Shader.
	 * @param shader Den Shader denn man gerne h�tte.
	 * @param vertices Die vertices die das Object haben soll.
	 */
	private void drawWithShader(Shader shader, float[] vertices){
		Object object = new Object(vertices);

		shader.start();
		shader.setUniform3f("pos", object.position);
		object.draw();
		shader.stop();
	}

	/**
	 * Berechnet die Vertices.
	 * @param size Die Gr�sse die das Object haben soll.
	 * @param x Die X-Koordinate des Object.
	 * @param y Die Y-Koordinate des Object.
	 * @return float[], die berechneten Vertices.
	 */
	private float[] calcVertices(float size, float x, float y){

		float px = (x * 2.0f) - 1.0f;
		float py = (y * 2.0f) - 1.0f;

		float[] vertices = {
				px, py + size, 0f,
				px, py, 0f,
				px + size, py, 0f,
				px + size, py + size, 0f
		};

		return vertices;
	}

	private float getFieldSize() {
		return 2.0f / driver.getGame().getQuadController().getWidth();
	}

	private ArrayList<Quad> getQuads() {
		return driver.getGame().getQuadController().getQuads();
	}
}
