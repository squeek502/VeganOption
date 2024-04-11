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
import squeek.veganoption.content.registry.DescriptionRegistry;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;

import java.util.ArrayList;
import java.util.List;

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

		String text = DescriptionRegistry.processWikiText(
			textBuilder.toString(),
			topic,
			(referencedItemStack, referenceMatcher, referencedBuffer) -> {
				if (!isItemStackReferenceRedundant(topic, referencedItemStack, referenced, related))
					referenced.add(EntryIngredients.of(referencedItemStack));
				referenceMatcher.appendReplacement(referencedBuffer, wrapItemNameInFormat(referencedItemStack, REF_COLOR_LIGHT));
			},
			(referencedFluid, referenceMatcher, referencedBuffer) -> {
				if (!isFluidReferenceRedundant(referencedFluid, referenced))
					referenced.add(EntryIngredients.of(referencedFluid));
				referenceMatcher.appendReplacement(referencedBuffer, LangHelper.wrapInFormat(referencedFluid.getFluidType().getDescriptionId(), REF_COLOR_LIGHT));
			});

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
	public static String formattedTopicName(ItemStack topic)
	{
		return wrapItemNameInFormat(topic, TOPIC_COLOR_LIGHT);
	}

	public static String wrapItemNameInFormat(ItemStack item, ChatFormatting format)
	{
		return LangHelper.wrapInFormat(item.getDescriptionId(), format);
	}

	public static String getCraftingOfItem(ItemStack topic)
	{
		String key = DescriptionRegistry.getCraftingKey(topic);
		if (LangHelper.existsRaw(key))
			return LangHelper.translateRaw(key, formattedTopicName(topic));
		return "";
	}

	public static String getUsageOfItem(ItemStack topic)
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
