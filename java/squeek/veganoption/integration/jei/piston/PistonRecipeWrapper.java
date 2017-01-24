package squeek.veganoption.integration.jei.piston;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PistonRecipeWrapper extends BlankRecipeWrapper
{
	public final PistonCraftingRecipe recipe;

	public PistonRecipeWrapper(PistonCraftingRecipe recipe)
	{
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients)
	{
		if (recipe.fluidInput != null)
			ingredients.setInput(FluidStack.class, recipe.fluidInput);
		if (!recipe.itemInputs.isEmpty())
		{
			List<List<ItemStack>> inputStacks = new ArrayList<List<ItemStack>>();
			for (InputItemStack input : recipe.itemInputs)
			{
				inputStacks.add(input.getItemStackList());
			}
			ingredients.setInputLists(ItemStack.class, inputStacks);
		}
		if (recipe.fluidOutput != null)
			ingredients.setOutput(FluidStack.class, recipe.fluidOutput);
		if (!recipe.itemOutputs.isEmpty())
		{
			ingredients.setOutputs(ItemStack.class, recipe.itemOutputs);
		}
	}
}
