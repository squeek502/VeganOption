package squeek.veganoption.content.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Like ShapelessOreRecipe, but all OreDict inputs need to match with eachother
 *
 * For example, ShapelessMatchingOreRecipe(output, "slimeball", "slimeball") would
 * require both inputs to be the same item (both Resin or both Slimeball, for example);
 * a mixture (1 Resin, 1 Slimeball) would not work
 */
public class ShapelessMatchingOreRecipe extends ShapelessOreRecipe
{
	public Map<ArrayList<ItemStack>, Integer> requiredMatchingStacksByOreDictStacks = new HashMap<ArrayList<ItemStack>, Integer>();

	static
	{
		RecipeSorter.register(ModInfo.MODID_LOWER + ":shapelessmatchingore", ShapelessMatchingOreRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
	}

	@SuppressWarnings("unchecked")
	public ShapelessMatchingOreRecipe(ItemStack result, Object... recipe)
	{
		super(result, recipe);

		NonNullList<Object> inputs = getInput();

		for (int i = 0; i < inputs.size(); i++)
		{
			Object input = inputs.get(i);

			if (!(input instanceof ArrayList))
				continue;

			int numRequiredMatches = 1;

			for (int j = 0; j < inputs.size(); j++)
			{
				if (i == j)
					continue;

				Object testInput = inputs.get(j);

				if (testInput == input)
				{
					numRequiredMatches++;
				}
			}

			if (numRequiredMatches > 1)
			{
				requiredMatchingStacksByOreDictStacks.put((ArrayList<ItemStack>) input, numRequiredMatches);
			}
		}
	}

	@Override
	public boolean matches(InventoryCrafting var1, World world)
	{
		if (!super.matches(var1, world))
			return false;

		for (Entry<ArrayList<ItemStack>, Integer> entry : requiredMatchingStacksByOreDictStacks.entrySet())
		{
			ArrayList<ItemStack> requiredInputs = entry.getKey();
			ItemStack matchingInput = null;
			int numRequiredMatches = entry.getValue();
			int numMatches = 0;

			for (int x = 0; x < var1.getSizeInventory(); x++)
			{
				ItemStack slot = var1.getStackInSlot(x);

				if (!slot.isEmpty())
				{
					for (ItemStack requiredInput : requiredInputs)
					{
						if (OreDictionary.itemMatches(requiredInput, slot, false))
						{
							if (matchingInput == null)
								matchingInput = slot;

							if (!OreDictionary.itemMatches(slot, matchingInput, true))
								return false;

							numMatches++;
							break;
						}
					}
				}
			}

			if (numMatches != numRequiredMatches)
				return false;
		}

		return true;
	}
}
