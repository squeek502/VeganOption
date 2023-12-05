package squeek.veganoption.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import squeek.veganoption.content.modules.FrozenBubble;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.EffectsHelper;
import squeek.veganoption.helpers.RandomHelper;
import squeek.veganoption.helpers.TemperatureHelper;

public class EntityBubble extends ThrowableItemProjectile
{
	private static final byte EVENT_ID = 7;
	public static final int LIFETIME_BASE = 40;
	public static final int LIFETIME_MAX = 80;
	public static final float FREEZING_TEMPERATURE = -15;
	public static final int TEMPERATURE_CHECK_RADIUS = 3;

	public int lifetime = RandomHelper.getRandomIntFromRange(LIFETIME_BASE, LIFETIME_MAX);
	public float temperature;

	public EntityBubble(Level level, Player player)
	{
		super(FrozenBubble.bubbleEntityType.get(), player, level);
	}

	public EntityBubble(Level level, double x, double y, double z)
	{
		super(FrozenBubble.bubbleEntityType.get(), x, y, z, level);
	}

	public EntityBubble(EntityType<? extends EntityBubble> type, Level level)
	{
		super(type, level);
	}

	private void setTemperature()
	{
		this.temperature = TemperatureHelper.getBiomeTemperature(level(), blockPosition());
	}

	@Override
	public boolean hurt(DamageSource source, float amount)
	{
		pop();
		return super.hurt(source, amount);
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	public void tick()
	{
		if (firstTick)
			setTemperature();

		if (!level().isClientSide() && (tickCount >= lifetime || this.isInWater()))
		{
			pop();
			return;
		}

		float surroundingTemp = getSurroundingAirTemperature(level(), blockPosition());
		temperature += (surroundingTemp - temperature) * 0.05f;

		if (!level().isClientSide() && temperature <= FREEZING_TEMPERATURE)
		{
			freeze();
			return;
		}

		if (!level().isClientSide() && tickCount % 10 == 0)
		{
			Vec3 motion = getDeltaMovement();
			double motionX = motion.x + 0.1d * (RandomHelper.random.nextDouble() - 0.5d);
			double motionY = motion.y + 0.1d * (RandomHelper.random.nextDouble() - 0.5d);
			double motionZ = motion.z + 0.1d * (RandomHelper.random.nextDouble() - 0.5d);
			setDeltaMovement(motionX, motionY, motionZ);
		}

		super.tick();

		Vec3 motion = getDeltaMovement();
		// this check helps avoid low velocities which minecraft seems to struggle with? (visually stutters)
		// have not looked into the cause; this is basically an ugly/fast workaround
		// todo: check if still a problem
		if (motion.x * motion.x + motion.y * motion.y + motion.z * motion.z > 0.025f)
			setDeltaMovement(motion.x * 0.9d, motion.y * 0.9d, motion.z * 0.9);
	}

	@Override
	protected float getGravity()
	{
		return 0.0F;
	}

	public void pop()
	{
		if (!level().isClientSide())
		{
			level().broadcastEntityEvent(this, EVENT_ID);
			discard();
		}
	}

	public void freeze()
	{
		if (!level().isClientSide())
		{
			ItemEntity frozenBubble = new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(FrozenBubble.frozenBubble.get()));
			level().addFreshEntity(frozenBubble);
			discard();
		}
	}

	@Override
	protected void onHit(HitResult result)
	{
		super.onHit(result);
		pop();
	}

	@Override
	public void handleEntityEvent(byte id)
	{
		super.handleEntityEvent(id);
		if (id == EVENT_ID)
		{
			EffectsHelper.doEntityBreakParticles(level(), getX(), getY(), getZ(), getDefaultItem());
		}
	}

	public static float getSurroundingAirTemperature(Level level, BlockPos pos)
	{
		float airTemperature = TemperatureHelper.getBiomeTemperature(level, pos);

		BlockPos[] surroundingBlocks = BlockHelper.getBlocksInRadiusAround(pos, TEMPERATURE_CHECK_RADIUS);

		for (BlockPos blockPos : surroundingBlocks)
		{
			Block block = level.getBlockState(blockPos).getBlock();
			if (block == Blocks.AIR)
				airTemperature -= 0.01f;
			else if (block == Blocks.SNOW)
				airTemperature -= 0.1f;
			else if (block == Blocks.ICE)
				airTemperature -= 0.5f;
			else if (block == Blocks.PACKED_ICE)
				airTemperature -= 1.0f;
			else if (block == Blocks.LAVA)
				airTemperature += 3.0f;
			else if (block == Blocks.TORCH)
				airTemperature += 0.5f;
		}

		return airTemperature;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		super.deserializeNBT(nbt);
		temperature = nbt.getFloat("temperature");
		lifetime = nbt.getInt("lifetime");
	}

	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag nbt = super.serializeNBT();
		nbt.putFloat("temperature", temperature);
		nbt.putInt("lifetime", lifetime);
		return nbt;
	}

	@Override
	protected Item getDefaultItem()
	{
		return FrozenBubble.soapSolution.get();
	}
}
