package squeek.veganoption.content.modifiers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
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
	// TODO: Split these different <ItemStack, String> maps into modules for more fine-grained control
	public HashMap<ItemStack, String> itemToOreDictConversions = new HashMap<ItemStack, String>();
	public HashMap<ItemStack, String> itemToOreDictConversionsForFoodOutputs = new HashMap<ItemStack, String>();
	public HashMap<ItemStack, String> itemToOreDictConversionsForNonFoodOutputs = new HashMap<ItemStack, String>();
	public HashMap<String, String> oreDictToOreDictConversions = new HashMap<String, String>();
	public List<ItemStack> excludedRecipeOutputs = new ArrayList<ItemStack>();
	public List<RecipeModification> customModifications = new ArrayList<RecipeModification>();

	public abstract static class RecipeModification
	{
		public abstract IRecipe modify(IRecipe recipe);
	}

	public void convertInput(ItemStack inputToConvert, String oreDictEntry)
	{
		itemToOreDictConversions.put(inputToConvert, oreDictEntry);
	}

	public void convertInputForFoodOutput(ItemStack inputToConvert, String oreDictEntry)
	{
		itemToOreDictConversionsForFoodOutputs.put(inputToConvert, oreDictEntry);
	}

	public void convertInputForNonFoodOutput(ItemStack inputToConvert, String oreDictEntry)
	{
		itemToOreDictConversionsForNonFoodOutputs.put(inputToConvert, oreDictEntry);
	}

	public void convertOreDict(String oreDictFrom, String oreDictTo)
	{
		oreDictToOreDictConversions.put(oreDictFrom, oreDictTo);
	}

	public void excludeOutput(ItemStack outputToExclude)
	{
		excludedRecipeOutputs.add(outputToExclude);
	}

	public void addCustomModification(RecipeModification recipeMod)
	{
		customModifications.add(recipeMod);
	}

	public void replaceRecipes()
	{
		long millisecondsStart = System.currentTimeMillis();

		@SuppressWarnings("unchecked")
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		List<IRecipe> recipesToRemove = new ArrayList<IRecipe>();
		List<IRecipe> recipesToAdd = new ArrayList<IRecipe>();
		int recipesConverted = 0;

		for (IRecipe recipe : recipes)
		{
			IRecipe convertedRecipe = convertRecipe(recipe);
			boolean didMakeModification = convertedRecipe != null;
			IRecipe recipeToConvert = convertedRecipe != null ? convertedRecipe : recipe;

			for (RecipeModification customModification : customModifications)
			{
				convertedRecipe = customModification.modify(recipeToConvert);

				if (convertedRecipe != null)
				{
					recipeToConvert = convertedRecipe;
					didMakeModification = true;
				}
				else
					convertedRecipe = recipeToConvert;
			}

			if (!didMakeModification)
				continue;

			if (convertedRecipe != recipe)
			{
				recipesToRemove.add(recipe);
				recipesToAdd.add(convertedRecipe);
			}
			recipesConverted++;
		}

		recipes.removeAll(recipesToRemove);
		recipes.addAll(recipesToAdd);

		long timeSpentInMilliseconds = System.currentTimeMillis() - millisecondsStart;
		String timeTakenString = "took " + (timeSpentInMilliseconds / 1000.0f) + " seconds";
		VeganOption.Log.info("Replaced " + recipesConverted + " recipes with OreDictionary'd equivalents (" + timeTakenString + ")");
	}

	public IRecipe convertRecipe(IRecipe recipe)
	{
		ItemStack output = recipe.getRecipeOutput();

		if (output == null || containsMatch(excludedRecipeOutputs, output))
		{
			return null;
		}

		IRecipe convertedRecipe = convertRecipe(recipe, itemToOreDictConversions);

		if (isFood(output))
		{
			IRecipe recipeToConvert = convertedRecipe != null ? convertedRecipe : recipe;
			IRecipe convertedFoodRecipe = convertRecipe(recipeToConvert, itemToOreDictConversionsForFoodOutputs);
			convertedRecipe = convertedFoodRecipe != null ? convertedFoodRecipe : convertedRecipe;
		}
		else
		{
			IRecipe recipeToConvert = convertedRecipe != null ? convertedRecipe : recipe;
			IRecipe convertedNonFoodRecipe = convertRecipe(recipeToConvert, itemToOreDictConversionsForNonFoodOutputs);
			convertedRecipe = convertedNonFoodRecipe != null ? convertedNonFoodRecipe : convertedRecipe;
		}

		return convertedRecipe;
	}

	public IRecipe convertRecipe(IRecipe recipe, Map<ItemStack, String> itemToOredictMap)
	{
		if (recipe.getClass() == ShapedRecipes.class)
		{
			return convertShapedRecipe((ShapedRecipes) recipe, itemToOredictMap);
		}
		else if (recipe.getClass() == ShapelessRecipes.class)
		{
			return convertShapelessRecipe((ShapelessRecipes) recipe, itemToOredictMap);
		}
		else if (recipe.getClass() == ShapedOreRecipe.class)
		{
			return convertShapedOreRecipe((ShapedOreRecipe) recipe, itemToOredictMap);
		}
		else if (recipe.getClass() == ShapelessOreRecipe.class)
		{
			return convertShapelessOreRecipe((ShapelessOreRecipe) recipe, itemToOredictMap);
		}
		return null;
	}

	public IRecipe convertShapedRecipe(ShapedRecipes recipe, Map<ItemStack, String> itemToOredictMap)
	{
		if (containsMatch(itemToOredictMap.keySet(), recipe.recipeItems))
		{
			try
			{
				ShapedOreRecipe oreRecipe = shapedOreRecipeReplaceConstructor.newInstance(recipe, itemToOredictMap);
				// the replace constructor doesn't take care of all cases that our conversion does,
				// so run it through the conversion again
				convertRecipe(oreRecipe);
				return oreRecipe;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public IRecipe convertShapelessRecipe(ShapelessRecipes recipe, Map<ItemStack, String> itemToOredictMap)
	{
		ItemStack[] recipeItems = recipe.recipeItems.toArray(new ItemStack[recipe.recipeItems.size()]);
		if (containsMatch(itemToOredictMap.keySet(), recipeItems))
		{
			try
			{
				ShapelessOreRecipe oreRecipe = shapelessOreRecipeReplaceConstructor.newInstance(recipe, itemToOredictMap);
				// the replace constructor doesn't take care of all cases that our conversion does,
				// so run it through the conversion again
				convertRecipe(oreRecipe);
				return oreRecipe;
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public IRecipe convertShapedOreRecipe(ShapedOreRecipe recipe, Map<ItemStack, String> itemToOredictMap)
	{
		Object[] inputs = recipe.getInput();
		boolean inputReplaced = false;
		for (int i = 0; i < inputs.length; i++)
		{
			Object inputObj = inputs[i];
			if (inputObj instanceof ItemStack && containsMatch(itemToOredictMap.keySet(), (ItemStack) inputObj))
			{
				inputs[i] = OreDictionary.getOres(getConversionFor((ItemStack) inputObj, itemToOredictMap));
				inputReplaced = true;
			}
			else if (inputObj instanceof ArrayList)
			{
				@SuppressWarnings("unchecked")
				ArrayList<ItemStack> inputOres = (ArrayList<ItemStack>) inputObj;
				String currentOreDict = findMatchingOreDict(inputOres, oreDictToOreDictConversions.keySet());
				if (currentOreDict != null)
				{
					String newOreDict = oreDictToOreDictConversions.get(currentOreDict);
					inputs[i] = OreDictionary.getOres(newOreDict);
					inputReplaced = true;
				}
			}
		}
		return inputReplaced ? recipe : null;
	}

	public IRecipe convertShapelessOreRecipe(ShapelessOreRecipe recipe, Map<ItemStack, String> itemToOredictMap)
	{
		List<Object> inputsToRemove = new ArrayList<Object>();
		List<Object> inputsToAdd = new ArrayList<Object>();

		ArrayList<Object> inputs = recipe.getInput();
		for (Object inputObj : inputs)
		{
			if (inputObj instanceof ItemStack && containsMatch(itemToOredictMap.keySet(), (ItemStack) inputObj))
			{
				inputsToRemove.add(inputObj);
				inputsToAdd.add(OreDictionary.getOres(getConversionFor((ItemStack) inputObj, itemToOredictMap)));
			}
			else if (inputObj instanceof ArrayList)
			{
				@SuppressWarnings("unchecked")
				ArrayList<ItemStack> inputOres = (ArrayList<ItemStack>) inputObj;
				String currentOreDict = findMatchingOreDict(inputOres, oreDictToOreDictConversions.keySet());
				if (currentOreDict != null)
				{
					String newOreDict = oreDictToOreDictConversions.get(currentOreDict);
					inputsToRemove.add(inputObj);
					inputsToAdd.add(OreDictionary.getOres(newOreDict));
				}
			}
		}

		if (inputsToRemove.size() > 0)
		{
			inputs.removeAll(inputsToRemove);
			inputs.addAll(inputsToAdd);
			return recipe;
		}

		return null;
	}

	private String findMatchingOreDict(Collection<ItemStack> inputOres, Collection<String> oreDicts)
	{
		for (String oreDict : oreDicts)
		{
			if (inputOres == OreDictionary.getOres(oreDict))
				return oreDict;
		}
		return null;
	}

	private String getConversionFor(ItemStack itemStack, Map<ItemStack, String> itemToOreDictConversions)
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

	private boolean containsMatch(Collection<ItemStack> inputs, ItemStack... targets)
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

	// TODO: Optionally use the AppleCore API method
	public boolean isFood(ItemStack itemStack)
	{
		if (itemStack == null || itemStack.getItem() == null)
			return false;

		return itemStack.getItem() instanceof ItemFood || itemStack.getItem() == Items.CAKE;
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
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
		catch (SecurityException e)
		{
			throw new RuntimeException(e);
		}
	}
}
