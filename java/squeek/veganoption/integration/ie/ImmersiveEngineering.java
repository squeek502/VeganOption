package squeek.veganoption.integration.ie;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.modules.FrozenBubble;
import squeek.veganoption.content.modules.Ink;
import squeek.veganoption.content.modules.VegetableOil;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.integration.IntegratorBase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ImmersiveEngineering extends IntegratorBase
{
	private static final int STANDARD_MIXER_ENGERGY_USAGE = 3200;
	private static final int STANDARD_CRUSHER_ENGERGY_USAGE = 2400;
	private static final int HEMP_SEED_META = 0;

	@Override
	public void oredict()
	{
		super.oredict();

		Item seed = getItem("seed");
		ItemStack hempSeeds = new ItemStack(seed, 1, HEMP_SEED_META);
		OreDictionary.registerOre(ContentHelper.plantMilkSourceOreDict, hempSeeds.copy());
		OreDictionary.registerOre(ContentHelper.vegetableOilSourceOreDict, hempSeeds.copy());
	}

	@Override
	public void recipes()
	{
		super.recipes();

		// Mixer recipes, using Piston Crafting's registry
		for (PistonCraftingRecipe recipe : PistonCraftingRegistry.getRecipes())
		{
			if (recipe.fluidInput == null || recipe.fluidOutput == null)
				continue;

			List<InputItemStack> itemInputs = recipe.itemInputs;
			List<Object> mixerInputs = new ArrayList<Object>();
			for (InputItemStack itemInput : itemInputs)
			{
				if (!itemInput.isOreDict())
				{
					// need to break up any stacked items into individual stacks before sending to IE
					ItemStack singleInput = itemInput.wrappedItemStack.copy();
					singleInput.stackSize = 1;
					for (int i = 0; i < itemInput.stackSize(); i++)
					{
						mixerInputs.add(singleInput);
					}
				}
				else
				{
					for (int i = 0; i < itemInput.stackSize(); i++)
					{
						mixerInputs.add(itemInput.oreDictItemStacks);
					}
				}
			}

			addMixerRecipe(
				recipe.fluidOutput.copy(),
				recipe.fluidInput.copy(),
				mixerInputs.toArray(),
				STANDARD_MIXER_ENGERGY_USAGE
			);
		}

		// Bottling Machine
		addStandardBottlingMachineRecipe(new ItemStack(FrozenBubble.soapSolution), FrozenBubble.fluidSoapSolution);
		addStandardBottlingMachineRecipe(new ItemStack(Ink.blackVegetableOilInk), Ink.blackInkFluid);
		addStandardBottlingMachineRecipe(new ItemStack(Ink.whiteVegetableOilInk), Ink.whiteInkFluid);
		addStandardBottlingMachineRecipe(new ItemStack(VegetableOil.oilVegetable), VegetableOil.fluidVegetableOil);

		// Crusher, using Piston Crafting's registry
		for (PistonCraftingRecipe recipe : PistonCraftingRegistry.getRecipes())
		{
			if (recipe.fluidInput != null || recipe.fluidOutput != null)
				continue;

			if (recipe.itemInputs.size() != 1 || recipe.itemOutputs.size() != 1)
				continue;

			InputItemStack input = recipe.itemInputs.get(0);
			ItemStack output = recipe.itemOutputs.get(0);

			if (input.stackSize() != 1)
				continue;

			addCrusherRecipe(output, input.isOreDict() ? input.oreDictItemStacks : input.wrappedItemStack, STANDARD_CRUSHER_ENGERGY_USAGE);
		}
	}

	private void addMixerRecipe(FluidStack output, FluidStack input, Object[] itemInputs, int energy)
	{
		if (mixerAddRecipe == null)
			return;

		try
		{
			mixerAddRecipe.invoke(null, output, input, itemInputs, energy);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void addStandardBottlingMachineRecipe(ItemStack output, Fluid input)
	{
		addBottlingMachineRecipe(output, new ItemStack(Items.GLASS_BOTTLE), new FluidStack(input, Fluid.BUCKET_VOLUME));
	}

	private void addBottlingMachineRecipe(ItemStack output, Object input, FluidStack fluidInput)
	{
		if (bottlingMachineAddRecipe == null)
			return;

		try
		{
			bottlingMachineAddRecipe.invoke(null, output, input, fluidInput);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void addCrusherRecipe(ItemStack output, Object input, int energy)
	{
		if (crusherAddRecipe == null)
			return;

		try
		{
			crusherAddRecipe.invoke(null, output, input, energy);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private static final String mixerRecipeClassName = "blusunrize.immersiveengineering.api.crafting.MixerRecipe";
	private static final String bottlingMachineRecipeClassName = "blusunrize.immersiveengineering.api.crafting.BottlingMachineRecipe";
	private static final String crusherRecipeClassName = "blusunrize.immersiveengineering.api.crafting.CrusherRecipe";
	private static Method mixerAddRecipe = null;
	private static Method bottlingMachineAddRecipe = null;
	private static Method crusherAddRecipe = null;

	static
	{
		try
		{
			Class<?> mixerRecipeClass = Class.forName(mixerRecipeClassName);
			mixerAddRecipe = mixerRecipeClass.getDeclaredMethod("addRecipe", FluidStack.class, FluidStack.class, Object[].class, int.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			Class<?> mixerRecipeClass = Class.forName(bottlingMachineRecipeClassName);
			bottlingMachineAddRecipe = mixerRecipeClass.getDeclaredMethod("addRecipe", ItemStack.class, Object.class, FluidStack.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			Class<?> crusherRecipeClass = Class.forName(crusherRecipeClassName);
			crusherAddRecipe = crusherRecipeClass.getDeclaredMethod("addRecipe", ItemStack.class, Object.class, int.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
