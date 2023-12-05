package squeek.veganoption.content.modifiers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import squeek.veganoption.VeganOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeModifier
{
	public Map<Ingredient, Ingredient> genericConversions = new HashMap<>();
	public Map<Ingredient, Ingredient> foodOutputConversions = new HashMap<>();
	public Map<Ingredient, Ingredient> notFoodOutputConversions = new HashMap<>();
	public List<Item> excludedRecipeOutputs = new ArrayList<>();

	public void convertInput(Ingredient toConvert, Ingredient replacement)
	{
		genericConversions.put(toConvert, replacement);
	}

	public void convertInputForFood(Ingredient toConvert, Ingredient replacement)
	{
		foodOutputConversions.put(toConvert, replacement);
	}

	public void convertInputForNonFood(Ingredient toConvert, Ingredient replacement)
	{
		notFoodOutputConversions.put(toConvert, replacement);
	}

	public void excludeOutput(Item outputToExclude)
	{
		excludedRecipeOutputs.add(outputToExclude);
	}

	public void replaceRecipes()
	{
		long millisecondsStart = System.currentTimeMillis();
		int recipesConverted = 0;

		// todo

		long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
		String timeTakenString = "took " + (timeSpentInMilliseconds / 1000.0f) + " seconds";
		VeganOption.Log.info("Replaced " + recipesConverted + " recipes with OreDictionary'd equivalents (" + timeTakenString + ")");
	}

	public Recipe convertRecipe(Recipe recipe)
	{
		//todo

		return recipe;
	}

	// TODO: Optionally use the AppleCore API method
	public boolean isFood(ItemStack itemStack)
	{
		if (itemStack.isEmpty())
			return false;

		return itemStack.getItem().isEdible() || itemStack.getItem() == Items.CAKE;
	}
}
