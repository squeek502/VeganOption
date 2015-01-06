package squeek.veganoption.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import squeek.veganoption.helpers.RandomHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class CraftingModifier
{
	public HashMap<ItemStack, ItemStack[]> inputsToRemoveForOutput = new HashMap<ItemStack, ItemStack[]>();
	public HashMap<ItemStack, ItemStack[]> inputsToKeepForOutput = new HashMap<ItemStack, ItemStack[]>();

	public CraftingModifier()
	{
		FMLCommonHandler.instance().bus().register(this);
	}

	public void addInputsToRemoveForOutput(ItemStack output, ItemStack... inputs)
	{
		inputsToRemoveForOutput.put(output, inputs);
	}

	public void addInputsToKeepForOutput(ItemStack output, ItemStack... inputs)
	{
		inputsToKeepForOutput.put(output, inputs);
	}

	@SubscribeEvent
	public void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
	{
		List<ItemStack> inputsToRemove = getInputsToRemoveForOutput(event.crafting);
		List<ItemStack> inputsToKeep = getInputsToKeepForOutput(event.crafting);

		if (inputsToRemove.isEmpty() && inputsToKeep.isEmpty())
			return;

		for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++)
		{
			ItemStack stackInSlot = event.craftMatrix.getStackInSlot(i);
			if (stackInSlot != null)
			{
				for (ItemStack inputToRemove : inputsToRemove)
				{
					if (OreDictionary.itemMatches(inputToRemove, stackInSlot, false))
					{
						stackInSlot.stackSize -= inputToRemove.stackSize;
						if (stackInSlot.stackSize <= 0)
							event.craftMatrix.setInventorySlotContents(i, null);
						break;
					}
				}
				for (ItemStack inputToKeep : inputsToKeep)
				{
					if (OreDictionary.itemMatches(inputToKeep, stackInSlot, false))
					{
						stackInSlot.stackSize += inputToKeep.stackSize;
						if (stackInSlot.isItemStackDamageable() && stackInSlot.attemptDamageItem(inputToKeep.stackSize, RandomHelper.random))
						{
							stackInSlot.stackSize--;
						}
						break;
					}
				}
			}
		}
	}

	public List<ItemStack> getInputsToRemoveForOutput(ItemStack output)
	{
		List<ItemStack> inputsToRemove = new ArrayList<ItemStack>();
		for (Entry<ItemStack, ItemStack[]> entry : inputsToRemoveForOutput.entrySet())
		{
			if (OreDictionary.itemMatches(entry.getKey(), output, false))
			{
				inputsToRemove.addAll(Arrays.asList(entry.getValue()));
			}
		}
		return inputsToRemove;
	}

	public List<ItemStack> getInputsToKeepForOutput(ItemStack output)
	{
		List<ItemStack> inputsToKeep = new ArrayList<ItemStack>();
		for (Entry<ItemStack, ItemStack[]> entry : inputsToKeepForOutput.entrySet())
		{
			if (OreDictionary.itemMatches(entry.getKey(), output, false))
			{
				inputsToKeep.addAll(Arrays.asList(entry.getValue()));
			}
		}
		return inputsToKeep;
	}
}
