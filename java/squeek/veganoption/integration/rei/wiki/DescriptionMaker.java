package squeek.veganoption.integration.rei.wiki;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import squeek.veganoption.content.registry.DescriptionRegistry;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DescriptionMaker
{
	public static final Font FONT = Minecraft.getInstance().font;
	public static final int DESC_DISPLACEMENT = 3;
	public static final int WIDTH = 160;
	public static final int HEIGHT = 140;
	public static final int Y_START = 22;
	public static final int PADDING = 4;
	public static final int MAX_LINES_PER_PAGE = (HEIGHT - Y_START) / FONT.lineHeight - 1;
	public static final ChatFormatting REF_COLOR_LIGHT = ChatFormatting.DARK_BLUE;
	public static final ChatFormatting REF_COLOR_DARK = ChatFormatting.AQUA;
	public static final ChatFormatting TOPIC_COLOR_LIGHT = ChatFormatting.BLACK;
	public static final ChatFormatting TOPIC_COLOR_DARK = ChatFormatting.WHITE;

	public List<DescriptionDisplay> createDisplays(ItemStack topic)
	{
		List<DescriptionDisplay> pages = new ArrayList<>();
		List<ItemStack> related = getRelatedItems(topic);
		List<EntryIngredient> referenced = new ArrayList<>();
		int firstPageMaxLines = MAX_LINES_PER_PAGE;

		StringBuilder textBuilder = new StringBuilder();
		textBuilder.append(getText(topic));

		for (ItemStack relatedStack : related)
		{
			String relatedText = getRelatedText(relatedStack);
			if (!relatedText.isEmpty() && !textBuilder.isEmpty())
				textBuilder.append("\n\n");
			textBuilder.append(relatedText);
		}

		String text = processText(textBuilder.toString(), topic, referenced, related);

		if (!related.isEmpty())
			firstPageMaxLines -= DESC_DISPLACEMENT;
		if (!referenced.isEmpty())
			firstPageMaxLines -= DESC_DISPLACEMENT;

		List<FormattedCharSequence> splitText = splitText(text, FONT, WIDTH - PADDING * 2);

		for (int page = 0; page < getNumPages(splitText, MAX_LINES_PER_PAGE, firstPageMaxLines); page++)
		{
			int startingLineIndex = getStartingLine(page, MAX_LINES_PER_PAGE, firstPageMaxLines);
			int endingLineIndex = Math.min(splitText.size(), getStartingLine(page + 1, MAX_LINES_PER_PAGE, firstPageMaxLines));
			pages.add(newDisplay(topic, related, referenced, text, startingLineIndex, endingLineIndex, page == 0));
		}

		return pages;
	}

	public String processText(String text, ItemStack topic, List<EntryIngredient> referenced, List<ItemStack> related)
	{
		if (text == null)
			return null;

		text = text.replaceAll("\\\\n", String.valueOf('\n'));

		// {unlocalized.string.name} looks up the localized string
		Matcher localizationMatcher = Pattern.compile("\\{([^\\}]+)\\}").matcher(text);
		StringBuffer localizedBuffer = new StringBuffer(text.length());
		while (localizationMatcher.find())
		{
			localizationMatcher.appendReplacement(localizedBuffer, LangHelper.translateRaw(localizationMatcher.group(1), formattedTopicName(topic)));
		}
		localizationMatcher.appendTail(localizedBuffer);
		text = localizedBuffer.toString();

		// [[mod:item_name]] references an item/block/fluid
		Matcher referenceMatcher = Pattern.compile("\\[\\[([^\\]:]+:[^\\]:]+)\\]\\]").matcher(text);
		StringBuffer referencedBuffer = new StringBuffer(text.length());
		while (referenceMatcher.find())
		{
			String objectName = referenceMatcher.group(1);
			ItemStack referencedItemStack = MiscHelper.getItemStackByObjectName(objectName);
			if (!referencedItemStack.isEmpty())
			{
				if (!isItemStackReferenceRedundant(topic, referencedItemStack, referenced, related))
					referenced.add(EntryIngredients.of(referencedItemStack));
				referenceMatcher.appendReplacement(referencedBuffer, wrapItemNameInFormat(referencedItemStack, REF_COLOR_LIGHT));
			}
			else
			{
				Fluid referencedFluid = MiscHelper.getFluidByObjectName(objectName);
				if (referencedFluid != Fluids.EMPTY)
				{
					if (!isFluidReferenceRedundant(referencedFluid, referenced))
						referenced.add(EntryIngredients.of(referencedFluid));
					referenceMatcher.appendReplacement(referencedBuffer, wrapInFormat(referencedFluid.getFluidType().getDescriptionId(), REF_COLOR_LIGHT));
				}
			}
		}
		referenceMatcher.appendTail(referencedBuffer);
		text = referencedBuffer.toString();

		return text;
	}

	public static List<FormattedCharSequence> splitText(String text, Font fontRenderer, int maxWidth)
	{
		if (text == null)
			return null;

		return new ArrayList<>(fontRenderer.split(FormattedText.of(text), maxWidth));
	}


	public int getNumPages(List<FormattedCharSequence> splitText, int maxLinesPerPage, int firstPageMaxLines)
	{
		return splitText.size() > firstPageMaxLines ? 1 + Mth.ceil((splitText.size() - firstPageMaxLines) / (float) maxLinesPerPage) : 1;
	}

	public int getStartingLine(int page, int maxLinesPerPage, int firstPageMaxLines)
	{
		if (page == 0)
			return 0;
		else
			return firstPageMaxLines + (page - 1) * maxLinesPerPage;
	}

	public abstract List<ItemStack> getRelatedItems(ItemStack topic);

	public abstract String getText(ItemStack topic);

	public abstract String getRelatedText(ItemStack topic);

	public abstract DescriptionDisplay newDisplay(ItemStack topic, List<ItemStack> related, List<EntryIngredient> referenced, String text, int startingLineIndex, int endingLineIndex, boolean isFirstPage);

	/**
	 * Formats the provided item name, with formatting for the description topic.
	 */
	public String formattedTopicName(ItemStack topic)
	{
		return wrapItemNameInFormat(topic, TOPIC_COLOR_LIGHT);
	}

	public String wrapItemNameInFormat(ItemStack item, ChatFormatting format)
	{
		return wrapInFormat(item.getDescriptionId(), format);
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
	public String wrapInFormat(String langKey, ChatFormatting format)
	{
		return format + LangHelper.translateRaw(langKey).replaceAll(" ", ChatFormatting.RESET + " " + format) + ChatFormatting.RESET;
	}

	public String getCraftingOfItem(ItemStack topic)
	{
		String key = DescriptionRegistry.getCraftingKey(topic);
		if (LangHelper.existsRaw(key))
			return LangHelper.translateRaw(key, formattedTopicName(topic));
		return "";
	}

	public String getUsageOfItem(ItemStack topic)
	{
		String key = DescriptionRegistry.getUsageKey(topic);
		if (LangHelper.existsRaw(key))
			return LangHelper.translateRaw(key, formattedTopicName(topic));
		return "";
	}

	public boolean isItemStackReferenceRedundant(ItemStack topic, ItemStack referencedItem, List<EntryIngredient> referenced, List<ItemStack> related)
	{
		return referencedItem.getItem() == topic.getItem() || referenced.contains(EntryIngredients.of(referencedItem)) || MiscHelper.getMatchingItemFromStackList(related, referencedItem.getItem()) != null;
	}

	public boolean isFluidReferenceRedundant(Fluid referencedFluid, List<EntryIngredient> referenced)
	{
		return referenced.contains(EntryIngredients.of(referencedFluid));
	}
}
