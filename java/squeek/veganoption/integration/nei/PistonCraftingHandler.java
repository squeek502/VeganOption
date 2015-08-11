package squeek.veganoption.integration.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.helpers.FluidHelper;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.helpers.MiscHelper;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;

// TODO: Support for multiple outputs
public class PistonCraftingHandler extends TemplateRecipeHandler
{
	public static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	public static final int WIDTH = 166;
	public static final int PADDING = 4;
	public static final Point startPoint = new Point(WIDTH / 2, 16);

	public static Item pistonItem = Item.getItemFromBlock(Blocks.piston);
	public static final PositionedStack pistonStack = new PositionedStack(new ItemStack(Blocks.piston), startPoint.x - GuiHelper.STANDARD_SLOT_WIDTH / 2, startPoint.y + 4, false);

	public class CachedPistonCraftingRecipe extends TemplateRecipeHandler.CachedRecipe
	{
		public List<PositionedStack> ingredients = new ArrayList<PositionedStack>();
		public List<PositionedStack> other = new ArrayList<PositionedStack>();
		public PositionedStack result;

		public CachedPistonCraftingRecipe(PistonCraftingRecipe recipe)
		{
			super();
			ItemStack resultAsItemStack = recipe.itemOutputs.size() > 0 ? recipe.itemOutputs.get(0) : FluidHelper.toItemStack(recipe.fluidOutput);
			result = new PositionedStack(resultAsItemStack, startPoint.x + (GuiHelper.STANDARD_SLOT_WIDTH * 2) + 4, startPoint.y + 4, false);

			int numIngredients = recipe.itemInputs.size() + (recipe.fluidInput != null ? 1 : 0);
			int width = numIngredients <= 1 ? 1 : (numIngredients <= 4 ? 2 : 3);
			int height = numIngredients <= 2 ? 1 : (numIngredients <= 6 ? 2 : 3);
			Object ingredientsArray[] = new Object[width * height];

			int i = 0;
			for (InputItemStack input : recipe.itemInputs)
			{
				if (input.wrappedItemStack != null)
				{
					ingredientsArray[i++] = input.wrappedItemStack;
				}
				else if (input.oreDictItemStacks != null)
				{
					if (input.stackSize() == 1)
						ingredientsArray[i++] = input.oreDictItemStacks;
					else
					{
						List<ItemStack> oreDictItemStacksWithCorrectStackSize = new ArrayList<ItemStack>();
						for (ItemStack inputStack : input.oreDictItemStacks)
						{
							ItemStack inputWithCorrectStackSize = inputStack.copy();
							inputWithCorrectStackSize.stackSize = input.stackSize();
							oreDictItemStacksWithCorrectStackSize.add(inputWithCorrectStackSize);
						}
						ingredientsArray[i++] = oreDictItemStacksWithCorrectStackSize;
					}

				}
			}
			if (recipe.fluidInput != null)
			{
				ingredientsArray[i++] = FluidHelper.toItemStack(recipe.fluidInput);
			}

			setIngredients(width, height, ingredientsArray);
		}

		@Override
		public PositionedStack getResult()
		{
			return result;
		}

		public void setIngredients(int width, int height, Object[] items)
		{
			int maxWidth = 3 * GuiHelper.STANDARD_SLOT_WIDTH;
			int startX = startPoint.x - (int) (GuiHelper.STANDARD_SLOT_WIDTH * 1.5f) - maxWidth / 2 - (GuiHelper.STANDARD_SLOT_WIDTH * (width - 1) / 2);
			int startY = startPoint.y + 4 - (GuiHelper.STANDARD_SLOT_WIDTH * (height - 1) / 2);

			for (int x = 0; x < width; ++x)
			{
				for (int y = 0; y < height; ++y)
				{
					if (items[(y * width + x)] == null)
					{
						continue;
					}
					PositionedStack stack = new PositionedStack(items[(y * width + x)], startX + x * GuiHelper.STANDARD_SLOT_WIDTH, startY + y * GuiHelper.STANDARD_SLOT_WIDTH, false);
					this.ingredients.add(stack);
				}
			}
		}

		@Override
		public List<PositionedStack> getIngredients()
		{
			return getCycledIngredients(cycleticks / 20, ingredients);
		}

		@Override
		public PositionedStack getOtherStack()
		{
			return PistonCraftingHandler.pistonStack;
		}

		@Override
		public List<PositionedStack> getOtherStacks()
		{
			List<PositionedStack> otherStacks = super.getOtherStacks();
			otherStacks.addAll(other);
			return otherStacks;
		}

		public void computeVisuals()
		{
			for (PositionedStack p : this.ingredients)
				p.generatePermutations();
		}

	}

	ItemStack itemStack = null;

	public PistonCraftingHandler()
	{
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results)
	{
		List<PistonCraftingRecipe> recipes = null;

		if (outputId.equals(getOverlayIdentifier()))
			recipes = PistonCraftingRegistry.getRecipes();
		else if (outputId.equals("item") || outputId.equals("liquid"))
		{
			for (Object result : results)
			{
				ItemStack itemStackToCheck = null;

				if (result instanceof ItemStack)
					itemStackToCheck = (ItemStack) result;
				else if (result instanceof FluidStack)
					itemStackToCheck = FluidHelper.toItemStack((FluidStack) result);

				if (itemStackToCheck != null && hasRecipe(itemStackToCheck))
				{
					this.itemStack = itemStackToCheck.copy();
					itemStack.stackSize = 1;
					recipes = PistonCraftingRegistry.getSubsetByOutput(this.itemStack);
					break;
				}
			}
		}

		if (recipes != null)
			loadRecipes(recipes);
		else
			super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients)
	{
		List<PistonCraftingRecipe> recipes = null;

		if (inputId.equals(getOverlayIdentifier()))
			recipes = PistonCraftingRegistry.getRecipes();
		else if (inputId.equals("item") || inputId.equals("liquid"))
		{
			for (Object ingredient : ingredients)
			{
				ItemStack itemStackToCheck = null;

				if (ingredient instanceof ItemStack)
					itemStackToCheck = (ItemStack) ingredient;
				else if (ingredient instanceof FluidStack)
					itemStackToCheck = FluidHelper.toItemStack((FluidStack) ingredient);

				if (itemStackToCheck != null && hasUsage(itemStackToCheck))
				{
					this.itemStack = itemStackToCheck.copy();
					itemStack.stackSize = 1;
					if (itemStack.getItem() == pistonItem)
						recipes = PistonCraftingRegistry.getRecipes();
					else
						recipes = PistonCraftingRegistry.getSubsetByInput(this.itemStack);
					break;
				}
			}
		}

		if (recipes != null)
			loadRecipes(recipes);
		else
			super.loadUsageRecipes(inputId, ingredients);
	}

	public void loadRecipes(Collection<PistonCraftingRecipe> recipes)
	{
		for (PistonCraftingRecipe recipe : recipes)
		{
			CachedPistonCraftingRecipe cachedRecipe = new CachedPistonCraftingRecipe(recipe);
			cachedRecipe.computeVisuals();
			if (itemStack != null && cachedRecipe.contains(cachedRecipe.ingredients, itemStack))
			{
				cachedRecipe.setIngredientPermutation(cachedRecipe.ingredients, itemStack);
			}
			arecipes.add(cachedRecipe);
		}
	}

	public boolean hasUsage(ItemStack itemStack)
	{
		if (itemStack.getItem() == Item.getItemFromBlock(Blocks.piston))
			return true;

		for (PistonCraftingRecipe recipe : PistonCraftingRegistry.getRecipes())
		{
			for (InputItemStack input : recipe.itemInputs)
			{
				if (input.matches(itemStack))
					return true;
			}
			if (recipe.fluidInput != null && recipe.fluidInput.isFluidEqual(FluidHelper.fromItemStack(itemStack)))
				return true;
		}
		return false;
	}

	public boolean hasRecipe(ItemStack itemStack)
	{
		for (PistonCraftingRecipe recipe : PistonCraftingRegistry.getRecipes())
		{
			for (ItemStack output : recipe.itemOutputs)
			{
				if (OreDictionary.itemMatches(output, itemStack, true))
					return true;
			}
			if (recipe.fluidOutput != null && recipe.fluidOutput.isFluidEqual(FluidHelper.fromItemStack(itemStack)))
				return true;
		}
		return false;
	}

	@Override
	public void loadTransferRects()
	{
		transferRects.add(new RecipeTransferRect(new Rectangle(startPoint.x + 4, startPoint.y + 4, 24, 15), getOverlayIdentifier()));
		super.loadTransferRects();
	}

	@Override
	public String getOverlayIdentifier()
	{
		return ModInfo.MODID + ".pistonCrafting";
	}

	@Override
	public String getRecipeName()
	{
		return LangHelper.translate("nei.pistonCrafting");
	}

	public Point getRecipePosition(int recipe)
	{
		return startPoint;
	}

	public List<String> styleFluidTooltip(List<String> tooltip)
	{
		for (int i = 0; i < tooltip.size(); i++)
		{
			tooltip.set(i, EnumChatFormatting.GOLD + EnumChatFormatting.ITALIC.toString() + tooltip.get(i) + EnumChatFormatting.RESET);
		}
		return tooltip;
	}

	@Override
	public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipeIndex)
	{
		if (FluidHelper.fromItemStack(stack) == null)
			return super.handleItemTooltip(gui, stack, currenttip, recipeIndex);

		CachedPistonCraftingRecipe cachedRecipe = (CachedPistonCraftingRecipe) arecipes.get(recipeIndex);

		if (gui.isMouseOver(cachedRecipe.result, recipeIndex))
		{
			@SuppressWarnings("unchecked")
			List<String> splitFluidOutputTooltip = fontRenderer.listFormattedStringToWidth(LangHelper.translate("nei.pistonCrafting.tooltip.fluidOutput"), WIDTH);
			styleFluidTooltip(splitFluidOutputTooltip);
			currenttip.addAll(splitFluidOutputTooltip);
		}
		else
		{
			for (PositionedStack positionedIngredient : cachedRecipe.ingredients)
			{
				if (gui.isMouseOver(positionedIngredient, recipeIndex))
				{
					@SuppressWarnings("unchecked")
					List<String> splitFluidInputTooltip = fontRenderer.listFormattedStringToWidth(LangHelper.translate("nei.pistonCrafting.tooltip.fluidInput"), WIDTH);
					styleFluidTooltip(splitFluidInputTooltip);
					currenttip.addAll(splitFluidInputTooltip);
					break;
				}
			}
		}

		return super.handleItemTooltip(gui, stack, currenttip, recipeIndex);
	}

	@Override
	public void drawBackground(int recipe)
	{
		Point recipePos = getRecipePosition(recipe);
		int x = recipePos.x;
		int y = recipePos.y;

		GL11.glColor4f(1, 1, 1, 1);
		GuiDraw.changeTexture("textures/gui/container/furnace.png");

		// arrow
		GuiDraw.drawTexturedModalRect(x + 4, y + 4, 79, 35, 24, 15);
		this.drawProgressBar(x + 4, y + 4, 176, 14, 24, 15, MiscHelper.TICKS_PER_SEC, 0);

		// output slot
		GuiDraw.drawTexturedModalRect(x + GuiHelper.STANDARD_SLOT_WIDTH * 2 - 1, y - 1, 111, 30, 26, 26);

		// input slots
		CachedPistonCraftingRecipe cachedRecipe = (CachedPistonCraftingRecipe) arecipes.get(recipe);
		for (PositionedStack ingredient : cachedRecipe.ingredients)
		{
			GuiDraw.drawTexturedModalRect(ingredient.relx - 1, ingredient.rely - 1, 55, 16, 18, 18);
		}
	}

	@Override
	public void drawForeground(int recipe)
	{
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

}
