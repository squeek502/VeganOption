package squeek.veganoption.integration.nei;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.helpers.ColorHelper;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.registry.RelationshipRegistry;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;

public class TextHandler implements IUsageHandler, ICraftingHandler
{
	public static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	public static final int WIDTH = 166;
	public static final int HEIGHT = 130;
	public static final int Y_START = 22;
	public static final int PADDING = 4;
	public static final int MAX_LINES_PER_PAGE = (HEIGHT - Y_START) / fontRenderer.FONT_HEIGHT;

	public String text = null;
	public ItemStack itemStack = null;
	public boolean isUsage;
	public List<String> splitText = new ArrayList<String>();
	public int firstPageMaxLines = MAX_LINES_PER_PAGE;

	public List<ItemStack> children;
	public List<ItemStack> parents;
	public List<ItemStack> referenced = new ArrayList<ItemStack>();

	public TextHandler()
	{
	}

	public TextHandler(ItemStack itemStack, boolean isUsage)
	{
		this.itemStack = itemStack.copy();
		this.itemStack.stackSize = 1;

		this.isUsage = isUsage;
		this.children = isUsage ? RelationshipRegistry.getChildren(itemStack) : null;
		this.parents = !isUsage ? RelationshipRegistry.getParents(itemStack) : null;

		this.text = isUsage ? getUsageOfItemStack(this.itemStack) : getCraftingOfItemStack(this.itemStack);
		
		if (parents != null)
		{
			for (ItemStack parent : parents)
			{
				String parentString = getUsageOfItemStack(parent);
				if (!parentString.isEmpty())
				{
					this.text += (!this.text.isEmpty() ? "\n\n" : "") + parentString;
				}
			}
		}
		else if (children != null)
		{
			for (ItemStack child : children)
			{
				String childString = getCraftingOfItemStack(child);
				if (!childString.isEmpty())
				{
					this.text += (!this.text.isEmpty() ? "\n\n" : "") + childString;
				}
			}
		}
		
		if (parents != null || children != null)
			firstPageMaxLines -= 3;

		this.text = processText(text);
		
		if (referenced.size() > 0)
			firstPageMaxLines -= 4;

		splitText = splitText(text);
	}
	
	public String getStringOfItemStack(String string, ItemStack itemStack)
	{
		if (LangHelper.existsRaw(string))
		{
			return LangHelper.translateRaw(string, EnumChatFormatting.BLACK + itemStack.getDisplayName() + EnumChatFormatting.RESET);
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

	@SuppressWarnings("unchecked")
	public List<String> splitText(String text)
	{
		if (text == null)
			return null;

		return fontRenderer.listFormattedStringToWidth(text, WIDTH - PADDING * 2);
	}
	
	public String processText(String text)
	{
		if (text == null)
			return null;

		// {unlocalized.string.name} looks up the localized string
		Matcher localizationMatcher = Pattern.compile("\\{([^\\}]+)\\}").matcher(text);
		StringBuffer localizedBuffer = new StringBuffer(text.length());
		while (localizationMatcher.find())
		{
			localizationMatcher.appendReplacement(localizedBuffer, StatCollector.translateToLocal(localizationMatcher.group(1)));
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
					meta = new Integer(referenceMatcher.group(2));
				}
				catch (NumberFormatException e)
				{
				}
			}
			ItemStack itemStack = MiscHelper.getItemStackByObjectName(objectName);
			if (itemStack != null && itemStack.getItem() != null)
			{
				itemStack.setItemDamage(meta);
				if (!isReferenceRedundant(itemStack))
					referenced.add(itemStack);
				referenceMatcher.appendReplacement(referencedBuffer, EnumChatFormatting.DARK_BLUE + itemStack.getDisplayName() + EnumChatFormatting.RESET);
			}
		}
		referenceMatcher.appendTail(referencedBuffer);
		text = referencedBuffer.toString();

		return text;
	}

	public boolean isReferenceRedundant(ItemStack itemStack)
	{
		return OreDictionary.itemMatches(this.itemStack, itemStack, false) || MiscHelper.isItemStackInList(referenced, itemStack) || MiscHelper.isItemStackInList(children, itemStack) || MiscHelper.isItemStackInList(parents, itemStack);
	}

	public boolean hasUsageText(ItemStack itemStack)
	{
		return LangHelper.existsRaw(itemStack.getUnlocalizedName() + ".nei.usage") || RelationshipRegistry.getChildren(itemStack) != null;
	}

	public boolean hasRecipeText(ItemStack itemStack)
	{
		return LangHelper.existsRaw(itemStack.getUnlocalizedName() + ".nei.crafting") || RelationshipRegistry.getParents(itemStack) != null;
	}

	@Override
	public IUsageHandler getUsageHandler(String inputId, Object... ingredients)
	{
		if (inputId.equals("item"))
		{
			for (Object ingredient : ingredients)
			{
				if (ingredient instanceof ItemStack && hasUsageText((ItemStack) ingredient))
				{
					return new TextHandler((ItemStack) ingredient, true);
				}
			}
		}
		return this;
	}

	@Override
	public ICraftingHandler getRecipeHandler(String outputId, Object... results)
	{
		if (outputId.equals("item"))
		{
			for (Object result : results)
			{
				if (result instanceof ItemStack && hasRecipeText((ItemStack) result))
				{
					return new TextHandler((ItemStack) result, false);
				}
			}
		}
		return this;
	}

	@Override
	public String getRecipeName()
	{
		return isUsage ? LangHelper.translate("nei.usage") : LangHelper.translate("nei.crafting");
	}

	@Override
	public int numRecipes()
	{
		if (text == null && children == null && parents == null)
			return 0;

		return splitText.size() > firstPageMaxLines ? 1 + MathHelper.ceiling_float_int((splitText.size() - firstPageMaxLines) / (float) MAX_LINES_PER_PAGE) : 1;
	}

	@Override
	public void drawBackground(int recipe)
	{
	}

	public int getStartingLine(int recipe)
	{
		if (recipe == 0)
			return 0;
		else
			return firstPageMaxLines + (recipe - 1) * MAX_LINES_PER_PAGE;
	}

	@Override
	public void drawForeground(int recipe)
	{
		int y = Y_START;
		if (parents != null && recipe == 0)
		{
			final String byproductOfString = LangHelper.translate("nei.byproduct.of");
			GuiDraw.drawString(byproductOfString, WIDTH / 2 - GuiDraw.getStringWidth(byproductOfString) / 2, y, ColorHelper.DEFAULT_TEXT_COLOR, false);
			y += fontRenderer.FONT_HEIGHT + GuiHelper.STANDARD_SLOT_WIDTH + PADDING;
		}
		if (children != null && recipe == 0)
		{
			final String byproductsString = LangHelper.translate("nei.byproducts");
			GuiDraw.drawString(byproductsString, WIDTH / 2 - GuiDraw.getStringWidth(byproductsString) / 2, y, ColorHelper.DEFAULT_TEXT_COLOR, false);
			y += fontRenderer.FONT_HEIGHT + GuiHelper.STANDARD_SLOT_WIDTH + PADDING;
		}
		if (text != null)
		{
			int maxLines = recipe == 0 ? firstPageMaxLines : MAX_LINES_PER_PAGE;
			int startLine = getStartingLine(recipe);
			int endLine = Math.min(startLine + maxLines, splitText.size());
			for (int i=startLine; i < endLine; i++)
			{
				String line = splitText.get(i);
				GuiDraw.drawString(line, WIDTH / 2 - GuiDraw.getStringWidth(line) / 2, y, ColorHelper.DEFAULT_TEXT_COLOR, false);
				y += fontRenderer.FONT_HEIGHT;
			}
		}
		if (referenced.size() > 0 && recipe == 0)
		{
			y = HEIGHT - GuiHelper.STANDARD_SLOT_WIDTH - fontRenderer.FONT_HEIGHT;
			final String referencesString = LangHelper.translate("nei.references");
			GuiDraw.drawString(referencesString, WIDTH / 2 - GuiDraw.getStringWidth(referencesString) / 2, y, ColorHelper.DEFAULT_TEXT_COLOR, false);
		}
	}

	@Override
	public List<PositionedStack> getIngredientStacks(int recipe)
	{
		List<PositionedStack> positionedParents = new ArrayList<PositionedStack>();
		if (parents != null && recipe == 0)
		{
			int startX = WIDTH / 2 - (parents.size() * GuiHelper.STANDARD_SLOT_WIDTH) / 2;
			int startY = 22 + fontRenderer.FONT_HEIGHT;
			for (int i = 0; i < parents.size(); i++)
			{
				positionedParents.add(new PositionedStack(parents.get(i), startX + i * GuiHelper.STANDARD_SLOT_WIDTH, startY, false));
			}
		}
		return positionedParents;
	}

	@Override
	public List<PositionedStack> getOtherStacks(int recipe)
	{
		List<PositionedStack> positionedChildren = new ArrayList<PositionedStack>();
		if (children != null && recipe == 0)
		{
			int startX = WIDTH / 2 - (children.size() * GuiHelper.STANDARD_SLOT_WIDTH) / 2;
			int startY = 22 + fontRenderer.FONT_HEIGHT + (parents != null ? fontRenderer.FONT_HEIGHT + GuiHelper.STANDARD_SLOT_WIDTH + PADDING : 0);
			for (int i = 0; i < children.size(); i++)
			{
				positionedChildren.add(new PositionedStack(children.get(i), startX + i * GuiHelper.STANDARD_SLOT_WIDTH, startY, false));
			}
		}
		if (referenced.size() > 0 && recipe == 0)
		{
			int referencedX = WIDTH / 2 - (referenced.size() * GuiHelper.STANDARD_SLOT_WIDTH) / 2;
			int referencedY = HEIGHT - GuiHelper.STANDARD_SLOT_WIDTH;
			for (int i = 0; i < referenced.size(); i++)
			{
				positionedChildren.add(new PositionedStack(referenced.get(i), referencedX + i * GuiHelper.STANDARD_SLOT_WIDTH, referencedY, false));
			}
		}
		return positionedChildren;
	}

	@Override
	public PositionedStack getResultStack(int recipe)
	{
		return new PositionedStack(itemStack, WIDTH / 2 - GuiHelper.STANDARD_SLOT_WIDTH / 2, 0, false);
	}

	@Override
	public void onUpdate()
	{
	}

	@Override
	public boolean hasOverlay(GuiContainer gui, Container container, int recipe)
	{
		return false;
	}

	@Override
	public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int recipe)
	{
		return null;
	}

	@Override
	public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe)
	{
		return null;
	}

	@Override
	public int recipiesPerPage()
	{
		return 1;
	}

	@Override
	public List<String> handleTooltip(GuiRecipe gui, List<String> currenttip, int recipe)
	{
		return currenttip;
	}

	@Override
	public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe)
	{
		return currenttip;
	}

	@Override
	public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe)
	{
		return false;
	}

	@Override
	public boolean mouseClicked(GuiRecipe gui, int button, int recipe)
	{
		return false;
	}

}
