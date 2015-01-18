package squeek.veganoption.content.modifiers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.VeganOption;
import squeek.veganoption.helpers.MiscHelper;

public class RecipeModifier
{
	public HashMap<ItemStack, String> itemToOreDictConversions = new HashMap<ItemStack, String>();
	public List<ItemStack> excludedRecipeOutputs = new ArrayList<ItemStack>();

	public void convertInput(ItemStack inputToConvert, String oreDictEntry)
	{
		itemToOreDictConversions.put(inputToConvert, oreDictEntry);
	}

	public void excludeOutput(ItemStack outputToExclude)
	{
		excludedRecipeOutputs.add(outputToExclude);
	}

	public void replaceRecipes()
	{
		@SuppressWarnings("unchecked")
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		List<IRecipe> recipesToRemove = new ArrayList<IRecipe>();
		List<IRecipe> recipesToAdd = new ArrayList<IRecipe>();
		List<ItemStack> replaceStacks = new ArrayList<ItemStack>(itemToOreDictConversions.keySet());
		int oreRecipesReplaced = 0;

		// Search vanilla recipes for recipes to replace
		for (Object obj : recipes)
		{
			if (obj instanceof ShapedRecipes)
			{
				ShapedRecipes recipe = (ShapedRecipes) obj;
				ItemStack output = recipe.getRecipeOutput();
				if (output != null && containsMatch(excludedRecipeOutputs, output))
				{
					continue;
				}

				if (containsMatch(replaceStacks, recipe.recipeItems))
				{
					try
					{
						ShapedOreRecipe oreRecipe = shapedOreRecipeReplaceConstructor.newInstance(recipe, itemToOreDictConversions);
						convertOreRecipe(oreRecipe, replaceStacks);
						recipesToAdd.add(oreRecipe);
						recipesToRemove.add(recipe);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (obj instanceof ShapelessRecipes)
			{
				ShapelessRecipes recipe = (ShapelessRecipes) obj;
				ItemStack output = recipe.getRecipeOutput();
				if (output != null && containsMatch(excludedRecipeOutputs, output))
				{
					continue;
				}

				@SuppressWarnings("unchecked")
				ItemStack[] recipeItems = (ItemStack[]) recipe.recipeItems.toArray(new ItemStack[recipe.recipeItems.size()]);
				if (containsMatch(replaceStacks, recipeItems))
				{
					try
					{
						ShapelessOreRecipe oreRecipe = shapelessOreRecipeReplaceConstructor.newInstance(recipe, itemToOreDictConversions);
						convertOreRecipe(oreRecipe, replaceStacks);
						recipesToAdd.add(oreRecipe);
						recipesToRemove.add(recipe);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			else if (convertOreRecipe(obj, replaceStacks))
			{
				oreRecipesReplaced++;
			}
		}

		recipes.removeAll(recipesToRemove);
		recipes.addAll(recipesToAdd);
		if (recipesToRemove.size() + oreRecipesReplaced > 0)
		{
			VeganOption.Log.info("Replaced " + (recipesToRemove.size() + oreRecipesReplaced) + " recipes with OreDictionary'd equivalents");
		}
	}

	public boolean convertOreRecipe(Object obj, List<ItemStack> replaceStacks)
	{
		if (obj instanceof ShapedOreRecipe)
		{
			ShapedOreRecipe recipe = (ShapedOreRecipe) obj;
			ItemStack output = recipe.getRecipeOutput();
			if (output != null && containsMatch(excludedRecipeOutputs, output))
			{
				return false;
			}

			Object[] inputs = recipe.getInput();
			boolean inputReplaced = false;
			for (int i = 0; i < inputs.length; i++)
			{
				Object inputObj = inputs[i];
				if (inputObj instanceof ItemStack && containsMatch(replaceStacks, (ItemStack) inputObj))
				{
					inputs[i] = OreDictionary.getOres(getConversionFor((ItemStack) inputObj));
					inputReplaced = true;
				}
			}
			if (inputReplaced)
				return true;
		}
		else if (obj instanceof ShapelessOreRecipe)
		{
			ShapelessOreRecipe recipe = (ShapelessOreRecipe) obj;
			ItemStack output = recipe.getRecipeOutput();
			if (output != null && containsMatch(excludedRecipeOutputs, output))
			{
				return false;
			}

			List<ItemStack> inputsToRemove = new ArrayList<ItemStack>();
			List<ArrayList<ItemStack>> inputsToAdd = new ArrayList<ArrayList<ItemStack>>();

			ArrayList<Object> inputs = recipe.getInput();
			for (Object inputObj : inputs)
			{
				if (inputObj instanceof ItemStack && containsMatch(replaceStacks, (ItemStack) inputObj))
				{
					inputsToRemove.add((ItemStack) inputObj);
					inputsToAdd.add(OreDictionary.getOres(getConversionFor((ItemStack) inputObj)));
				}
			}

			if (inputsToRemove.size() > 0)
			{
				inputs.removeAll(inputsToRemove);
				inputs.addAll(inputsToAdd);
				return true;
			}
		}
		return false;
	}

	private String getConversionFor(ItemStack itemStack)
	{
		String bestMatch = null;
		for (Entry<ItemStack, String> conversion : itemToOreDictConversions.entrySet())
		{
			if (conversion.getKey().isItemEqual(itemStack))
			{
				return conversion.getValue();
			}

			if (MiscHelper.wildcardItemStacksMatch(conversion.getKey(), itemStack))
			{
				bestMatch = conversion.getValue();
			}
		}
		return bestMatch;
	}

	private boolean containsMatch(List<ItemStack> inputs, ItemStack... targets)
	{
		for (ItemStack input : inputs)
		{
			for (ItemStack target : targets)
			{
				if (MiscHelper.wildcardItemStacksMatch(target, input))
				{
					return true;
				}
			}
		}
		return false;
	}

	// reflection
	public static Constructor<ShapedOreRecipe> shapedOreRecipeReplaceConstructor = null;
	public static Constructor<ShapelessOreRecipe> shapelessOreRecipeReplaceConstructor = null;
	static
	{
		try
		{
			shapedOreRecipeReplaceConstructor = ShapedOreRecipe.class.getDeclaredConstructor(ShapedRecipes.class, Map.class);
			shapedOreRecipeReplaceConstructor.setAccessible(true);
			shapelessOreRecipeReplaceConstructor = ShapelessOreRecipe.class.getDeclaredConstructor(ShapelessRecipes.class, Map.class);
			shapelessOreRecipeReplaceConstructor.setAccessible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
