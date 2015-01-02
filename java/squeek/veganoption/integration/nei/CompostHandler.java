package squeek.veganoption.integration.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.ModInfo;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.registry.CompostRegistry;
import squeek.veganoption.registry.Content;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;

// BEWARE: this is a sad, hacked together mess
public class CompostHandler extends TemplateRecipeHandler
{
	public static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	public static final int WIDTH = 166;
	public static final int PADDING = 4;

	ItemStack itemStack = null;
	boolean isUsage;

	public CompostHandler()
	{
	}

	@Override
	public void loadTransferRects()
	{
		transferRects.add(new RecipeTransferRect(new Rectangle(WIDTH / 2 - 11, 16, 22, 15), getOverlayIdentifier()));
		super.loadTransferRects();
	}

	@Override
	public String getOverlayIdentifier()
	{
		return ModInfo.MODID + ".composting";
	}

	public CompostHandler(ItemStack itemStack, boolean isUsage)
	{
		this.itemStack = itemStack.copy();
		this.itemStack.stackSize = 1;
		this.isUsage = isUsage;
	}

	@Override
	public ICraftingHandler getRecipeHandler(String outputId, Object... results)
	{
		if (outputId.equals(getOverlayIdentifier()))
		{
			itemStack = new ItemStack(Content.composter);
			return this;
		}
		else if (outputId.equals("item"))
		{
			for (Object result : results)
			{
				if (result instanceof ItemStack && isCompostResult((ItemStack) result))
				{
					return new CompostHandler((ItemStack) result, false);
				}
			}
		}
		return super.getRecipeHandler(outputId, results);
	}

	@Override
	public IUsageHandler getUsageHandler(String inputId, Object... ingredients)
	{
		if (inputId.equals(getOverlayIdentifier()))
		{
			itemStack = new ItemStack(Content.composter);
			return this;
		}
		else if (inputId.equals("item"))
		{
			for (Object ingredient : ingredients)
			{
				if (ingredient instanceof ItemStack && CompostRegistry.isCompostable((ItemStack) ingredient))
				{
					return new CompostHandler((ItemStack) ingredient, true);
				}
				else if (ingredient instanceof ItemStack && ((ItemStack) ingredient).getItem() == Item.getItemFromBlock(Content.composter))
				{
					itemStack = (ItemStack) ingredient;
					return this;
				}
			}
		}
		return super.getUsageHandler(inputId, ingredients);
	}

	public boolean isCompostResult(ItemStack itemStack)
	{
		return itemStack != null && itemStack.getItem() != null && (itemStack.isItemEqual(new ItemStack(Content.compost)) || itemStack.getItem() == Content.rottenPlants);
	}

	@Override
	public String getRecipeName()
	{
		return "Composting";
	}

	@Override
	public int numRecipes()
	{
		if (itemStack != null)
		{
			if (isCompostResult(itemStack) || CompostRegistry.isBrown(itemStack))
				return 1;
			else
				return 2;
		}
		return 0;
	}

	public Point getRecipePosition(int recipe)
	{
		return new Point(WIDTH / 2, 16 + -(recipe % recipiesPerPage() > 0 ? recipe % recipiesPerPage() * 24 : 0));
	}

	@Override
	public void drawBackground(int recipe)
	{
		Point recipePos = getRecipePosition(recipe);
		int x = recipePos.x;
		int y = recipePos.y;
		
		GL11.glColor4f(1, 1, 1, 1);
		GuiDraw.changeTexture("textures/gui/container/crafting_table.png");
		
		GuiDraw.drawTexturedModalRect(x + GuiHelper.STANDARD_SLOT_WIDTH - 1, y - 2, 119, 29, 26, 27);

		y += 4;
		GL11.glColor4f(.75f, 1f, .75f, 1);
		GuiDraw.drawTexturedModalRect(x - GuiHelper.STANDARD_SLOT_WIDTH * 2 - 1, y - 1, 47, 34, 18, 18);
		GuiDraw.drawTexturedModalRect(x - GuiHelper.STANDARD_SLOT_WIDTH * 3 - 1, y - 1, 47, 34, 18, 18);
		GL11.glColor4f(1f, 1f, .75f, 1);
		GuiDraw.drawTexturedModalRect(x - GuiHelper.STANDARD_SLOT_WIDTH * 4 - 1, y - 1, 47, 34, 18, 18);
	}

	@Override
	public void drawForeground(int recipe)
	{
		Point recipePos = getRecipePosition(recipe);
		int x = recipePos.x;
		int y = recipePos.y + PADDING;

		GL11.glColor4f(1, 1, 1, 1);
		GuiDraw.changeTexture("textures/gui/container/crafting_table.png");
		GuiDraw.drawTexturedModalRect(x - 11, y, 90, 35, 22, 15);
	}

	@Override
	public List<PositionedStack> getIngredientStacks(int recipe)
	{
		List<PositionedStack> positionedStacks = new ArrayList<PositionedStack>();
		int ticksPerStackSize = MiscHelper.TICKS_PER_SEC * 1;
		int curGreen = (int) ((cycleticks / ticksPerStackSize) % (CompostRegistry.greens.size()));

		Point recipePos = getRecipePosition(recipe);
		int x = recipePos.x - (GuiHelper.STANDARD_SLOT_WIDTH * 2);
		int y = recipePos.y + 4;

		ItemStack greenStack = CompostRegistry.isGreen(itemStack) && isUsage ? itemStack : CompostRegistry.greens.get(curGreen).copy();
		positionedStacks.add(new PositionedStack(greenStack, x, y, false));

		if (recipe == 0 && (isUsage || itemStack == null || itemStack.getItem() != Content.rottenPlants))
		{
			int curSecondGreen = (curGreen + 1) % CompostRegistry.greens.size();
			int curBrown = (int) ((cycleticks / ticksPerStackSize) % (CompostRegistry.browns.size()));
			ItemStack brownStack = CompostRegistry.isBrown(itemStack) && isUsage ? itemStack : CompostRegistry.browns.get(curBrown).copy();

			x -= GuiHelper.STANDARD_SLOT_WIDTH;
			positionedStacks.add(new PositionedStack(CompostRegistry.greens.get(curSecondGreen).copy(), x, y, false));
			x -= GuiHelper.STANDARD_SLOT_WIDTH;
			positionedStacks.add(new PositionedStack(brownStack, x, y, false));
		}
		return positionedStacks;
	}

	@Override
	public List<PositionedStack> getOtherStacks(int recipe)
	{
		List<PositionedStack> positionedStacks = new ArrayList<PositionedStack>();
		return positionedStacks;
	}

	@Override
	public PositionedStack getResultStack(int recipe)
	{
		Point recipePos = getRecipePosition(recipe);
		int x = recipePos.x + (GuiHelper.STANDARD_SLOT_WIDTH) + 4;
		int y = recipePos.y + 4;
		ItemStack result = recipe == 0 && (isUsage || itemStack == null || itemStack.getItem() != Content.rottenPlants) ? new ItemStack(Content.compost) : new ItemStack(Content.rottenPlants);
		return new PositionedStack(result, x, y, false);
	}

	@Override
	public int recipiesPerPage()
	{
		return 2;
	}

	@Override
	public String getGuiTexture()
	{
		return null;
	}

	// have to correct transferRect offsets because NEI enforces 65px between recipes
	// regardless of recipesPerPage
	public static Method transferRectTooltip;
	public static Method transferRect;
	static
	{
		try
		{
			transferRectTooltip = TemplateRecipeHandler.class.getDeclaredMethod("transferRectTooltip", GuiContainer.class, Collection.class, int.class, int.class, List.class);
			transferRectTooltip.setAccessible(true);
			transferRect = TemplateRecipeHandler.class.getDeclaredMethod("transferRect", GuiContainer.class, Collection.class, int.class, int.class, boolean.class);
			transferRect.setAccessible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> handleTooltip(GuiRecipe gui, List<String> currenttip, int recipe)
	{
		if (GuiContainerManager.shouldShowTooltip(gui) && currenttip.size() == 0)
		{
			Point offset = gui.getRecipePosition(recipe);
			Point offsetOffset = getRecipePosition(recipe);
			Point realOffset = new Point(offset.x + offsetOffset.x - WIDTH / 2, offset.y + offsetOffset.y - 12);
			try
			{
				currenttip = (List<String>) transferRectTooltip.invoke(null, gui, transferRects, realOffset.x, realOffset.y, currenttip);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return currenttip;
	}

	@SuppressWarnings("unused")
	private boolean transferRect(GuiRecipe gui, int recipe, boolean usage)
	{
		Point offset = gui.getRecipePosition(recipe);
		Point offsetOffset = getRecipePosition(recipe);
		Point realOffset = new Point(offset.x + offsetOffset.x, offset.y + offsetOffset.y);
		try
		{
			return (Boolean) transferRect.invoke(null, gui, transferRects, realOffset.x, realOffset.y, usage);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
