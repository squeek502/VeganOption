package squeek.veganoption.helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import squeek.veganoption.ModInfo;

public class LangHelper
{
	public static String prependModId(String identifier)
	{
		return ModInfo.MODID_LOWER + "." + identifier;
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
		return I18n.get(key);
	}

	public static String translateRaw(String key, Object... args)
	{
		return I18n.get(key, args);
	}

	public static boolean existsRaw(String key)
	{
		return I18n.exists(key);
	}

	public static String contextString(String format, String context, Object... params)
	{
		return translate(format + ".format", translate("context." + context + ".title", params), translate("context." + context + ".value", params), params);
	}

	/**
	 * Generates a typical gray-colored tooltip line from item.veganoption.item_name.tooltip, for use in Item#appendHoverText.
	 * @param item The name of the item
	 * @return A translatable component of item.veganoption.{item}.tooltip, with the color set to gray.
	 */
	public static Component tooltip(String item)
	{
		return Component.translatable("item." + ModInfo.MODID_LOWER + "." + item + ".tooltip").withStyle(ChatFormatting.GRAY);
	}
}
