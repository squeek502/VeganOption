package squeek.veganoption.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLogic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.helpers.RandomHelper;
import squeek.veganoption.network.MessageFX;
import squeek.veganoption.network.NetworkHandler;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockEnderRift extends BlockEndPortal implements IFluidFlowHandler
{
	public static final int BLOCK_TELEPORT_RADIUS = 4;
	public static final int NAUSEA_LENGTH_IN_SECONDS = 5;

	public static class MaterialEnderRift extends MaterialLogic
	{
		public MaterialEnderRift()
		{
			super(MapColor.AIR);
			this.setImmovableMobility();
		}
	}

	public static MaterialEnderRift materialEnderRift = new MaterialEnderRift();

	public BlockEnderRift()
	{
		super(materialEnderRift);
		setTickRandomly(true);
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityEnderRift();
	}

	@Override
	public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side)
	{
		return side == EnumFacing.UP || side == EnumFacing.DOWN;
	}

	@Override
	public boolean onFluidFlowInto(World world, BlockPos pos, int flowDecay)
	{
		// absorb fluid flow
		return true;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
	{
		super.updateTick(world, pos, state, random);

		BlockPos aboveBlockPos = pos.up();
		BlockPos belowBlockPos = pos.down();
		if (BlockHelper.isWater(world, aboveBlockPos) && world.getBlockState(belowBlockPos).getBlock().isReplaceable(world, belowBlockPos))
		{
			BlockPos sourceBlockToConsume = BlockHelper.followWaterStreamToSourceBlock(world, aboveBlockPos);
			if (sourceBlockToConsume != null)
			{
				world.setBlockToAir(sourceBlockToConsume);

				if (!world.isDaytime())
				{
					world.setBlockState(belowBlockPos, Ender.rawEnder.getDefaultState().withProperty(BlockFluidBase.LEVEL, 7));
				}
				else
				{
					BlockPos[] blocksInRadius = BlockHelper.getBlocksInRadiusAround(pos, BLOCK_TELEPORT_RADIUS);
					blocksInRadius = BlockHelper.filterBlockListToBreakableBlocks(world, blocksInRadius);
					if (blocksInRadius.length > 0)
					{
						// TODO: teleport block to the end?
						BlockPos blockPosToSwallow = blocksInRadius[RandomHelper.random.nextInt(blocksInRadius.length)];
						world.setBlockToAir(blockPosToSwallow);
						world.playSound(blockPosToSwallow.getX(), blockPosToSwallow.getY(), blockPosToSwallow.getZ(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

						if (!world.isRemote)
						{
							NetworkRegistry.TargetPoint target = new NetworkRegistry.TargetPoint(world.provider.getDimension(), blockPosToSwallow.getX(), blockPosToSwallow.getY(), blockPosToSwallow.getZ(), 80);
							NetworkHandler.channel.sendToAllAround(new MessageFX(blockPosToSwallow.getX(), blockPosToSwallow.getY(), blockPosToSwallow.getZ(), MessageFX.FX.BLOCK_TELEPORT), target);
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void spawnBlockTeleportFX(World world, double x, double y, double z, Random rand)
	{
		for (int i = 0; i < 128; ++i)
		{
			double posX = x + rand.nextDouble();
			double posY = y + rand.nextDouble();
			double posZ = z + rand.nextDouble();
			double velX = rand.nextDouble() - 0.5D;
			double velY = rand.nextDouble() - 0.5D;
			double velZ = rand.nextDouble() - 0.5D;

			world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY, posZ, velX, velY, velZ);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block changedBlock, BlockPos fromPos)
	{
		super.neighborChanged(state, world, pos, changedBlock, fromPos);

		if (!canPlaceBlockAt(world, pos))
			world.setBlockToAir(pos);
	}

	@Override
	public boolean canPlaceBlockAt(World world, @Nonnull BlockPos pos)
	{
		return isValidPortalLocation(world, pos) && super.canPlaceBlockAt(world, pos);
	}

	public static boolean isValidPortalLocation(World world, BlockPos blockPos)
	{
		for (BlockPos blockToCheck : BlockHelper.getBlocksAdjacentTo(blockPos))
		{
			if (!(world.getBlockState(blockToCheck).getBlock() instanceof BlockEncrustedObsidian))
				return false;
		}
		return true;
	}

	// by not calling the super's method, this also stops colliding entities from teleporting to the end
	@Override
	public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, MiscHelper.TICKS_PER_SEC * NAUSEA_LENGTH_IN_SECONDS));
		}
	}

	// by not calling the super's method, this stops the portal removing itself immediately in certain dimensions
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
	}
}
