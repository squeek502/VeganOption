package squeek.veganoption.content.modifiers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import squeek.veganoption.VeganOption;
import squeek.veganoption.helpers.MiscHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RecipeModifier
{
	public Map<Supplier<Ingredient>, Supplier<Ingredient>> genericConversions = new HashMap<>();
	public Map<Supplier<Ingredient>, Supplier<Ingredient>> foodOutputConversions = new HashMap<>();
	public Map<Supplier<Ingredient>, Supplier<Ingredient>> notFoodOutputConversions = new HashMap<>();
	public List<Item> excludedRecipeOutputs = new ArrayList<>();
	public List<CraftingRecipe> recipes = new ArrayList<>();
	public List<RecipeHolder<?>> convertedRecipeHolders = new ArrayList<>();

	public void convertInput(Supplier<Ingredient> toConvert, Supplier<Ingredient> replacement)
	{
		genericConversions.put(toConvert, replacement);
	}

	public void convertInputForFood(Supplier<Ingredient> toConvert, Supplier<Ingredient> replacement)
	{
		foodOutputConversions.put(toConvert, replacement);
	}

	public void convertInputForNonFood(Supplier<Ingredient> toConvert, Supplier<Ingredient> replacement)
	{
		notFoodOutputConversions.put(toConvert, replacement);
	}

	public void excludeOutput(Item outputToExclude)
	{
		excludedRecipeOutputs.add(outputToExclude);
	}

	@SubscribeEvent
	public void convertRecipes(TagsUpdatedEvent event)
	{
		long millisecondsStart = System.currentTimeMillis();
		int recipesConverted = 0;

		RecipeManager manager = MiscHelper.getRecipeManager();
		recipes = new ArrayList<>();
		for (RecipeHolder<?> holder : manager.getRecipes())
		{
			Recipe<?> recipe = holder.value();
			if (shouldConvert(recipe))
			{
				convertedRecipeHolders.add(holder);
				recipes.add(convertRecipe((CraftingRecipe) recipe));
				recipesConverted++;
			}
		}

		long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
		String timeTakenString = "took " + (timeSpentInMilliseconds / 1000.0f) + " seconds";
		VeganOption.Log.info("Added " + recipesConverted + " Vegan Option conversion recipes (" + timeTakenString + ")");
	}

	public CraftingRecipe convertRecipe(CraftingRecipe recipe)
	{
		if (recipe instanceof ShapelessRecipe s)
		{
			boolean isFood = isFood(s.result);

			return new ShapelessRecipe(s.getGroup(), s.category(), s.result, createNewIngredientList(s.getIngredients(), isFood));
		}
		else if (recipe instanceof ShapedRecipe s)
		{
			boolean isFood = isFood(s.result);

			return new ShapedRecipe(s.getGroup(), s.category(), s.getWidth(), s.getHeight(), createNewIngredientList(s.getIngredients(), isFood), s.result, s.showNotification());
		}
		return recipe;
	}

	// TODO: Optionally use the AppleCore API method
	public boolean isFood(ItemStack itemStack)
	{
		if (itemStack.isEmpty())
			return false;

		return itemStack.getItem().isEdible() || itemStack.getItem() == Items.CAKE;
	}

	public boolean shouldConvert(Recipe<?> recipe)
	{
		if (recipe instanceof ShapelessRecipe s)
		{
			if (excludedRecipeOutputs.contains(s.result.getItem()))
				return false;
		}
		else if (recipe instanceof ShapedRecipe s)
		{
			if (excludedRecipeOutputs.contains(s.result.getItem()))
				return false;
		}
		else
		{
			return false;
		}

		for (int i = 0; i < recipe.getIngredients().size(); i++)
		{
			Ingredient ingredient = recipe.getIngredients().get(i);
			if (ingredient.isEmpty())
				continue;
			if (findMatchingIngredient(genericConversions, ingredient) != null || findMatchingIngredient(foodOutputConversions, ingredient) != null || findMatchingIngredient(notFoodOutputConversions, ingredient) != null)
				return true;
		}
		return false;
	}

	private Ingredient findIngredientOrSelf(Ingredient in, boolean outputIsFood)
	{
		if (outputIsFood)
		{
			Ingredient foodIngredient = findMatchingIngredient(foodOutputConversions, in);
			if (foodIngredient != null)
			{
				return foodIngredient;
			}
			Ingredient genericIngredient = findMatchingIngredient(genericConversions, in);
			if (genericIngredient != null)
			{
				return genericIngredient;
			}
		}
		else
		{
			Ingredient nonFoodIngredient = findMatchingIngredient(notFoodOutputConversions, in);
			if (nonFoodIngredient != null)
			{
				return nonFoodIngredient;
			}
			Ingredient genericIngredient = findMatchingIngredient(genericConversions, in);
			if (genericIngredient != null)
			{
				return genericIngredient;
			}
		}
		return in;
	}

	private NonNullList<Ingredient> createNewIngredientList(NonNullList<Ingredient> oldIngredientList, boolean isOutputFood)
	{
		NonNullList<Ingredient> newIngredients = NonNullList.create();

		for (Ingredient i : oldIngredientList)
		{
			newIngredients.add(findIngredientOrSelf(i, isOutputFood));
		}
		return newIngredients;
	}

	@Nullable
	private Ingredient findMatchingIngredient(Map<Supplier<Ingredient>, Supplier<Ingredient>> map, Ingredient toMatch)
	{
		for (Map.Entry<Supplier<Ingredient>, Supplier<Ingredient>> entry : map.entrySet())
		{
			if (entry.getKey().get().equals(toMatch))
				return entry.getValue().get();
		}
		return null;
	}
}
