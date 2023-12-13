package squeek.veganoption.items;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import squeek.veganoption.entities.EntityBubble;

public class ItemSoapSolution extends Item
{
	public static final float BUBBLE_INITIAL_VELOCITY = 0.3f;

	public ItemSoapSolution()
	{
		super(new Item.Properties().durability(15).setNoRepair().craftRemainder(Items.GLASS_BOTTLE));
		DispenserBlock.registerBehavior(this, new ItemSoapSolution.DispenserBehavior());
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack)
	{
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack stack)
	{
		return 16;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity)
	{
		if (!level.isClientSide() && entity instanceof Player player)
		{
			EntityBubble bubble = new EntityBubble(level, player);
			bubble.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, BUBBLE_INITIAL_VELOCITY, 1.0F);
			level.addFreshEntity(bubble);
		}

		itemStack.hurtAndBreak(1, entity, (e) -> { /* do nothing */});

		if (itemStack.getCount() == 0 && hasCraftingRemainingItem(itemStack))
		{
			return getCraftingRemainingItem(itemStack);
		}

		return super.finishUsingItem(itemStack, level, entity);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(level, player, hand);
	}

	public static class DispenserBehavior extends AbstractProjectileDispenseBehavior
	{
		@Override
		protected Projectile getProjectile(Level level, Position pos, ItemStack stack)
		{
			return new EntityBubble(level, pos.x(), pos.y(), pos.z());
		}

		@Override
		public ItemStack execute(BlockSource blockSource, ItemStack itemStack)
		{
			// counteract the shrink in super.execute
			itemStack.grow(1);
			super.execute(blockSource, itemStack);

			itemStack.hurt(1, blockSource.level().getRandom(), null);
			if (itemStack.getDamageValue() >= itemStack.getMaxDamage())
			{
				itemStack.setCount(0);
			}
			if (itemStack.getCount() == 0 && !itemStack.getCraftingRemainingItem().isEmpty())
			{
				return itemStack.getCraftingRemainingItem();
			}
			return itemStack;
		}

		@Override
		protected float getPower()
		{
			return 0.5f;
		}
	}
}
