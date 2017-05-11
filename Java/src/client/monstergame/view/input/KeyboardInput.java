package client.monstergame.view.input;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
*
* Diese Klasse kümmert sich darum, dass man KeyboardInputs abfangen und verwerten kann.
*
*/
public class KeyboardInput extends GLFWKeyCallback {

	public static boolean[] keys = new boolean[65536];

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		keys[key] = action != GLFW_RELEASE;

	}

	public static boolean isKeyDown(int keycode){
		return keys[keycode];
	}

}
