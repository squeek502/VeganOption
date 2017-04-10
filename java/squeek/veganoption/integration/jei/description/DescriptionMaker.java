package squeek.veganoption.integration.jei.description;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static squeek.veganoption.integration.jei.description.DescriptionCategory.MAX_LINES_PER_PAGE;

public abstract class DescriptionMaker
{
	private static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
	public static final int DESC_DISPLACEMENT_RELATED = 3;
	public static final int DESC_DISPLACEMENT_REFERENCED = 3;

	public <T extends DescriptionWrapper> List<T> create(Class<T> clazz, ItemStack itemStack)
	{
		List<T> pages = new ArrayList<T>();
		List<ItemStack> related = getRelated(itemStack);
		List<ItemStack> referenced = new ArrayList<ItemStack>();
		int firstPageMaxLines = MAX_LINES_PER_PAGE;

		StringBuilder textBuilder = new StringBuilder();
		textBuilder.append(getText(itemStack));

		for (ItemStack relatedStack : related)
		{
			String relatedText = getRelatedText(relatedStack);
			if (!relatedText.isEmpty() && textBuilder.length() > 0)
				textBuilder.append("\n\n");
			textBuilder.append(relatedText);
		}

		String text = processText(itemStack, textBuilder.toString(), referenced, related);

		if (!related.isEmpty())
			firstPageMaxLines -= DESC_DISPLACEMENT_RELATED;
		if (!referenced.isEmpty())
			firstPageMaxLines -= DESC_DISPLACEMENT_REFERENCED;

		List<String> splitText = splitText(text, fontRenderer, DescriptionCategory.WIDTH - DescriptionCategory.PADDING * 2);

		for (int page = 0; page < getNumPages(splitText, DescriptionCategory.MAX_LINES_PER_PAGE, firstPageMaxLines); page++)
		{
			int startingLineIndex = getStartingLine(page, DescriptionCategory.MAX_LINES_PER_PAGE, firstPageMaxLines);
			int endingLineIndex = Math.min(splitText.size(), getStartingLine(page + 1, DescriptionCategory.MAX_LINES_PER_PAGE, firstPageMaxLines));
			List<String> pageText = splitText.subList(startingLineIndex, endingLineIndex);
			while (!pageText.isEmpty() && TextFormatting.getTextWithoutFormattingCodes(pageText.get(0)).isEmpty())
				pageText.remove(0);

			Constructor<T> constructor;
			T wrapper;
			try
			{
				constructor = clazz.getConstructor(ItemStack.class, List.class, List.class, List.class);
				wrapper = constructor.newInstance(itemStack, page == 0 ? related : Collections.<ItemStack>emptyList(), page == 0 ? referenced : Collections.<ItemStack>emptyList(), pageText);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			pages.add(wrapper);
		}

		return pages;
	}

	public abstract List<ItemStack> getRelated(ItemStack itemStack);

	public abstract String getText(ItemStack itemStack);

	public abstract String getRelatedText(ItemStack itemStack);

	public List<String> splitText(String text, FontRenderer fontRenderer, int maxWidth)
	{
		if (text == null)
			return null;

		return new ArrayList<String>(fontRenderer.listFormattedStringToWidth(text, maxWidth));
	}

	public String processText(ItemStack itemStack, String text, List<ItemStack> referenced, List<ItemStack> related)
	{
		if (text == null)
			return null;

		text = text.replaceAll("\\\\n", String.valueOf('\n'));

		// {unlocalized.string.name} looks up the localized string
		Matcher localizationMatcher = Pattern.compile("\\{([^\\}]+)\\}").matcher(text);
		StringBuffer localizedBuffer = new StringBuffer(text.length());
		while (localizationMatcher.find())
		{
			localizationMatcher.appendReplacement(localizedBuffer, getStringOfItemStack(localizationMatcher.group(1), itemStack));
		}
		localizationMatcher.appendTail(localizedBuffer);
		text = localizedBuffer.toString();

		// [[mod:item_name:meta]] references an item/block (:meta is optional)
		Matcher referenceMatcher = Pattern.compile("\\[\\[([^\\]:]+:[^\\]:]+):?(\\d+)?\\]\\]").matcher(text);
		StringBuffer referencedBuffer = new StringBuffer(text.length());
		while (referenceMatcher.find())
		{
			String objectName = referenceMatcher.group(1);
			int meta = 0;
			if (referenceMatcher.groupCount() > 1)
			{
				try
				{
					meta = Integer.parseInt(referenceMatcher.group(2));
				}
				catch (NumberFormatException e)
				{
				}
			}
			ItemStack referencedItemStack = MiscHelper.getItemStackByObjectName(objectName);
			if (referencedItemStack != null && referencedItemStack.getItem() != null)
			{
				referencedItemStack.setItemDamage(meta);
				if (!isReferenceRedundant(itemStack, referencedItemStack, referenced, related))
					referenced.add(referencedItemStack);
				referenceMatcher.appendReplacement(referencedBuffer, TextFormatting.DARK_BLUE + referencedItemStack.getDisplayName() + TextFormatting.RESET);
			}
		}
		referenceMatcher.appendTail(referencedBuffer);
		text = referencedBuffer.toString();

		return text;
	}

	public int getNumPages(List<String> splitText, int maxLinesPerPage, int firstPageMaxLines)
	{
		return splitText.size() > firstPageMaxLines ? 1 + MathHelper.ceiling_float_int((splitText.size() - firstPageMaxLines) / (float) maxLinesPerPage) : 1;
	}

	public int getStartingLine(int page, int maxLinesPerPage, int firstPageMaxLines)
	{
		if (page == 0)
			return 0;
		else
			return firstPageMaxLines + (page - 1) * maxLinesPerPage;
	}

	public String getStringOfItemStack(String string, ItemStack itemStack)
	{
		if (LangHelper.existsRaw(string))
		{
			return LangHelper.translateRaw(string, TextFormatting.BLACK + itemStack.getDisplayName() + TextFormatting.RESET);
		}
		return "";
	}

	public String getUsageOfItemStack(ItemStack itemStack)
	{
		return getStringOfItemStack(itemStack.getUnlocalizedName() + ".nei.usage", itemStack);
	}

	public String getCraftingOfItemStack(ItemStack itemStack)
	{
		return getStringOfItemStack(itemStack.getUnlocalizedName() + ".nei.crafting", itemStack);
	}

	public boolean isReferenceRedundant(ItemStack itemStack, ItemStack referencedItemStack, List<ItemStack> referenced, List<ItemStack> related)
	{
		return OreDictionary.itemMatches(itemStack, referencedItemStack, false) || MiscHelper.isItemStackInList(referenced, referencedItemStack) || MiscHelper.isItemStackInList(related, referencedItemStack);
	}
}
