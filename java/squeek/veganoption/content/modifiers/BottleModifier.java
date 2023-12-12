package squeek.veganoption.content.modifiers;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A modifier used to handle glass bottles/bottled fluid interactions with the basin.
 * TODO: Determine a balanced way to handle in-world bottling of non-water fluids. Or figure out a way to handle this in NeoForge and submit a proposal.
 */
public class BottleModifier
{
	private final Map<TagKey<Fluid>, Supplier<ItemStack>> customBottlingHandlers = new HashMap<>();
	private final Map<Predicate<ItemStack>, Supplier<Fluid>> customUnbottlingHandlers = new HashMap<>();

	public BottleModifier()
	{
		registerCustomBottleHandler(
			FluidTags.WATER,
			() -> PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER),
			(stack) -> stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER,
			() -> Fluids.WATER);
	}

	/**
	 * Register a fluid as being bottleable in-world and by the basin.
	 * @param fluidTag The fluid type
	 * @param newBottleSupplier A supplier providing the new bottled version, i.e., water bottle, vegetable oil, etc.
	 * @param emptyBottlePredicate A predicate to determine if the given itemstack should empty into the given fluid.
	 * @param out The fluid result from emptying a stack for which emptyBottlePredicate is true.
	 */
	public void registerCustomBottleHandler(TagKey<Fluid> fluidTag, Supplier<ItemStack> newBottleSupplier, Predicate<ItemStack> emptyBottlePredicate, Supplier<Fluid> out)
	{
		customBottlingHandlers.put(fluidTag, newBottleSupplier);
		customUnbottlingHandlers.put(emptyBottlePredicate, out);
	}

	/**
	 * Returns the bottle for this fluid, or empty if no bottle is available.
	 */
	public ItemStack getNewBottleStack(Fluid fluid)
	{
		for (Map.Entry<TagKey<Fluid>, Supplier<ItemStack>> entry : customBottlingHandlers.entrySet())
		{
			if (fluid.is(entry.getKey()))
				return entry.getValue().get();
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Returns the Fluid to empty as, or empty if not applicable.
	 */
	public Fluid stackShouldEmptyAs(ItemStack stack)
	{
		for (Map.Entry<Predicate<ItemStack>, Supplier<Fluid>> entry : customUnbottlingHandlers.entrySet())
		{
			if (entry.getKey().test(stack))
				return entry.getValue().get();
		}
		return Fluids.EMPTY;
	}
}
