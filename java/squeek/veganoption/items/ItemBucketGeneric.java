package squeek.veganoption.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemBucketGeneric extends ItemBucket
{
	public Block filledWith;

	public ItemBucketGeneric(Block filledWith)
	{
		super(filledWith);
		this.filledWith = filledWith;
		MinecraftForge.EVENT_BUS.register(this);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new ItemBucketGeneric.DispenserBehavior());
	}

	@SubscribeEvent
	public void onFillBucket(FillBucketEvent event)
	{
		if (event.isCanceled() || event.getResult() != Event.Result.DEFAULT)
			return;

		if (event.getTarget() == null)
			return;

		IBlockState state = event.getWorld().getBlockState(event.getTarget().getBlockPos());
		Block block = state.getBlock();

		if (block == filledWith && block.getMetaFromState(state) == getPlacedLiquidMetadata())
		{
			event.getWorld().setBlockToAir(event.getTarget().getBlockPos());
			event.setFilledBucket(new ItemStack(this));
			event.setResult(Event.Result.ALLOW);
		}
	}

	public int getPlacedLiquidMetadata()
	{
		return filledWith instanceof BlockFluidFinite ? 7 : 0;
	}

	@Override
	public boolean tryPlaceContainedLiquid(EntityPlayer player, World world, BlockPos pos)
	{
		if (this.filledWith == Blocks.AIR)
		{
			return false;
		}
		else
		{
			IBlockState state = world.getBlockState(pos);
			Material material = state.getMaterial();
			boolean flag = !material.isSolid();

			if (!world.isAirBlock(pos) && !flag)
			{
				return false;
			}
			else
			{
				if (world.provider.doesWaterVaporize() && this.filledWith == Blocks.FLOWING_WATER)
				{
					world.playSound(player, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.PLAYERS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

					for (int l = 0; l < 8; ++l)
					{
						world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0D, 0D, 0D);
					}
				}
				else
				{
					if (!world.isRemote && flag && !material.isLiquid())
					{
						state.getBlock().breakBlock(world, pos, state);
					}

					world.setBlockState(pos, filledWith.getDefaultState().withProperty(BlockFluidBase.LEVEL, getPlacedLiquidMetadata()), 3);
				}

				return true;
			}
		}
	}

	public static class DispenserBehavior extends BehaviorDefaultDispenseItem
	{
		@Override
		public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack)
		{
			ItemBucket itembucket = (ItemBucket) itemStack.getItem();
			EnumFacing enumfacing = blockSource.getWorld().getBlockState(blockSource.getBlockPos()).getValue(BlockDispenser.FACING);

			if (itembucket.tryPlaceContainedLiquid(null, blockSource.getWorld(), blockSource.getBlockPos().offset(enumfacing)))
			{
				itemStack.setItem(Items.BUCKET);
				itemStack.stackSize = 1;
				return itemStack;
			}
			else
			{
				return super.dispense(blockSource, itemStack);
			}
		}
	}
}
