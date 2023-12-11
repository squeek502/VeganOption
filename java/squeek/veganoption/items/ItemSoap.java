package squeek.veganoption.items;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import java.util.Collection;
import java.util.List;

public class ItemSoap extends Item
{
	public static ItemStack milkBucket = new ItemStack(Items.MILK_BUCKET);

	public ItemSoap()
	{
		super(new Item.Properties()
			.stacksTo(1)
			.durability(4)
			.setNoRepair());
		DispenserBlock.registerBehavior(this, new ItemSoap.DispenserBehavior());
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemStack)
	{
		return UseAnim.EAT;
	}

	@Override
	public int getUseDuration(ItemStack itemStack)
	{
		return 32;
	}

	/**
	 * A way to cure only the potion effects that another item is a curative item of
	 */
	public static void curePotionEffectsAsItem(LivingEntity entity, ItemStack curativeItemToMimic)
	{
		entity.curePotionEffects(curativeItemToMimic);
	}

	/**
	 * A way to cure potion effects without clearing effects that are meant
	 * to be uncurable (e.g. Thaumcraft warp)
	 */
	public static void cureAllCurablePotionEffects(Player player)
	{
		Collection<MobEffectInstance> activePotionEffects = player.getActiveEffects();
		for (MobEffectInstance potionEffect : activePotionEffects)
		{
			if (potionEffect.getCurativeItems().isEmpty())
				continue;

			player.removeEffect(potionEffect.getEffect());
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
	{
		if (!level.isClientSide())
		{
			curePotionEffectsAsItem(entity, milkBucket);
			stack.hurtAndBreak(1, entity, (entityIn) -> { /* do nothing */});
		}
		return super.finishUsingItem(stack, level, entity);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemUtils.startUsingInstantly(level, player, hand);
		return super.use(level, player, hand);
	}

	public static class DispenserBehavior extends DefaultDispenseItemBehavior
	{
		@Override
		protected ItemStack execute(BlockSource blockSource, ItemStack itemStack)
		{
			List<LivingEntity> entitiesInFront = blockSource.level().getEntitiesOfClass(LivingEntity.class, new AABB(blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING))));

			LivingEntity mostDirtyEntity = null;
			for (LivingEntity entityInFront : entitiesInFront)
			{
				if (mostDirtyEntity == null || entityInFront.getActiveEffects().size() > mostDirtyEntity.getActiveEffects().size())
					mostDirtyEntity = entityInFront;
			}
			if (mostDirtyEntity != null)
			{
				curePotionEffectsAsItem(mostDirtyEntity, milkBucket);

				if (itemStack.hurt(1, blockSource.level().getRandom(), null))
					itemStack.setCount(0);
				return itemStack;
			}
			return super.execute(blockSource, itemStack);
		}
	}
}
