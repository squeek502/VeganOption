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

	// return true to stop the default code from being executed
	public static boolean onEntityItemUpdate(EntityItem entityItem)
	{
		if (!entityItem.worldObj.isRemote && entityItem.isCollided)
		{
			BlockPos pos = new BlockPos(entityItem.posX, entityItem.posY, entityItem.posZ);
			if (entityItem.worldObj.getBlockState(pos).getBlock() == Blocks.PISTON_HEAD)
			{
				return MinecraftForge.EVENT_BUS.post(new PistonEvent.CrushItem(entityItem));
			}
		}
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

	public static final int PISTON_BLOCKEVENT_EXTEND = 0;
	public static final int PISTON_BLOCKEVENT_RETRACT = 1;

	public static void onPistonBlockEventReceived(IBlockState state, World world, BlockPos pos, int eventID)
	{
		if (eventID == PISTON_BLOCKEVENT_RETRACT)
		{
			EnumFacing facing = state.getValue(BlockPistonBase.FACING);
			MinecraftForge.EVENT_BUS.post(new PistonEvent.Retract(world, pos, facing));
		}
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
