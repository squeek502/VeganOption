package squeek.veganoption.helpers;

import net.minecraft.util.StatCollector;
import squeek.veganoption.ModInfo;

public class LangHelper
{
	public static String unlocalized(String identifier)
	{
		return ModInfo.MODID + "." + identifier;
	}

	public static String translate(String identifier)
	{
		return StatCollector.translateToLocal(unlocalized(identifier));
	}

	public static String translate(String identifier, Object... args)
	{
		return StatCollector.translateToLocalFormatted(unlocalized(identifier), args);
	}
}
