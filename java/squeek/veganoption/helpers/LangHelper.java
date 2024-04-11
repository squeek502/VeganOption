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

	/**
	 * Wraps each localized word in the provided lang key with the provided formatting code and a reset code.
	 * <br/>
	 * This is needed to prevent formatting from carrying through entire lines when the item name is split on a line break by the font
	 * renderer. Minecraft's native string splitter does not handle that case properly.
	 * <br/>
	 * Example: <code>wrapInFormat(Items.GOLDEN_APPLE.getDescriptionId(), ChatFormatting.RED)</code> would return the String <code>"§cGolden§r §cApple§r"</code>
	 * <br/>
	 * Due to spaces not being formatted, underline and strikethrough do not appear as would be expected.
	 */
	public static String wrapInFormat(String langKey, ChatFormatting format)
	{
		return format + translateRaw(langKey).replaceAll(" ", ChatFormatting.RESET + " " + format) + ChatFormatting.RESET;
	}
}
