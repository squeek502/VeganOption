package squeek.veganoption.helpers;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyHelper
{
	public static boolean isCtrlKeyDown()
	{
		// prioritize CONTROL, but allow OPTION as well on Mac (note: GuiScreen's isCtrlKeyDown only checks for the OPTION key on Mac)
		boolean isCtrlKeyDown = isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
		if (!isCtrlKeyDown && Minecraft.ON_OSX)
			isCtrlKeyDown = isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);

		return isCtrlKeyDown;
	}

	public static boolean isShiftKeyDown()
	{
		return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

	public static boolean isKeyDown(int key)
	{
		return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key) == GLFW.GLFW_PRESS;
	}
}
