package squeek.veganoption.content.modifiers;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import squeek.veganoption.entities.EntityPlasticEgg;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class EggModifier
{
	/**
	 * A generic EggModifier for when the ItemStack being searched is null. It does is ensured to do nothing.
	 */
	private static final EggModifier DO_NOTHING_MODIFIER = new EggModifier();
	private static final Map<ItemStackMatcher, EggModifier> modifiers = new HashMap<ItemStackMatcher, EggModifier>();

	public void addItem(ItemStack stack, EggModifier modifier)
	{
		addItem(new ItemStackMatcher(stack), modifier);
	}

	public void addItem(ItemStackMatcher matcher, EggModifier modifier)
	{
		modifiers.put(matcher, modifier);
	}

	/**
	 * Finds an EggModifier that matches the provided ItemStack (see {@link ItemStackMatcher}). If it does not find a match, it will
	 * instantiate a new {@link EggModifierDropItem}, add it to the modifier registry, and return it. Never null.
	 */
	@Nonnull
	public EggModifier findModifierForItemStack(ItemStack value)
	{
		if (value == null) return DO_NOTHING_MODIFIER;

		for (Map.Entry<ItemStackMatcher, EggModifier> entry : modifiers.entrySet())
		{
			if (entry.getKey().matches(value)) return entry.getValue();
		}

		EggModifier modifier = new EggModifierDropItem(value);
		modifiers.put(new ItemStackMatcher(value), modifier);
		return modifier;
	}

	/**
	 * Called when the plastic egg collides with an entity.
	 * @param rayTraceResult See {@link EntityThrowable#onImpact(RayTraceResult)}
	 * @param eggEntity The egg that collided
	 */
	public void onEntityCollision(RayTraceResult rayTraceResult, EntityPlasticEgg eggEntity)
	{
	}

	/**
	 * Called before the egg is set to dead. See {@link #onEntityCollision(RayTraceResult, EntityPlasticEgg)} for parameters.
	 */
	public void onImpactGeneric(RayTraceResult rayTraceResult, EntityPlasticEgg eggEntity)
	{
	}

	public static class ItemStackMatcher
	{
		private final ItemStack toMatch;

		public ItemStackMatcher(ItemStack toMatch)
		{
			this.toMatch = toMatch;
		}

		public boolean matches(ItemStack value)
		{
			return ItemStack.areItemStacksEqual(toMatch, value);
		}
	}

	public static class EggModifierDropItem extends EggModifier
	{
		private final ItemStack toDrop;

		public EggModifierDropItem(ItemStack toDrop)
		{
			this.toDrop = toDrop;
		}

		@Override
		public void onImpactGeneric(RayTraceResult rayTraceResult, EntityPlasticEgg eggEntity)
		{
			EntityItem item = new EntityItem(eggEntity.worldObj, eggEntity.posX, eggEntity.posY, eggEntity.posZ, toDrop);
			eggEntity.worldObj.spawnEntityInWorld(item);
		}
	}
}
