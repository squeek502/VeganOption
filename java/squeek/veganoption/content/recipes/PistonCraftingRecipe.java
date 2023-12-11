package squeek.veganoption.content.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import squeek.veganoption.content.crafting.PistonCraftingHandler;
import squeek.veganoption.helpers.FluidHelper;
import squeek.veganoption.helpers.WorldHelper;

import java.util.*;
import java.util.Map.Entry;

// todo: json
public class PistonCraftingRecipe
{
	public List<InputItemStack> itemInputs = new ArrayList<>();
	public FluidStack fluidInput = FluidStack.EMPTY;
	public List<ItemStack> itemOutputs = new ArrayList<>();
	public FluidStack fluidOutput = FluidStack.EMPTY;
	protected Random rand = new Random();

	public PistonCraftingRecipe(Object output, Object... inputs)
	{
		this(new Object[]{output}, inputs);
	}

	/**
	 * @param outputs Outputs must be either of type ItemStack or FluidStack
	 * @param inputs Inputs must be either of type InputItemStack or FluidStack
	 */
	public PistonCraftingRecipe(Object[] outputs, Object[] inputs)
	{
		for (Object input : inputs)
		{
			if (input instanceof InputItemStack)
				itemInputs.add((InputItemStack) input);
			else if (input instanceof FluidStack fluidStack)
				fluidInput = fluidStack;
			else
				throw new RuntimeException("Unsupported PistonCraftingRecipe input: " + input);
		}
		for (Object output : outputs)
		{
			if (output instanceof ItemStack stack)
				itemOutputs.add(stack);
			else if (output instanceof FluidStack fluidStack)
				fluidOutput = fluidStack;
			else
				throw new RuntimeException("Unsupported PistonCraftingRecipe output: " + output);
		}
	}

	public boolean tryCraft(Level level, BlockPos pos)
	{
		IFluidHandler fluidHandler = getOutputFluidHandler(level, pos);

		if (!canOutputFluid(fluidHandler))
			return false;

		PistonCraftingHandler.WorldPosition displacedPos = new PistonCraftingHandler.WorldPosition(level, pos);
		FluidStack displacedFluid = PistonCraftingHandler.displacedLiquids.get(displacedPos);

		if (!fluidInputMatches(displacedFluid))
			return false;

		List<ItemEntity> itemEntitiesWithin = WorldHelper.getItemEntitiesWithin(displacedPos.level, displacedPos.pos);
		Map<InputItemStack, List<ItemEntity>> itemEntitiesByInput = getItemEntitiesByInput(itemInputs, itemEntitiesWithin);

		if (!itemInputMatches(itemEntitiesByInput))
			return false;

		boolean isReplacementPossible = itemInputs.size() == itemOutputs.size() && fluidOutput.isEmpty();

		if (isReplacementPossible)
		{
			int i = 0;
			for (Entry<InputItemStack, List<ItemEntity>> entry : itemEntitiesByInput.entrySet())
			{
				ItemStack output = itemOutputs.get(i);
				for (ItemEntity inputEntity : entry.getValue())
				{
					ItemStack inputStack = inputEntity.getItem();
					ItemStack newItemStack = output.copy();
					newItemStack.setCount((int) (inputStack.getCount() * ((float) output.getCount() / entry.getKey().getCount())));
					inputEntity.setItem(newItemStack);
				}
				i++;
			}
		}
		else
		{
			Map<ItemStack, ItemEntity> itemEntitiesByOutput = new HashMap<>();
			for (ItemStack itemOutput : itemOutputs)
			{
				List<ItemEntity> randomReferenceEntityList = itemEntitiesByInput.get(itemInputs.get(rand.nextInt(itemInputs.size())));
				ItemEntity randomReferenceEntity = randomReferenceEntityList.get(rand.nextInt(randomReferenceEntityList.size()));
				ItemEntity outputEntity = new ItemEntity(level, randomReferenceEntity.getX(), randomReferenceEntity.getY(), randomReferenceEntity.getZ(), itemOutput.copy());
				outputEntity.getItem().setCount(0);
				itemEntitiesByOutput.put(itemOutput, outputEntity);
			}

			do
			{
				if (!fluidInput.isEmpty() && displacedFluid != null)
				{
					displacedFluid.setAmount(displacedFluid.getAmount() - fluidInput.getAmount());
					if (displacedFluid.getAmount() <= 0)
					{
						PistonCraftingHandler.displacedLiquids.remove(displacedPos);
						displacedFluid = null;
					}
				}
				if (!fluidOutput.isEmpty() && fluidHandler != null)
				{
					fluidHandler.fill(fluidOutput, IFluidHandler.FluidAction.EXECUTE);
				}
				for (Entry<InputItemStack, List<ItemEntity>> inputEntry : itemEntitiesByInput.entrySet())
				{
					int numRequired = inputEntry.getKey().getCount();
					int numConsumed = 0;
					for (ItemEntity inputEntity : inputEntry.getValue())
					{
						ItemStack inputStack = inputEntity.getItem();
						int numToConsume = Math.min(inputStack.getCount(), numRequired - numConsumed);
						inputStack.shrink(numToConsume);
						numConsumed += numToConsume;

						if (numConsumed >= numRequired)
							break;
					}
				}
				for (Entry<ItemStack, ItemEntity> entry : itemEntitiesByOutput.entrySet())
				{
					entry.getValue().getItem().grow(entry.getKey().getCount());
				}
			}
			while (fluidInputMatches(displacedFluid) && itemInputMatches(itemEntitiesByInput) && canOutputFluid(fluidHandler));

			for (Entry<ItemStack, ItemEntity> entry : itemEntitiesByOutput.entrySet())
			{
				level.addFreshEntity(entry.getValue());
			}
		}

		return true;
	}

	public boolean canOutputFluid(Level level, BlockPos pos)
	{
		if (fluidOutput.isEmpty())
			return true;

		return canOutputFluid(getOutputFluidHandler(level, pos));
	}

	public boolean canOutputFluid(IFluidHandler fluidHandler)
	{
		if (fluidOutput.isEmpty())
			return true;

		if (fluidHandler == null)
			return false;

		return fluidHandler.fill(fluidOutput, IFluidHandler.FluidAction.SIMULATE) == fluidOutput.getAmount();
	}

	public IFluidHandler getOutputFluidHandler(Level world, BlockPos pos)
	{
		if (fluidOutput.isEmpty())
			return null;

		return FluidHelper.getFluidHandlerAt(world, pos.below(), Direction.UP);
	}

	public boolean itemInputMatches(Level level, BlockPos pos)
	{
		if (itemInputs.isEmpty())
			return true;

		return itemInputMatches(WorldHelper.getItemEntitiesWithin(level, pos));
	}

	public boolean itemInputMatches(List<ItemEntity> itemEntities)
	{
		if (itemInputs.isEmpty())
			return true;

		return itemInputMatches(getItemEntitiesByInput(itemInputs, itemEntities));
	}

	public boolean itemInputMatches(Map<InputItemStack, List<ItemEntity>> itemEntitiesByInput)
	{
		if (itemInputs.isEmpty())
			return true;

		for (Entry<InputItemStack, List<ItemEntity>> itemEntitiesByInputEntry : itemEntitiesByInput.entrySet())
		{
			if (getStackSizeOfItemEntities(itemEntitiesByInputEntry.getValue()) < itemEntitiesByInputEntry.getKey().getCount())
				return false;
		}
		return true;
	}

	public static Map<InputItemStack, List<ItemEntity>> getItemEntitiesByInput(Collection<InputItemStack> targets, Collection<ItemEntity> itemEntities)
	{
		Map<InputItemStack, List<ItemEntity>> itemEntitiesByItemStack = new HashMap<>();
		for (InputItemStack target : targets)
		{
			itemEntitiesByItemStack.put(target, getMatchingItemEntities(target, itemEntities));
		}
		return itemEntitiesByItemStack;
	}

	public static List<ItemEntity> getMatchingItemEntities(InputItemStack target, Collection<ItemEntity> itemEntities)
	{
		List<ItemEntity> matchingEntities = new ArrayList<>();
		for (ItemEntity itemEntity : itemEntities)
		{
			if (target.matches(itemEntity.getItem()))
			{
				matchingEntities.add(itemEntity);
			}
		}
		return matchingEntities;
	}

	public static int getStackSizeOfItemEntities(Collection<ItemEntity> itemEntities)
	{
		int stackSize = 0;
		for (ItemEntity itemEntity : itemEntities)
		{
			stackSize += itemEntity.getItem().getCount();
		}
		return stackSize;
	}

	public boolean fluidInputMatches(Level world, BlockPos pos)
	{
		return fluidInputMatches(PistonCraftingHandler.displacedLiquids.get(new PistonCraftingHandler.WorldPosition(world, pos)));
	}

	public boolean fluidInputMatches(FluidStack fluidStack)
	{
		if (fluidStack == null)
			return fluidInput.isEmpty();
		else
			return fluidStack.isFluidEqual(fluidInput) && fluidStack.getAmount() >= fluidInput.getAmount();
	}
}
