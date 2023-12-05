package squeek.veganoption.content.modifiers;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import squeek.veganoption.entities.EntityPlasticEgg;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class EggModifier
{
	/**
	 * A generic EggModifier for when the Item being searched is null. It is ensured to do nothing.
	 */
	private static final EggModifier DO_NOTHING_MODIFIER = new EggModifier();
	private static final Map<Item, EggModifier> modifiers = new HashMap<>();

	public void addItem(Item item, EggModifier modifier)
	{
		modifiers.put(item, modifier);
	}

	/**
	 * Finds an EggModifier that matches the provided Item. If it does not find a match, it will
	 * instantiate a new {@link EggModifierDropItem}, add it to the modifier registry, and return it. Never null.
	 */
	@Nonnull
	public EggModifier findModifierForItem(Item value)
	{
		if (value == null) return DO_NOTHING_MODIFIER;

		for (Map.Entry<Item, EggModifier> entry : modifiers.entrySet())
		{
			if (entry.getKey() == value) return entry.getValue();
		}

		EggModifier modifier = new EggModifierDropItem(value);
		modifiers.put(value, modifier);
		return modifier;
	}

	/**
	 * Called when the plastic egg collides with something. This is called <b>before</b> the {@link #onHitEntity} and {@link #onHitBlock}
	 * methods are called.
	 *
	 * Default implementation must do nothing, or {@link EggModifier#DO_NOTHING_MODIFIER} must be changed.
	 */
	public void onHitGeneric(HitResult hitResult, EntityPlasticEgg eggEntity)
	{
	}

	/**
	 * Called when the plastic egg collides with an entity.
	 *
	 * Default implementation must do nothing, or {@link EggModifier#DO_NOTHING_MODIFIER} must be changed.
	 */
	public void onHitEntity(EntityHitResult hitResult, EntityPlasticEgg eggEntity)
	{
	}

	/**
	 * Called when the plastic egg collides with a block.
	 *
	 * Default implementation must do nothing, or {@link EggModifier#DO_NOTHING_MODIFIER} must be changed.
	 */
	public void onHitBlock(BlockHitResult hitResult, EntityPlasticEgg eggEntity)
	{
	}

	public static class EggModifierDropItem extends EggModifier
	{
		private final Item toDrop;

		public EggModifierDropItem(Item toDrop)
		{
			this.toDrop = toDrop;
		}

		@Override
		public void onHitGeneric(HitResult rayTraceResult, EntityPlasticEgg eggEntity)
		{
			ItemEntity item = new ItemEntity(eggEntity.level(), eggEntity.getBlockX(), eggEntity.getBlockY(), eggEntity.getBlockZ(), new ItemStack(toDrop));
			eggEntity.level().addFreshEntity(item);
		}
	}
}
