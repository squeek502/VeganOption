package squeek.veganoption.items;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import squeek.veganoption.helpers.RandomHelper;

public abstract class ItemThrowableGeneric extends Item
{
	public static final float DEFAULT_THROWSPEED = 1.5F;

	public SoundEvent throwSound;
	public float throwSpeed;

	public ItemThrowableGeneric()
	{
		this(DEFAULT_THROWSPEED);
	}

	public ItemThrowableGeneric(float throwSpeed)
	{
		this(SoundEvents.ARROW_SHOOT, throwSpeed);
	}

	public ItemThrowableGeneric(SoundEvent throwSound)
	{
		this(throwSound, DEFAULT_THROWSPEED);
	}

	public ItemThrowableGeneric(SoundEvent throwSound, float throwSpeed)
	{
		super(new Item.Properties());
		this.throwSound = throwSound;
		this.throwSpeed = throwSpeed;

		DispenserBlock.registerBehavior(this, new ItemThrowableGeneric.DispenserBehavior(this));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack inHand = player.getItemInHand(hand);
		if (!player.isCreative())
		{
			inHand.shrink(1);
		}

		player.playSound(throwSound, 0.5F, 0.4F / (RandomHelper.random.nextFloat() * 0.4F + 0.8F));

		if (!level.isClientSide())
		{
			ThrowableItemProjectile entity = getNewProjectile(inHand, level, player);
			entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, throwSpeed, 1.0F);
			level.addFreshEntity(entity);
		}

		return InteractionResultHolder.success(inHand);
	}

	public abstract ThrowableItemProjectile getNewProjectile(ItemStack thrownItem, Level level, Player thrower);

	public abstract ThrowableItemProjectile getNewProjectile(ItemStack thrownItem, Level level, double x, double y, double z);

	public static class DispenserBehavior extends AbstractProjectileDispenseBehavior
	{
		public ItemThrowableGeneric itemThrowableGeneric;

		public DispenserBehavior(ItemThrowableGeneric itemThrowableGeneric)
		{
			super();
			this.itemThrowableGeneric = itemThrowableGeneric;
		}

		@Override
		protected Projectile getProjectile(Level level, Position pos, ItemStack stack)
		{
			return itemThrowableGeneric.getNewProjectile(stack, level, pos.x(), pos.y(), pos.z());
		}
	}
}
