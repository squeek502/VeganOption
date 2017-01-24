package squeek.veganoption.integration.jei.piston;

import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;

import java.util.ArrayList;
import java.util.List;

public class PistonRecipeMaker
{
	public static List<PistonRecipeWrapper> getRecipes()
	{
		List<PistonRecipeWrapper> recipes = new ArrayList<PistonRecipeWrapper>();
		for (PistonCraftingRecipe recipe : PistonCraftingRegistry.getRecipes())
		{
			recipes.add(new PistonRecipeWrapper(recipe));
		}
		return recipes;
	}
}
