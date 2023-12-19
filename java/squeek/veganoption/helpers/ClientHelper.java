package squeek.veganoption.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ClientHelper
{
	public static Level getLevel()
	{
		return Minecraft.getInstance().level;
	}
}
