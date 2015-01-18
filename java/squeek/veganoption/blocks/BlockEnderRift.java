package squeek.veganoption.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.BlockHelper.BlockPos;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.helpers.RandomHelper;
import squeek.veganoption.network.MessageBlockTeleport;
import squeek.veganoption.network.NetworkHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnderRift extends BlockEndPortal
{
	public static final int BLOCK_TELEPORT_RADIUS = 4;
	public static final int NAUSEA_LENGTH_IN_SECONDS = 5;

	public static class MaterialEnderRift extends MaterialLogic
	{
		public MaterialEnderRift()
		{
			super(MapColor.airColor);
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
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityEnderRift();
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 0.75F, 1.0F);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		return side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal();
	}

	// absord fluid flow
	public boolean onFluidFlowInto(World world, int x, int y, int z, int flowDecay)
	{
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		super.updateTick(world, x, y, z, random);

		BlockHelper.BlockPos riftBlockPos = BlockHelper.blockPos(world, x, y, z);
		BlockHelper.BlockPos aboveBlockPos = riftBlockPos.getOffset(0, 1, 0);
		if (BlockHelper.isWater(aboveBlockPos) && world.getBlock(x, y - 1, z).isReplaceable(world, x, y - 1, z))
		{
			BlockHelper.BlockPos sourceBlockToConsume = BlockHelper.followWaterStreamToSourceBlock(aboveBlockPos);
			if (sourceBlockToConsume != null)
			{
				world.setBlockToAir(sourceBlockToConsume.x, sourceBlockToConsume.y, sourceBlockToConsume.z);

				if (!world.isDaytime())
				{
					world.setBlock(x, y - 1, z, Ender.rawEnder, 7, 3);
				}
				else
				{
					BlockPos[] blocksInRadius = BlockHelper.getBlocksInRadiusAround(new BlockPos(world, x, y, z), BLOCK_TELEPORT_RADIUS);
					blocksInRadius = BlockHelper.filterBlockListToBreakableBlocks(blocksInRadius);
					if (blocksInRadius.length > 0)
					{
						// TODO: teleport block to the end?
						BlockPos blockPosToSwallow = blocksInRadius[RandomHelper.random.nextInt(blocksInRadius.length)];
						blockPosToSwallow.world.setBlockToAir(blockPosToSwallow.x, blockPosToSwallow.y, blockPosToSwallow.z);
						blockPosToSwallow.world.playSoundEffect(blockPosToSwallow.x, blockPosToSwallow.y, blockPosToSwallow.z, "mob.endermen.portal", 1.0F, 1.0F);

						if (!world.isRemote)
						{
							TargetPoint target = new TargetPoint(world.provider.dimensionId, blockPosToSwallow.x, blockPosToSwallow.y, blockPosToSwallow.z, 80);
							NetworkHandler.channel.sendToAllAround(new MessageBlockTeleport(blockPosToSwallow.x, blockPosToSwallow.y, blockPosToSwallow.z), target);
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

			world.spawnParticle("portal", posX, posY, posZ, velX, velY, velZ);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block changedBlock)
	{
		super.onNeighborBlockChange(world, x, y, z, changedBlock);

		if (!canBlockStay(world, x, y, z))
			world.setBlockToAir(x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return isValidPortalLocation(world, x, y, z) && super.canBlockStay(world, x, y, z);
	}

	public static boolean isValidPortalLocation(World world, int x, int y, int z)
	{
		BlockHelper.BlockPos blockPos = BlockHelper.blockPos(world, x, y, z);
		for (BlockHelper.BlockPos blockToCheck : BlockHelper.getBlocksAdjacentTo(blockPos))
		{
			if (!(blockToCheck.getBlock() instanceof BlockEncrustedObsidian))
				return false;
		}
		return true;
	}

	// by not calling the super's method, this also stops colliding entities from teleporting to the end
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			player.addPotionEffect(new PotionEffect(Potion.confusion.id, MiscHelper.TICKS_PER_SEC * NAUSEA_LENGTH_IN_SECONDS));
		}
		return;
	}
}
