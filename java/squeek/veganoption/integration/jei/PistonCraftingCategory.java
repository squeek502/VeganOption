package squeek.veganoption.integration.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.helpers.LangHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static squeek.veganoption.integration.jei.VeganOptionPlugin.CRAFTING_TABLE_GUI;

class PistonCraftingCategory implements IRecipeCategory<PistonCraftingRecipe>
{
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable arrow;
	private final IDrawable slot;

	PistonCraftingCategory(IGuiHelper guiHelper)
	{
		background = guiHelper.createBlankDrawable(116, 54);
		icon = guiHelper.createDrawableItemStack(new ItemStack(Items.PISTON));
		arrow = guiHelper.createDrawable(CRAFTING_TABLE_GUI, 90, 35, 22, 15);
		slot = guiHelper.getSlotDrawable();
	}

	@Nonnull
	@Override
	public RecipeType<PistonCraftingRecipe> getRecipeType()
	{
		return VeganOptionPlugin.PISTON_CRAFTING;
	}

	@Nonnull
	@Override
	public Component getTitle()
	{
		return Component.translatable(LangHelper.prependModId("jei.piston_crafting"));
	}

	@Nonnull
	@Override
	public IDrawable getBackground()
	{
		return background;
	}

	@Nonnull
	@Override
	public IDrawable getIcon()
	{
		return icon;
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull PistonCraftingRecipe recipe, @Nonnull IFocusGroup focuses)
	{
		{
			List<Object> inputs = new ArrayList<>(recipe.itemInputs);
			if (!recipe.fluidInput.isEmpty())
				inputs.add(recipe.fluidInput);
			List<List<Object>> inputRows = Lists.partition(inputs, 2);
			int y = 0;
			// 24 = 8 padding, 18 for the slot itself
			int xStart = (getWidth() / 2) - (arrow.getWidth() / 2) - 24;
			int yStart = (getHeight() / 2) - (Math.max(1, inputRows.size()) * 9) + 1;
			for (List<Object> row : inputRows)
			{
				int x = row.size() - 1;
				for (Object col : row)
				{
					if (col instanceof InputItemStack s)
						builder.addSlot(RecipeIngredientRole.INPUT, xStart - x * 18, yStart + y * 18)
							.addItemStacks(s.resolve());
					else if (col instanceof FluidStack f)
						builder.addSlot(RecipeIngredientRole.INPUT, xStart - x * 18, yStart + y * 18)
							.addFluidStack(f.getFluid(), f.getAmount())
							.addTooltipCallback(new FluidTooltipCallback(f.getAmount()));
					x--;
				}
				y++;
			}
		}

		{
			List<Object> outputs = new ArrayList<>(recipe.itemOutputs);
			if (!recipe.fluidOutput.isEmpty())
				outputs.add(recipe.fluidOutput);
			List<List<Object>> outputRows = Lists.partition(outputs, 2);
			int y = 0;
			int xStart = (getWidth() / 2) + (arrow.getWidth() / 2) + 9;
			int yStart = (getHeight() / 2) - (Math.max(1, outputRows.size()) * 9) + 2;
			for (List<Object> row : outputRows)
			{
				int x = row.size() - 1;
				for (Object col : row)
				{
					if (col instanceof ItemStack)
						builder.addSlot(RecipeIngredientRole.OUTPUT, xStart + x * 18, yStart + y * 18)
							.addItemStack((ItemStack) col);
					else if (col instanceof FluidStack f)
						builder.addSlot(RecipeIngredientRole.OUTPUT, xStart + x * 18, yStart + y * 18)
							.addFluidStack(f.getFluid(), f.getAmount())
							.addTooltipCallback(new FluidTooltipCallback(f.getAmount()));
					x--;
				}
				y++;
			}
		}
	}

	@Override
	public void draw(@Nonnull PistonCraftingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		arrow.draw(guiGraphics, (getWidth() / 2) - (arrow.getWidth() / 2), (getHeight() / 2) - (arrow.getHeight() / 2));
		{
			List<Object> inputs = new ArrayList<>(recipe.itemInputs);
			if (!recipe.fluidInput.isEmpty())
				inputs.add(recipe.fluidInput);
			List<List<Object>> inputRows = Lists.partition(inputs, 2);
			int y = 0;
			int xStart = (getWidth() / 2) - (arrow.getWidth() / 2) - 24;
			int yStart = (getHeight() / 2) - (Math.max(1, inputRows.size()) * 9) + 1;
			for (List<Object> row : inputRows)
			{
				int x = row.size() - 1;
				for (Object ignored : row)
				{
					slot.draw(guiGraphics, (xStart - x * 18) - 1, (yStart + y * 18) - 1);
					x--;
				}
				y++;
			}
		}

		{
			List<Object> outputs = new ArrayList<>(recipe.itemOutputs);
			if (!recipe.fluidOutput.isEmpty())
				outputs.add(recipe.fluidOutput);
			List<List<Object>> outputRows = Lists.partition(outputs, 2);
			int y = 0;
			int xStart = (getWidth() / 2) + (arrow.getWidth() / 2) + 8;
			int yStart = (getHeight() / 2) - (Math.max(1, outputRows.size()) * 9) + 1;
			for (List<Object> row : outputRows)
			{
				int x = row.size() - 1;
				for (Object ignored : row)
				{
					slot.draw(guiGraphics, xStart + x * 18, yStart + y * 18);
					x--;
				}
				y++;
			}
		}

	}

	private record FluidTooltipCallback(int amount) implements IRecipeSlotTooltipCallback
	{
		@Override
		public void onTooltip(@Nonnull IRecipeSlotView recipeSlotView, @Nonnull List<Component> tooltip)
		{
			tooltip.add(Component.translatable(LangHelper.prependModId("jei.piston_crafting.tooltip.fluid_amount"), String.valueOf(amount)));
			String classifier = recipeSlotView.getRole() == RecipeIngredientRole.INPUT ? "input" : "output";
			tooltip.add(Component.translatable(LangHelper.prependModId("jei.piston_crafting.tooltip.fluid_" + classifier)).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
		}
	}
}
