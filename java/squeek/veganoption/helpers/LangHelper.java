package squeek.veganoption.helpers;

import net.minecraft.util.StatCollector;
import squeek.veganoption.ModInfo;

public class LangHelper
{
	public static String prependModId(String identifier)
	{
		return ModInfo.MODID + "." + identifier;
	}

	public static String translate(String identifier)
	{
		return translateRaw(prependModId(identifier));
	}

	public static String translate(String identifier, Object... args)
	{
		return translateRaw(prependModId(identifier), args);
	}

	public static boolean exists(String identifier)
	{
		return existsRaw(prependModId(identifier));
	}

	public static String translateRaw(String key)
	{
		return StatCollector.translateToLocal(key);
	}

	public static String translateRaw(String key, Object... args)
	{
		return StatCollector.translateToLocalFormatted(key, args);
	}

	public static boolean existsRaw(String key)
	{
		return StatCollector.canTranslate(key);
	}
}
