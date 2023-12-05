package squeek.veganoption.content.registry;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.helpers.FluidHelper;

import java.util.ArrayList;
import java.util.List;

public class PistonCraftingRegistry
{
	protected static List<PistonCraftingRecipe> pistonRecipes = new ArrayList<PistonCraftingRecipe>();

	public static List<PistonCraftingRecipe> getRecipes()
	{
		return pistonRecipes;
	}

	public static void register(PistonCraftingRecipe pistonRecipe)
	{
		pistonRecipes.add(pistonRecipe);
	}

	public static List<PistonCraftingRecipe> getSubsetByInput(ItemStack search)
	{
		List<PistonCraftingRecipe> matchingRecipes = new ArrayList<>();
		for (PistonCraftingRecipe recipe : getRecipes())
		{
			if (recipe.fluidInput != FluidStack.EMPTY && recipe.fluidInput.isFluidEqual(FluidHelper.fromItemStack(search)))
				matchingRecipes.add(recipe);
			else
			{
				for (InputItemStack recipeInput : recipe.itemInputs)
				{
					if (recipeInput.matches(search))
					{
						matchingRecipes.add(recipe);
						break;
					}
				}
			}
		}
		return matchingRecipes;
	}

	public static List<PistonCraftingRecipe> getSubsetByInput(FluidStack search)
	{
		return getSubsetByInput(FluidHelper.toItemStack(search));
	}

	public static List<PistonCraftingRecipe> getSubsetByOutput(ItemStack search)
	{
		List<PistonCraftingRecipe> matchingRecipes = new ArrayList<PistonCraftingRecipe>();
		for (PistonCraftingRecipe recipe : getRecipes())
		{
			if (recipe.fluidOutput != FluidStack.EMPTY && recipe.fluidOutput.isFluidEqual(FluidHelper.fromItemStack(search)))
				matchingRecipes.add(recipe);
			else
			{
				for (ItemStack recipeOutput : recipe.itemOutputs)
				{
					if (recipeOutput.is(search.getItem()))
					{
						matchingRecipes.add(recipe);
						break;
					}
				}
			}
		}
		return matchingRecipes;
	}

	public static List<PistonCraftingRecipe> getSubsetByOutput(FluidStack search)
	{
		return getSubsetByOutput(FluidHelper.toItemStack(search));
	}
}
