package squeek.veganoption.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.BlockFluidFinite;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemBucketGeneric extends ItemBucket
{
	public Block filledWith;

	public ItemBucketGeneric(Block filledWith)
	{
		super(filledWith);
		this.filledWith = filledWith;
		MinecraftForge.EVENT_BUS.register(this);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new ItemBucketGeneric.DispenserBehavior());
	}

	@SubscribeEvent
	public void onFillBucket(FillBucketEvent event)
	{
		if (event.isCanceled() || event.getResult() != Event.Result.DEFAULT)
			return;

		Block block = event.world.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ);

		if (block == filledWith && event.world.getBlockMetadata(event.target.blockX, event.target.blockY, event.target.blockZ) == getPlacedLiquidMetadata())
		{
			event.world.setBlockToAir(event.target.blockX, event.target.blockY, event.target.blockZ);
			event.result = new ItemStack(this);
			event.setResult(Event.Result.ALLOW);
		}
	}

	public int getPlacedLiquidMetadata()
	{
		return filledWith instanceof BlockFluidFinite ? 7 : 0;
	}

	@Override
	public boolean tryPlaceContainedLiquid(World world, int x, int y, int z)
	{
		if (this.filledWith == Blocks.air)
		{
			return false;
		}
		else
		{
			Material material = world.getBlock(x, y, z).getMaterial();
			boolean flag = !material.isSolid();

			if (!world.isAirBlock(x, y, z) && !flag)
			{
				return false;
			}
			else
			{
				if (world.provider.isHellWorld && this.filledWith == Blocks.flowing_water)
				{
					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

					for (int l = 0; l < 8; ++l)
					{
						world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0.0D, 0.0D, 0.0D);
					}
				}
				else
				{
					if (!world.isRemote && flag && !material.isLiquid())
					{
						world.func_147480_a(x, y, z, true);
					}

					world.setBlock(x, y, z, this.filledWith, getPlacedLiquidMetadata(), 3);
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
			int x = blockSource.getXInt();
			int y = blockSource.getYInt();
			int z = blockSource.getZInt();
			EnumFacing enumfacing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());

			if (itembucket.tryPlaceContainedLiquid(blockSource.getWorld(), x + enumfacing.getFrontOffsetX(), y + enumfacing.getFrontOffsetY(), z + enumfacing.getFrontOffsetZ()))
			{
				itemStack.func_150996_a(Items.bucket);
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
