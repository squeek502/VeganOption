package squeek.veganoption.asm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import squeek.veganoption.api.event.PistonEvent;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.blocks.IHollowBlock;
import squeek.veganoption.content.modules.Ender;

public class Hooks
{
	// return true to stop the default code from being executed
	public static boolean onFlowIntoBlock(World world, BlockPos pos, IBlockState state, int flowDecay)
	{
		Block block = world.getBlockState(pos).getBlock();
		if (block == Ender.enderRift)
			return ((BlockEnderRift) block).onFluidFlowInto(world, pos, flowDecay);
		else
			return false;
	}

	public static void onPistonMove(World world, BlockPos pos, EnumFacing direction, boolean extending)
	{
		if (extending)
		{
			IBlockState state = world.getBlockState(pos);
			MinecraftForge.EVENT_BUS.post(new PistonEvent.TryExtend(world, pos, state.getValue(BlockPistonBase.FACING)));
		}
	}

	public static void onPistonTileUpdate(World world, BlockPos pos, float progress, boolean extending)
	{
		if (extending)
			MinecraftForge.EVENT_BUS.post(new PistonEvent.Extending(world, pos, progress));
	}

	// return -1 to use default code, otherwise return 1 or 0 (will be interpretted as a boolean)
	public static int isBlockFullCube(World world, BlockPos pos)
	{
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof IHollowBlock)
		{
			return ((IHollowBlock) block).isBlockFullCube(world, pos) ? 1 : 0;
		}
		return -1;
	}
}
