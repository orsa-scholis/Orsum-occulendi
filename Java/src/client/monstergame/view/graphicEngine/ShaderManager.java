package client.monstergame.view.graphicEngine;

import client.monstergame.view.shaders.Shader;

/**
*
* Diese Klasse kÃ¼mmert sich um das Verwalten von Shadern.
*
* Wir haben hier 3 Shader erstellt:
* Den shaderObstacle, dieser wird fÃ¼r die Hindernisse verwendet. Er fÃ¤rbt das Feld schwarz.
* Den shaderPlayer, dieser wird fÃ¼r den Spieler verwendet. Er fÃ¤rbt das Feld blau.
* Den shaderMonster, dieser wird fÃ¼r das Monster verwendet. Er fÃ¤rbt das Feld rot.
*
*/
public class ShaderManager {

	private static ShaderManager instance;

	public final Shader shaderObstacle;
	public final Shader shaderPlayer;
	public final Shader shaderMonster;

	public static ShaderManager getInstance(){
		if(ShaderManager.instance == null){
			ShaderManager.instance = new ShaderManager();
		}
		return ShaderManager.instance;
	}

	private ShaderManager(){
		shaderObstacle = new Shader("/view/shaders/vertex.shader", "/view/shaders/fragment.shader");
		shaderPlayer = new Shader("/view/shaders/vertexPlayer.shader", "/view/shaders/fragment.shader");
		shaderMonster = new Shader("/view/shaders/vertexMonster.shader", "/view/shaders/fragment.shader");
	}



}
