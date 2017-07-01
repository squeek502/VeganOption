package squeek.veganoption.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Necessary because Blocks.bed is hardcoded in ItemBed's onItemUse method
 */
public class ItemBedGeneric extends ItemBed
{
	public final BlockBed bed;

	public ItemBedGeneric(BlockBed bed)
	{
		super();
		this.bed = bed;
	}

	// gross duplication
	// required because Blocks.bed is hardcoded in ItemBed.onItemUse
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = playerIn.getHeldItem(hand);
		if (worldIn.isRemote)
		{
			return EnumActionResult.SUCCESS;
		}
		else if (facing != EnumFacing.UP)
		{
			return EnumActionResult.FAIL;
		}
		else
		{
			IBlockState iblockstate = worldIn.getBlockState(pos);
			Block block = iblockstate.getBlock();
			boolean flag = block.isReplaceable(worldIn, pos);

			if (!flag)
			{
				pos = pos.up();
			}

			int i = MathHelper.floor((double) (playerIn.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			EnumFacing enumfacing = EnumFacing.getHorizontal(i);
			BlockPos blockpos = pos.offset(enumfacing);

			if (playerIn.canPlayerEdit(pos, facing, stack) && playerIn.canPlayerEdit(blockpos, facing, stack))
			{
				boolean flag1 = worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
				boolean flag2 = flag || worldIn.isAirBlock(pos);
				boolean flag3 = flag1 || worldIn.isAirBlock(blockpos);

				if (flag2 && flag3 && worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos, EnumFacing.UP) && worldIn.getBlockState(blockpos.down()).isSideSolid(worldIn, pos, EnumFacing.UP))
				{
					IBlockState iblockstate1 = bed.getDefaultState().withProperty(BlockBed.OCCUPIED, false).withProperty(BlockBed.FACING, enumfacing).withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);

					if (worldIn.setBlockState(pos, iblockstate1, 11))
					{
						IBlockState iblockstate2 = iblockstate1.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
						worldIn.setBlockState(blockpos, iblockstate2, 11);
					}

					SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, playerIn);
					worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
				else
				{
					return EnumActionResult.FAIL;
				}
			}
			else
			{
				return EnumActionResult.FAIL;
			}
		}
	}
}
