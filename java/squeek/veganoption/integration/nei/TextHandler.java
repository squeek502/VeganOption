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
import net.minecraft.util.StatCollector;
import squeek.veganoption.helpers.ColorHelper;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.LangHelper;
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
	public static final int PADDING = 4;

	String unlocalized = null;
	String text = null;
	ItemStack itemStack = null;
	boolean isUsage;

	List<ItemStack> children;
	List<ItemStack> parents;

	public TextHandler()
	{
	}

	public TextHandler(ItemStack itemStack, boolean isUsage)
	{
		this.itemStack = itemStack.copy();
		this.itemStack.stackSize = 1;

		this.isUsage = isUsage;
		this.unlocalized = itemStack.getUnlocalizedName() + ".nei." + (isUsage ? "usage" : "crafting");
		this.children = isUsage ? RelationshipRegistry.getChildren(itemStack) : null;
		this.parents = !isUsage ? RelationshipRegistry.getParents(itemStack) : null;

		if (LangHelper.existsRaw(unlocalized))
			this.text = LangHelper.translateRaw(unlocalized, itemStack.getDisplayName());
		else
		{
			if (!isUsage && parents != null && parents.size() == 1 && LangHelper.existsRaw(parents.get(0).getUnlocalizedName() + ".nei.usage"))
			{
				this.text = LangHelper.translateRaw(parents.get(0).getUnlocalizedName() + ".nei.usage", parents.get(0).getDisplayName());
			}
			else if (isUsage && children != null && children.size() == 1 && LangHelper.existsRaw(children.get(0).getUnlocalizedName() + ".nei.crafting"))
			{
				this.text = LangHelper.translateRaw(children.get(0).getUnlocalizedName() + ".nei.crafting", children.get(0).getDisplayName());
			}
		}

		this.text = processText(text);
	}

	public String processText(String text)
	{
		if (text == null)
			return null;

		Matcher m = Pattern.compile("\\{([^\\}]+)\\}").matcher(text);
		StringBuffer sb = new StringBuffer(text.length());
		while (m.find())
		{
			m.appendReplacement(sb, StatCollector.translateToLocal(m.group(1)));
		}
		m.appendTail(sb);
		return sb.toString();
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
		return text != null || children != null || parents != null ? 1 : 0;
	}

	@Override
	public void drawBackground(int recipe)
	{
	}

	@Override
	public void drawForeground(int recipe)
	{
		int y = 22;
		if (parents != null)
		{
			final String byproductOfString = LangHelper.translate("nei.byproduct.of");
			GuiDraw.drawString(byproductOfString, WIDTH / 2 - GuiDraw.getStringWidth(byproductOfString) / 2, y, ColorHelper.DEFAULT_TEXT_COLOR, false);
			y += fontRenderer.FONT_HEIGHT + GuiHelper.STANDARD_SLOT_WIDTH + PADDING;
		}
		if (children != null)
		{
			final String byproductsString = LangHelper.translate("nei.byproducts");
			GuiDraw.drawString(byproductsString, WIDTH / 2 - GuiDraw.getStringWidth(byproductsString) / 2, y, ColorHelper.DEFAULT_TEXT_COLOR, false);
			y += fontRenderer.FONT_HEIGHT + GuiHelper.STANDARD_SLOT_WIDTH + PADDING;
		}
		if (text != null)
		{
			@SuppressWarnings("rawtypes")
			List lines = fontRenderer.listFormattedStringToWidth(text, WIDTH - PADDING * 2);
			for (int i = 0; i < lines.size(); i++)
			{
				String t = (String) lines.get(i);
				GuiDraw.drawString(t, WIDTH / 2 - GuiDraw.getStringWidth(t) / 2, y + i * fontRenderer.FONT_HEIGHT, ColorHelper.DEFAULT_TEXT_COLOR, false);
			}
		}
	}

	@Override
	public List<PositionedStack> getIngredientStacks(int recipe)
	{
		List<PositionedStack> positionedParents = new ArrayList<PositionedStack>();
		if (parents != null)
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
	public List<PositionedStack> getOtherStacks(int recipetype)
	{
		List<PositionedStack> positionedChildren = new ArrayList<PositionedStack>();
		if (children != null)
		{
			int startX = WIDTH / 2 - (children.size() * GuiHelper.STANDARD_SLOT_WIDTH) / 2;
			int startY = 22 + fontRenderer.FONT_HEIGHT + (parents != null ? fontRenderer.FONT_HEIGHT + GuiHelper.STANDARD_SLOT_WIDTH + PADDING : 0);
			for (int i = 0; i < children.size(); i++)
			{
				positionedChildren.add(new PositionedStack(children.get(i), startX + i * GuiHelper.STANDARD_SLOT_WIDTH, startY, false));
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
