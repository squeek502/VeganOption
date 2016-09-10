package squeek.veganoption.items;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFertilizer extends Item
{
	public ItemFertilizer()
	{
		super();
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new ItemFertilizer.DispenserBehavior());
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (ItemDye.applyBonemeal(stack, world, pos, player))
		{
			if (!world.isRemote)
			{
				// Bone meal effect
				world.playEvent(2005, pos, 0);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	// copied from bonemeal's dispenser behavior in net.minecraft.init.Bootstrap
	public static class DispenserBehavior extends BehaviorDefaultDispenseItem
	{
		private boolean didFertilize = true;

		@Override
		protected ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack)
		{
			EnumFacing enumfacing = blockSource.getWorld().getBlockState(blockSource.getBlockPos()).getValue(BlockDispenser.FACING);
			World world = blockSource.getWorld();
			int x = (int) blockSource.getX() + enumfacing.getFrontOffsetX();
			int y = (int) blockSource.getY() + enumfacing.getFrontOffsetY();
			int z = (int) blockSource.getZ() + enumfacing.getFrontOffsetZ();
			BlockPos pos = new BlockPos(x, y, z);

			if (ItemDye.applyBonemeal(itemStack, world, pos))
			{
				if (!world.isRemote)
				{
					world.playEvent(2005, pos, 0);
				}
				this.didFertilize = true;
			}
			else
			{
				this.didFertilize = false;
			}

			return itemStack;
		}

		@Override
		protected void playDispenseSound(IBlockSource blockSource)
		{
			if (this.didFertilize)
			{
				blockSource.getWorld().playEvent(1000, blockSource.getBlockPos(), 0);
			}
			else
			{
				blockSource.getWorld().playEvent(1001, blockSource.getBlockPos(), 0);
			}
		}
	}
}
