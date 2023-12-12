package squeek.veganoption.content.modifiers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import squeek.veganoption.content.recipes.ShapelessDamageItemRecipe;
import squeek.veganoption.content.recipes.ShapelessDamageItemRecipeBuilder;

import java.util.*;
import java.util.function.Supplier;

/**
 * Use this modifier to override an item's craftRemainder for a given recipe output. For example, forcing the consumption of a bucket
 * rather than just the water inside the bucket, when crafting Item A.
 * <br/>
 * The 1.10 and earlier system included adding new craftRemainders with this modifier. That is now handled by {@link ShapelessDamageItemRecipe},
 * which serves the same purpose the old system served -- we only used this to damage soap and flint and steels when used in our recipes.
 * <br/>
 * Use the {@link ShapelessDamageItemRecipeBuilder} during datagen to create such recipes.
 * <br/>
 * TODO: We may consider in the future creating a new recipe type for craft remainder overrides rather than using this brute-force method.
 * For now, this works fine, albeit a bit needlessly hacky given the new APIs.
 */
public class CraftingModifier
{
	public HashMap<Item, Supplier<Ingredient[]>> craftRemainderOverrides = new HashMap<>();

	public CraftingModifier()
	{
		NeoForge.EVENT_BUS.register(this);
	}

	public void addInputsToRemoveForOutput(Item output, Supplier<Ingredient[]> inputs)
	{
		craftRemainderOverrides.put(output, inputs);
	}

	@SubscribeEvent
	public void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
	{
		List<Ingredient> inputsToRemove = getInputsToRemoveForOutput(event.getCrafting().getItem());

		if (inputsToRemove.isEmpty())
			return;

		for (int i = 0; i < event.getInventory().getContainerSize(); i++)
		{
			ItemStack stackInSlot = event.getInventory().getItem(i);
			if (!stackInSlot.isEmpty())
			{
				for (Ingredient inputToRemove : inputsToRemove)
				{
					if (inputToRemove.test(stackInSlot))
					{
						stackInSlot.shrink(1);
						break;
					}
				}
			}
		}
	}

	public List<Ingredient> getInputsToRemoveForOutput(Item output)
	{
		List<Ingredient> inputsToRemove = new ArrayList<>();
		for (Map.Entry<Item, Supplier<Ingredient[]>> entry : craftRemainderOverrides.entrySet())
		{
			if (entry.getKey() == output)
			{
				inputsToRemove.addAll(Arrays.asList(entry.getValue().get()));
			}
		}
		return inputsToRemove;
	}
}
