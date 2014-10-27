package squeek.veganoption.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.BlockHelper.BlockPos;
import squeek.veganoption.helpers.RandomHelper;
import squeek.veganoption.network.MessageBubblePop;
import squeek.veganoption.network.NetworkHandler;
import squeek.veganoption.registry.Content;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityBubble extends EntityThrowable
{
	public static final int LIFETIME_BASE = 40;
	public static final int LIFETIME_MAX = 80;
	public static final float FREEZING_TEMPERATURE = -15;
	public static final int TEMPERATURE_CHECK_RADIUS = 3;

	public int lifetime = RandomHelper.getRandomIntFromRange(LIFETIME_BASE, LIFETIME_MAX);
	public float temperature;

	public EntityBubble(World world)
	{
		super(world);
	}

	public EntityBubble(World world, EntityLivingBase entity)
	{
		super(world, entity);
	}

	public EntityBubble(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.temperature = getBiomeTemperature(worldObj, (int) posX, (int) posY, (int) posZ);
	}

	@Override
	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
	{
		pop();
		return super.attackEntityFrom(p_70097_1_, p_70097_2_);
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	public void onUpdate()
	{
		if (!this.worldObj.isRemote && (ticksExisted >= lifetime || this.isInWater()))
		{
			pop();
			return;
		}

		float surroundingTemp = getSurroundingAirTemperature(worldObj, posX, posY, posZ);
		temperature += (surroundingTemp - temperature) * 0.05f;

		if (!this.worldObj.isRemote && temperature <= FREEZING_TEMPERATURE)
		{
			freeze();
			return;
		}

		if (!this.worldObj.isRemote && ticksExisted % 10 == 0)
		{
			this.motionX += 0.1F * (RandomHelper.random.nextFloat() - 0.5F);
			this.motionY += 0.1F * (RandomHelper.random.nextFloat() - 0.5F);
			this.motionZ += 0.1F * (RandomHelper.random.nextFloat() - 0.5F);
		}

		super.onUpdate();

		this.motionX *= 0.9F;
		this.motionY *= 0.9F;
		this.motionZ *= 0.9F;
	}

	// initial speed
	@Override
	protected float func_70182_d()
	{
		return 0.3f;
	}

	@Override
	protected float getGravityVelocity()
	{
		return 0.0001F;
	}

	public void pop()
	{
		if (!this.worldObj.isRemote)
		{
			TargetPoint target = new TargetPoint(dimension, posX, posY, posZ, 80);
			NetworkHandler.channel.sendToAllAround(new MessageBubblePop(posX, posY, posZ), target);
			this.setDead();
		}
	}

	public void freeze()
	{
		if (!this.worldObj.isRemote)
		{
			EntityItem frozenBubble = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Content.frozenBubble));
			worldObj.spawnEntityInWorld(frozenBubble);
			this.setDead();
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition movingObjectPosition)
	{
		pop();
	}

	public static float getBiomeTemperature(World world, int x, int y, int z)
	{
		final float FLOAT_TEMP_TO_CELSIUS = 20f;
		return world.getBiomeGenForCoords(x, y).getFloatTemperature(x, y, z) * FLOAT_TEMP_TO_CELSIUS;
	}

	public static float getSurroundingAirTemperature(World world, double x, double y, double z)
	{
		float airTemperature = getBiomeTemperature(world, (int) x, (int) y, (int) z);

		BlockPos[] surroundingBlocks = BlockHelper.getBlocksInRadiusAround(new BlockPos(world, (int) x, (int) y, (int) z), TEMPERATURE_CHECK_RADIUS);

		for (BlockPos blockPos : surroundingBlocks)
		{
			Block block = blockPos.getBlock();
			if (block == Blocks.air)
				airTemperature -= 0.01f;
			else if (block == Blocks.snow)
				airTemperature -= 0.1f;
			else if (block == Blocks.ice)
				airTemperature -= 0.5f;
			else if (block == Blocks.packed_ice)
				airTemperature -= 1.0f;
			else if (block == Blocks.lava)
				airTemperature += 3.0f;
			else if (block == Blocks.torch)
				airTemperature += 0.5f;
		}

		return airTemperature;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
	{
		super.readEntityFromNBT(tag);

		temperature = tag.getFloat("temperature");
		lifetime = tag.getInteger("lifetime");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
	{
		super.writeEntityToNBT(tag);

		tag.setFloat("temperature", temperature);
		tag.setInteger("lifetime", lifetime);
	}
}
