package squeek.veganoption.integration.mystcraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.RecipeModifier.RecipeModification;
import squeek.veganoption.content.modules.Ink;
import squeek.veganoption.integration.IntegratorBase;

public class Mystcraft extends IntegratorBase
{
	public static final String blackInkFluidName = "myst.ink.black";
	public static final String inkVialItemName = "vial";
	public static Item inkVial;

	public static Fluid getBlackInkFluid()
	{
		return FluidRegistry.getFluid(blackInkFluidName);
	}

	@Override
	public void preInit()
	{
		inkVial = getItem(inkVialItemName);

		super.preInit();
	}

	@Override
	public void finish()
	{
		// register our ink as valid
		try 
		{
			Class<?> Mystcraft = Class.forName("com.xcompwiz.mystcraft.Mystcraft");
			Field validInksField = Mystcraft.getDeclaredField("validInks");
			@SuppressWarnings("unchecked")
			Set<String> validInks = (Set<String>) validInksField.get(null);
			validInks.add(Ink.blackInkFluid.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void recipes()
	{
		if (inkVial != null)
		{
			// this is unbelievably hacky and might break things down the line
			// as we're replacing the ArrayList that came from the OreDictionary 
			// with a new copy of it (without VO's ink included), meaning
			// that the recipe's list of valid items might not always be in sync with
			// the OreDictionary
			//
			// can't think of a better way to do this though, short of simply
			// forcing the ink sac as the only valid input
			RecipeModification removeVOInkAsInkVialInput = new RecipeModification()
			{
				@Override
				public IRecipe modify(IRecipe recipe)
				{
					if (recipe.getRecipeOutput() == null || recipe.getRecipeOutput().getItem() != inkVial)
						return null;

					if (!(recipe instanceof ShapelessOreRecipe))
						return null;

					ShapelessOreRecipe shapelessRecipe = (ShapelessOreRecipe) recipe;

					List<ArrayList<ItemStack>> inputsToRemove = new ArrayList<ArrayList<ItemStack>>(2);
					List<ArrayList<ItemStack>> inputsToAdd = new ArrayList<ArrayList<ItemStack>>(2);

					ArrayList<Object> inputs = shapelessRecipe.getInput();
					for (Object inputObj : inputs)
					{
						if (!(inputObj instanceof ArrayList))
							continue;

						ArrayList<ItemStack> dyeBlackCopyWithoutVOInk = new ArrayList<ItemStack>();
						boolean isDyeBlack = false;

						@SuppressWarnings("unchecked")
						ArrayList<ItemStack> inputOres = (ArrayList<ItemStack>) inputObj;
						for (Iterator<ItemStack> inputIterator = inputOres.iterator(); inputIterator.hasNext();)
						{
							ItemStack input = inputIterator.next();
							if (input != null && input.getItem() == Ink.blackVegetableOilInk)
							{
								isDyeBlack = true;
							}
							else
							{
								dyeBlackCopyWithoutVOInk.add(input);
							}
						}
						if (isDyeBlack)
						{
							inputsToRemove.add(inputOres);
							inputsToAdd.add(dyeBlackCopyWithoutVOInk);
						}
					}

					for (ArrayList<ItemStack> inputToRemove : inputsToRemove)
					{
						inputs.remove(inputToRemove);
					}
					for (ArrayList<ItemStack> inputToAdd : inputsToAdd)
					{
						inputs.add(0, inputToAdd);
					}

					return recipe;
				}
			};
			Modifiers.recipes.addCustomModification(removeVOInkAsInkVialInput);
		}
	}
}
