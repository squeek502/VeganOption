package squeek.veganoption.integration.jei.piston;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.ModInfo;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.jei.VOPlugin;

import javax.annotation.Nonnull;
import java.util.List;

public class PistonRecipeCategory extends BlankRecipeCategory<PistonRecipeWrapper>
{
	private final String localizedName;
	private final IDrawable background;

	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot1 = 1;

	public static final int width = 116;
	public static final int height = 54;

	private final ICraftingGridHelper craftingGridHelper;

	public PistonRecipeCategory(IGuiHelper guiHelper)
	{
		ResourceLocation location = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");
		background = guiHelper.createDrawable(location, 29, 16, width, height);
		localizedName = LangHelper.translate("jei.pistonCrafting");
		craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);
	}

	@Override
	@Nonnull
	public String getUid()
	{
		return VOPlugin.VORecipeCategoryUid.PISTON;
	}

	@Override
	@Nonnull
	public String getTitle()
	{
		return localizedName;
	}

	@Nonnull
	@Override
	public String getModName()
	{
		return ModInfo.MODID;
	}

	@Override
	@Nonnull
	public IDrawable getBackground()
	{
		return background;
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull PistonRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		guiFluidStacks.addTooltipCallback(fluidTooltipHandler);

		if (recipeWrapper.recipe.fluidOutput != null)
			guiFluidStacks.init(craftOutputSlot, false, 95, 19, 16, 16, 1000, false, null);
		else
			guiItemStacks.init(craftOutputSlot, false, 94, 18);

		for (int y = 0; y < 3; ++y)
		{
			for (int x = 0; x < 3; ++x)
			{
				int index = craftInputSlot1 + x + (y * 3);
				if (index == craftInputSlot1 && recipeWrapper.recipe.fluidInput != null)
					guiFluidStacks.init(index, true, x * 18 + 1, y * 18 + 1, 16, 16, recipeWrapper.recipe.fluidInput.amount, false, null);
				else
					guiItemStacks.init(index, true, x * 18, y * 18);
			}
		}

		guiItemStacks.set(ingredients);
		guiFluidStacks.set(ingredients);
	}

	public static class FluidTooltipCallback implements ITooltipCallback<FluidStack>
	{
		@Override
		public void onTooltip(int slotIndex, boolean input, @Nonnull FluidStack ingredient, @Nonnull List<String> tooltip)
		{
			String tooltipStr;
			if (input)
				tooltipStr = LangHelper.translate("jei.pistonCrafting.tooltip.fluidInput");
			else
				tooltipStr = LangHelper.translate("jei.pistonCrafting.tooltip.fluidOutput");

			tooltip.add(1, TextFormatting.GOLD + TextFormatting.ITALIC.toString() + tooltipStr + TextFormatting.RESET);
		}
	}
	private static FluidTooltipCallback fluidTooltipHandler = new FluidTooltipCallback();
}
