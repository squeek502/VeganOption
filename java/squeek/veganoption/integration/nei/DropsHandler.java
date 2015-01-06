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
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropInfo;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class DropsHandler extends TemplateRecipeHandler
{
	public static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	public static final int WIDTH = 166;
	public static final int PADDING = 4;

	ItemStack itemStack = null;
	List<DropInfo> blockDropSpecifiers = null;
	boolean isUsage;

	public DropsHandler()
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
		return ModInfo.MODID + ".drops";
	}

	public DropsHandler(ItemStack itemStack, boolean isUsage)
	{
		this.itemStack = itemStack.copy();
		this.itemStack.stackSize = 1;
		this.isUsage = isUsage;
		if (isUsage)
			this.blockDropSpecifiers = Modifiers.drops.getSubsetByBlock(this.itemStack);
		else
			this.blockDropSpecifiers = Modifiers.drops.getSubsetByDroppedItem(this.itemStack);
	}

	public boolean isApplicable(ItemStack itemStack)
	{
		return Modifiers.drops.dropExists(itemStack) || Modifiers.drops.hasDrops(itemStack);
	}

	@Override
	public ICraftingHandler getRecipeHandler(String outputId, Object... results)
	{
		if (outputId.equals(getOverlayIdentifier()))
		{
			blockDropSpecifiers = Modifiers.drops.getAllDrops();
			return this;
		}
		else if (outputId.equals("item"))
		{
			for (Object result : results)
			{
				if (result instanceof ItemStack && isApplicable((ItemStack) result))
				{
					return new DropsHandler((ItemStack) result, false);
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
			blockDropSpecifiers = Modifiers.drops.getAllDrops();
			return this;
		}
		else if (inputId.equals("item"))
		{
			for (Object ingredient : ingredients)
			{
				if (ingredient instanceof ItemStack && isApplicable((ItemStack) ingredient))
				{
					return new DropsHandler((ItemStack) ingredient, true);
				}
			}
		}
		return super.getUsageHandler(inputId, ingredients);
	}

	@Override
	public String getRecipeName()
	{
		return LangHelper.translate("nei.drops");
	}

	@Override
	public int numRecipes()
	{
		return blockDropSpecifiers != null ? blockDropSpecifiers.size() : 0;
	}

	public Point getRecipePosition(int recipe)
	{
		return new Point(WIDTH / 2, 16 + -(recipe % recipiesPerPage() > 0 ? recipe % recipiesPerPage() * 24 : 0));
	}

	@Override
	public void drawBackground(int recipe)
	{
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

		String dropChance = String.format("%.0f%%", blockDropSpecifiers.get(recipe).drop.dropChance * 100f);
		GuiDraw.drawString(dropChance, x - GuiDraw.getStringWidth(dropChance) / 2, y - fontRenderer.FONT_HEIGHT, 0x8b8b8b, false);
	}

	@Override
	public List<PositionedStack> getIngredientStacks(int recipe)
	{
		List<PositionedStack> positionedStacks = new ArrayList<PositionedStack>();
		if (blockDropSpecifiers != null)
		{
			Point recipePos = getRecipePosition(recipe);
			int x = recipePos.x - (GuiHelper.STANDARD_SLOT_WIDTH * 2);
			int y = recipePos.y;
			BlockSpecifier blockSpecifier = blockDropSpecifiers.get(recipe).dropper;
			ItemStack blockStack = blockSpecifier.neiItemStack != null ? blockSpecifier.neiItemStack : new ItemStack(blockSpecifier.block, 1, blockSpecifier.meta);
			positionedStacks.add(new PositionedStack(blockStack, x, y, false));
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
		if (blockDropSpecifiers != null)
		{
			Point recipePos = getRecipePosition(recipe);
			int x = recipePos.x + (GuiHelper.STANDARD_SLOT_WIDTH);
			int y = recipePos.y;
			DropSpecifier dropSpecifier = blockDropSpecifiers.get(recipe).drop;
			ItemStack drop = dropSpecifier.itemStack.copy();
			int ticksPerStackSize = MiscHelper.TICKS_PER_SEC * 1;
			drop.stackSize = dropSpecifier.dropsMin + (int) ((cycleticks / ticksPerStackSize) % (dropSpecifier.dropsMax + 1 - dropSpecifier.dropsMin));
			if (drop.stackSize > 0)
				return new PositionedStack(drop, x, y, false);
		}
		return null;
	}

	@Override
	public int recipiesPerPage()
	{
		return 3;
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

	@Override
	public boolean mouseClicked(GuiRecipe gui, int button, int recipe)
	{
		if (button == 0)
			return transferRect(gui, recipe, false);
		else if (button == 1)
			return transferRect(gui, recipe, true);

		return false;
	}

	private boolean transferRect(GuiRecipe gui, int recipe, boolean usage)
	{
		Point offset = gui.getRecipePosition(recipe);
		Point offsetOffset = getRecipePosition(recipe);
		Point realOffset = new Point(offset.x + offsetOffset.x - WIDTH / 2, offset.y + offsetOffset.y - 12);
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
