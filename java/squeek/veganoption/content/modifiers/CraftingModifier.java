package squeek.veganoption.content.modifiers;

import com.google.common.collect.Iterables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.tags.ITag;

import java.util.*;

public class CraftingModifier
{
	public HashMap<Item, Item[]> inputsToRemoveForOutput = new HashMap<>();
	public HashMap<Item, Item[]> inputsToKeepForOutput = new HashMap<>();

	public CraftingModifier()
	{
		NeoForge.EVENT_BUS.register(this);
	}

	public void addInputsToRemoveForOutput(Item output, Item... inputs)
	{
		inputsToRemoveForOutput.put(output, inputs);
	}

	public void addInputsToRemoveForOutput(Item output, ITag<Item> inputs)
	{
		addInputsToRemoveForOutput(output, Iterables.toArray(inputs, Item.class));
	}

	public void addInputsToRemoveForOutput(Item output, TagKey<Item>... inputs)
	{
		for (TagKey<Item> tag : inputs)
		{
			addInputsToRemoveForOutput(output, ForgeRegistries.ITEMS.tags().getTag(tag));
		}
	}

	public void addInputsToKeepForOutput(Item output, Item... inputs)
	{
		inputsToKeepForOutput.put(output, inputs);
	}

	public void addInputsToKeepForOutput(Item output, ITag<Item> inputs)
	{
		addInputsToKeepForOutput(output, Iterables.toArray(inputs, Item.class));
	}

	public void addInputsToKeepForOutput(Item output, TagKey<Item>... inputs)
	{
		for (TagKey<Item> tag : inputs)
		{
			addInputsToKeepForOutput(output, ForgeRegistries.ITEMS.tags().getTag(tag));
		}
	}

	@SubscribeEvent
	public void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
	{
		List<Item> inputsToRemove = getInputsToRemoveForOutput(event.getCrafting().getItem());
		List<Item> inputsToKeep = getInputsToKeepForOutput(event.getCrafting().getItem());

		if (inputsToRemove.isEmpty() && inputsToKeep.isEmpty())
			return;

		for (int i = 0; i < event.getInventory().getContainerSize(); i++)
		{
			ItemStack stackInSlot = event.getInventory().getItem(i);
			if (!stackInSlot.isEmpty())
			{
				for (Item inputToRemove : inputsToRemove)
				{
					if (inputToRemove == stackInSlot.getItem())
					{
						stackInSlot.shrink(1);
						if (stackInSlot.getCount() <= 0)
							event.getInventory().setItem(i, ItemStack.EMPTY);
						break;
					}
				}
				for (Item inputToKeep : inputsToKeep)
				{
					if (inputToKeep == stackInSlot.getItem())
					{
						stackInSlot.grow(stackInSlot.getCount());
						Player player = event.getEntity();
						ServerPlayer serverPlayer = player instanceof ServerPlayer ? (ServerPlayer) player : null;
 						if (stackInSlot.isDamageableItem() && stackInSlot.hurt(1, event.getEntity().getRandom(), serverPlayer))
						{
							stackInSlot.shrink(1);
						}
						break;
					}
				}
			}
		}
	}

	public List<Item> getInputsToRemoveForOutput(Item output)
	{
		List<Item> inputsToRemove = new ArrayList<>();
		for (Map.Entry<Item, Item[]> entry : inputsToRemoveForOutput.entrySet())
		{
			if (entry.getKey() == output)
			{
				inputsToRemove.addAll(Arrays.asList(entry.getValue()));
			}
		}
		return inputsToRemove;
	}

	public List<Item> getInputsToKeepForOutput(Item output)
	{
		List<Item> inputsToKeep = new ArrayList<>();
		for (Map.Entry<Item, Item[]> entry : inputsToKeepForOutput.entrySet())
		{
			if (entry.getKey() == output)
			{
				inputsToKeep.addAll(Arrays.asList(entry.getValue()));
			}
		}
		return inputsToKeep;
	}
}
