package client.monstergame.main;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Platform;

import client.monstergame.logic.Game;
import client.monstergame.logic.graph.Point;
import client.monstergame.view.Renderer;
import client.monstergame.view.controller.Movement;
import client.monstergame.view.controller.Updater;
import client.monstergame.view.graphicEngine.ShaderManager;
import client.monstergame.view.input.KeyboardInput;
import client.monstergame.view.input.MouseInput;

/**
 *
 * Dies ist die Main-Klasse.
 * Sie regelt das Initialisieren und verwalten aller f�r das Programm wichtige Objekte.
 *
 * @author Philipp
 *
 */
public class Driver {

	private boolean running = false;
	private long windows;
	private int height = 400;
	private int width = 400;
	@SuppressWarnings("unused")
	private GLFWKeyCallback keyCallback;
	@SuppressWarnings("unused")
	private GLFWCursorPosCallback cursorCallback;

	private Game game;
	private Movement move;
	private ShaderManager shaderMan;
	private Renderer rend;
	private Updater user;

    public static int FIELD = 0;
    public static int OBSTACLE = 1;

    /**
     * Setup aller ben�tigten Objekten und statischen Elemente.
     */
	public void init(){

		int[][] field = new int[][] {
            { FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD },
            { FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD },
            { FIELD, FIELD, OBSTACLE,FIELD, FIELD, FIELD, FIELD, FIELD },
            { FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD },
            { FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD },
            { FIELD, OBSTACLE,OBSTACLE,FIELD, FIELD, OBSTACLE, FIELD, FIELD },
            { FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD },
            { FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD, FIELD }
		};

		game = new Game(field);
		game.setMonsterPosition(new Point(0.4, 0.82));
		move = new Movement(this);
		rend = new Renderer(this);
		user = new Updater(this);
		running = true;
	}

	/**
	 * Setup von OpenGl und der GUI.
	 */
	public void graficInit(){
		if (Platform.get() == Platform.MACOSX) {
			java.awt.Toolkit.getDefaultToolkit();
		}

		if(!glfwInit()){
			System.err.println("Initialisierung fehlgeschlagen!");
		}

		GLFWErrorCallback.createPrint(System.err).set();
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
		windows = glfwCreateWindow(width, height, "Monstergame", NULL, NULL);

		if(windows == NULL){
			System.err.println("Fenster konnte nicht erstellt werden!");
		}

		glfwSetKeyCallback(windows, keyCallback = new KeyboardInput());
		glfwSetCursorPosCallback(windows, cursorCallback = new MouseInput());
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(
			windows,
			(vidmode.width() - width) / 2,
			(vidmode.height() - height) / 2
		);

		glfwMakeContextCurrent(windows);
		glfwSwapInterval(1);
		glfwShowWindow(windows);
		GL.createCapabilities();
		glViewport(0, 0, width, height);
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		System.out.println("OpenGL version " + glGetString(GL_VERSION));
		shaderMan = ShaderManager.getInstance();
	}

	/**
	 * Der eigentliche "Kontroller", ruft 30 mal pro Sekunde update() und render() auf.
	 */
	@SuppressWarnings("unused")
	public void run(){
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 30.0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;

		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if(delta >= 1.0){
				update();
				updates++;
				delta--;
			}
			render();
			frames++;
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				//System.out.println(updates + " UPS, " + frames + " FPS");
			}
			if(glfwWindowShouldClose(windows)){
				running = false;
			}
		}

		glfwFreeCallbacks(windows);
		glfwDestroyWindow(windows);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	/**
	 * Ruft die udate() Methode von Updater auf.
	 */
	private void update(){
		glfwPollEvents();
		user.update();
	}

	/**
	 * Ruft die render() Methode von Renderer auf.
	 */
	private void render(){
		glfwSwapBuffers(windows);
		glClear(GL_COLOR_BUFFER_BIT);
		rend.render();
	}

	/**
	 * Die Main-Methode.
	 *
	 */
	public static void main(String[] args) {
		Driver driver = new Driver();
		driver.init();
		driver.graficInit();
		driver.run();
	}

	public Game getGame() {
		return game;
	}

	public ShaderManager getShaderMan(){
		return shaderMan;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight(){
		return height;
	}

	public Movement getMove() {
		return move;
	}
}
