package squeek.veganoption.content.recipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import squeek.veganoption.content.crafting.PistonCraftingHandler;
import squeek.veganoption.helpers.WorldHelper;

public class PistonCraftingRecipe
{
	public List<InputItemStack> itemInputs = new ArrayList<InputItemStack>();
	public FluidStack fluidInput = null;
	public List<ItemStack> itemOutputs = new ArrayList<ItemStack>();
	public FluidStack fluidOutput = null;
	protected Random rand = new Random();

	public PistonCraftingRecipe(Object output, Object... inputs)
	{
		this(new Object[]{output}, inputs);
	}

	public PistonCraftingRecipe(Object[] outputs, Object[] inputs)
	{
		for (Object input : inputs)
		{
			if (input instanceof InputItemStack)
				itemInputs.add((InputItemStack) input);
			else if (input instanceof String || input instanceof Item || input instanceof Block || input instanceof ItemStack)
				itemInputs.add(new InputItemStack(input));
			else if (input instanceof Fluid)
				fluidInput = new FluidStack((Fluid) input, FluidContainerRegistry.BUCKET_VOLUME);
			else if (input instanceof FluidStack)
				fluidInput = (FluidStack) input;
			else
				throw new RuntimeException("Unsupported PistonCraftingRecipe input: " + input);
		}
		for (Object output : outputs)
		{
			if (output instanceof Item)
				itemOutputs.add(new ItemStack((Item) output));
			else if (output instanceof Block)
				itemOutputs.add(new ItemStack((Block) output));
			else if (output instanceof ItemStack)
				itemOutputs.add((ItemStack) output);
			else if (output instanceof Fluid)
				fluidOutput = new FluidStack((Fluid) output, FluidContainerRegistry.BUCKET_VOLUME);
			else if (output instanceof FluidStack)
				fluidOutput = (FluidStack) output;
			else
				throw new RuntimeException("Unsupported PistonCraftingRecipe output: " + output);
		}
	}

	// TODO: This probably doesn't work right for multiple overlapping OreDict inputs.
	// It will likely think that the recipe works but it might use the same EntityItem
	// for multiple  different OreDict inputs, and bug out when it starts consuming stuff
	public boolean tryCraft(World world, int x, int y, int z)
	{
		IFluidHandler fluidHandler = getOutputFluidHandler(world, x, y, z);

		if (!canOutputFluid(fluidHandler))
			return false;

		PistonCraftingHandler.WorldPosition fluidPos = new PistonCraftingHandler.WorldPosition(world, x, y, z);
		FluidStack displacedFluid = PistonCraftingHandler.displacedLiquids.get(fluidPos);

		if (!fluidInputMatches(displacedFluid))
			return false;

		List<EntityItem> entityItemsWithin = WorldHelper.getItemEntitiesWithin(world, x, y, z);
		Map<InputItemStack, List<EntityItem>> entityItemsByInput = getEntityItemsByInput(itemInputs, entityItemsWithin);

		if (!itemInputMatches(entityItemsByInput))
			return false;

		boolean isReplacementPossible = itemInputs.size() == itemOutputs.size() && fluidOutput == null;

		if (isReplacementPossible)
		{
			int i = 0;
			for (Entry<InputItemStack, List<EntityItem>> entry : entityItemsByInput.entrySet())
			{
				ItemStack output = itemOutputs.get(i);
				for (EntityItem inputEntity : entry.getValue())
				{
					ItemStack inputStack = inputEntity.getEntityItem();
					ItemStack newItemStack = output.copy();
					newItemStack.stackSize = (int) (inputStack.stackSize * ((float) output.stackSize / entry.getKey().stackSize()));
					inputEntity.setEntityItemStack(newItemStack);
				}
				i++;
			}
		}
		else
		{
			Map<ItemStack, EntityItem> entityItemsByOutput = new HashMap<ItemStack, EntityItem>();
			for (ItemStack itemOutput : itemOutputs)
			{
				List<EntityItem> randomReferenceEntityList = entityItemsByInput.get(itemInputs.get(rand.nextInt(itemInputs.size())));
				EntityItem randomReferenceEntity = randomReferenceEntityList.get(rand.nextInt(randomReferenceEntityList.size()));
				EntityItem outputEntity = new EntityItem(world, randomReferenceEntity.posX, randomReferenceEntity.posY, randomReferenceEntity.posZ, itemOutput.copy());
				outputEntity.getEntityItem().stackSize = 0;
				entityItemsByOutput.put(itemOutput, outputEntity);
			}

			do
			{
				if (fluidInput != null && displacedFluid != null)
				{
					displacedFluid.amount -= fluidInput.amount;
					if (displacedFluid.amount <= 0)
					{
						PistonCraftingHandler.displacedLiquids.remove(fluidPos);
						displacedFluid = null;
					}
				}
				if (fluidOutput != null && fluidHandler != null)
				{
					fluidHandler.fill(ForgeDirection.UP, fluidOutput, true);
				}
				for (Entry<InputItemStack, List<EntityItem>> inputEntry : entityItemsByInput.entrySet())
				{
					int numRequired = inputEntry.getKey().stackSize();
					int numConsumed = 0;
					for (EntityItem inputEntity : inputEntry.getValue())
					{
						ItemStack inputStack = inputEntity.getEntityItem();
						int numToConsume = Math.min(inputStack.stackSize, numRequired - numConsumed);
						inputStack.stackSize -= numToConsume;
						numConsumed += numToConsume;

						if (numConsumed >= numRequired)
							break;
					}
				}
				for (Entry<ItemStack, EntityItem> entry : entityItemsByOutput.entrySet())
				{
					entry.getValue().getEntityItem().stackSize += entry.getKey().stackSize;
				}
			}
			while (fluidInputMatches(displacedFluid) && itemInputMatches(entityItemsByInput) && canOutputFluid(fluidHandler));

			for (Entry<ItemStack, EntityItem> entry : entityItemsByOutput.entrySet())
			{
				world.spawnEntityInWorld(entry.getValue());
			}
		}

		return true;
	}

	public boolean canOutputFluid(World world, int x, int y, int z)
	{
		if (fluidOutput == null)
			return true;

		return canOutputFluid(getOutputFluidHandler(world, x, y, z));
	}

	public boolean canOutputFluid(IFluidHandler fluidHandler)
	{
		if (fluidOutput == null)
			return true;

		if (fluidHandler == null)
			return false;

		return fluidHandler.fill(ForgeDirection.UP, fluidOutput, false) == fluidOutput.amount;
	}

	public IFluidHandler getOutputFluidHandler(World world, int x, int y, int z)
	{
		if (fluidOutput == null)
			return null;

		TileEntity tileUnderneath = world.getTileEntity(x, y - 1, z);

		if (!(tileUnderneath instanceof IFluidHandler))
			return null;

		return (IFluidHandler) tileUnderneath;
	}

	public boolean itemInputMatches(World world, int x, int y, int z)
	{
		if (itemInputs.isEmpty())
			return true;

		return itemInputMatches(WorldHelper.getItemEntitiesWithin(world, x, y, z));
	}

	public boolean itemInputMatches(List<EntityItem> entityItems)
	{
		if (itemInputs.isEmpty())
			return true;

		return itemInputMatches(getEntityItemsByInput(itemInputs, entityItems));
	}

	public boolean itemInputMatches(Map<InputItemStack, List<EntityItem>> entityItemsByInput)
	{
		if (itemInputs.isEmpty())
			return true;

		for (Entry<InputItemStack, List<EntityItem>> entityItemsByInputEntry : entityItemsByInput.entrySet())
		{
			if (getStackSizeOfEntityItems(entityItemsByInputEntry.getValue()) < entityItemsByInputEntry.getKey().stackSize())
				return false;
		}
		return true;
	}

	public static Map<InputItemStack, List<EntityItem>> getEntityItemsByInput(Collection<InputItemStack> targets, Collection<EntityItem> entityItems)
	{
		Map<InputItemStack, List<EntityItem>> entityItemsByItemStack = new HashMap<InputItemStack, List<EntityItem>>();
		for (InputItemStack target : targets)
		{
			entityItemsByItemStack.put(target, getMatchingEntityItems(target, entityItems));
		}
		return entityItemsByItemStack;
	}

	public static List<EntityItem> getMatchingEntityItems(InputItemStack target, Collection<EntityItem> entityItems)
	{
		List<EntityItem> matchingEntities = new ArrayList<EntityItem>();
		for (EntityItem entityItem : entityItems)
		{
			if (target.matches(entityItem.getEntityItem()))
			{
				matchingEntities.add(entityItem);
			}
		}
		if (!matchingEntities.isEmpty() && target.isOreDict() && target.stackSize() > 1)
		{
			List<EntityItem> entitiesOfOneTypeWithLargestStackSize = null;
			int largestStackSize = 0;
			for (EntityItem entityItem : matchingEntities)
			{
				if (entitiesOfOneTypeWithLargestStackSize != null && entitiesOfOneTypeWithLargestStackSize.get(0).getEntityItem().isItemEqual(entityItem.getEntityItem()))
					continue;

				List<EntityItem> exactMatches = getMatchingEntityItems(new InputItemStack(entityItem.getEntityItem()), matchingEntities);
				int exactMatchesStackSize = getStackSizeOfEntityItems(exactMatches);

				if (exactMatchesStackSize >= target.stackSize())
					return exactMatches;

				if (exactMatchesStackSize > largestStackSize)
				{
					entitiesOfOneTypeWithLargestStackSize = exactMatches;
					largestStackSize = exactMatchesStackSize;
				}
			}
			matchingEntities = entitiesOfOneTypeWithLargestStackSize;
		}
		return matchingEntities;
	}

	public static int getStackSizeOfEntityItems(Collection<EntityItem> entityItems)
	{
		int stackSize = 0;
		for (EntityItem entityItem : entityItems)
		{
			stackSize += entityItem.getEntityItem().stackSize;
		}
		return stackSize;
	}

	public boolean fluidInputMatches(World world, int x, int y, int z)
	{
		return fluidInputMatches(PistonCraftingHandler.displacedLiquids.get(new PistonCraftingHandler.WorldPosition(world, x, y, z)));
	}

	public boolean fluidInputMatches(FluidStack fluidStack)
	{
		if (fluidStack == null)
			return fluidInput == null;
		else
			return fluidStack.isFluidEqual(fluidInput) && fluidStack.amount >= fluidInput.amount;
	}
}
