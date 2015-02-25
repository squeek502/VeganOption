package squeek.veganoption.content.registry;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.helpers.FluidHelper;

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
		List<PistonCraftingRecipe> matchingRecipes = new ArrayList<PistonCraftingRecipe>();
		for (PistonCraftingRecipe recipe : getRecipes())
		{
			if (recipe.fluidInput != null && recipe.fluidInput.isFluidEqual(FluidHelper.fromItemStack(search)))
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
			if (recipe.fluidOutput != null && recipe.fluidInput.isFluidEqual(FluidHelper.fromItemStack(search)))
				matchingRecipes.add(recipe);
			else
			{
				for (ItemStack recipeOutput : recipe.itemOutputs)
				{
					if (recipeOutput.isItemEqual(search))
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
