package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.MiscHelper;
import squeek.veganoption.helpers.RandomHelper;
import squeek.veganoption.network.EnderRiftParticleMessage;
import squeek.veganoption.network.NetworkHandler;

import java.util.Random;

public class BlockEnderRift extends EndPortalBlock implements IFluidFlowHandler
{
	public static final int BLOCK_TELEPORT_RADIUS = 4;
	public static final int NAUSEA_LENGTH_IN_SECONDS = 5;

	public BlockEnderRift()
	{
		super(BlockBehaviour.Properties.of()
			.mapColor(MapColor.NONE)
			.noCollission()
			.randomTicks()
			.lightLevel(state -> 15)
			.strength(-1f, 6000000f)
			.noLootTable());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileEntityEnderRift(pos, state);
	}

	@Override
	public boolean onFluidFlowInto(LevelAccessor level, BlockPos pos, int amount)
	{
		// absorb fluid flow
		return true;
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		super.tick(state, level, pos, random);

		BlockPos aboveBlockPos = pos.above();
		BlockPos belowBlockPos = pos.below();
		if (BlockHelper.isWater(level, aboveBlockPos) && level.getBlockState(belowBlockPos).canBeReplaced())
		{
			BlockPos sourceBlockToConsume = BlockHelper.followWaterStreamToSourceBlock(level, aboveBlockPos);
			if (sourceBlockToConsume != null)
			{
				BlockHelper.setBlockToAir(level, sourceBlockToConsume);

				if (!level.isDay())
				{
					level.setBlockAndUpdate(belowBlockPos, Ender.rawEnderBlock.get().defaultBlockState().setValue(LiquidBlock.LEVEL, 7));
				}
				else
				{
					BlockPos[] blocksInRadius = BlockHelper.getBlocksInRadiusAround(pos, BLOCK_TELEPORT_RADIUS);
					blocksInRadius = BlockHelper.filterBlockListToBreakableBlocks(level, blocksInRadius);
					if (blocksInRadius.length > 0)
					{
						// TODO: teleport block to the end?
						BlockPos blockPosToSwallow = blocksInRadius[RandomHelper.random.nextInt(blocksInRadius.length)];
						BlockHelper.setBlockToAir(level, blockPosToSwallow);
						level.playSound(null, blockPosToSwallow, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 1.0F);

						if (!level.isClientSide())
						{
							PacketDistributor.PacketTarget target = PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(blockPosToSwallow.getX(), blockPosToSwallow.getY(), blockPosToSwallow.getZ(), 80, level.dimension()));
							NetworkHandler.channel.send(target, new EnderRiftParticleMessage(blockPosToSwallow.getX(), blockPosToSwallow.getY(), blockPosToSwallow.getZ()));
						}
					}
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void spawnBlockTeleportFX(Level level, double x, double y, double z, Random rand)
	{
		for (int i = 0; i < 128; ++i)
		{
			double posX = x + rand.nextDouble();
			double posY = y + rand.nextDouble();
			double posZ = z + rand.nextDouble();
			double velX = rand.nextDouble() - 0.5D;
			double velY = rand.nextDouble() - 0.5D;
			double velZ = rand.nextDouble() - 0.5D;

			level.addParticle(ParticleTypes.PORTAL, posX, posY, posZ, velX, velY, velZ);
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
	{
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);

		if (!canSurvive(state, level, pos))
			BlockHelper.setBlockToAir(level, pos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		return isValidPortalLocation(level, pos) && super.canSurvive(state, level, pos);
	}

	public static boolean isValidPortalLocation(LevelReader level, BlockPos blockPos)
	{
		for (BlockPos blockToCheck : BlockHelper.getBlocksAdjacentTo(blockPos))
		{
			if (!(level.getBlockState(blockToCheck).getBlock() instanceof BlockEncrustedObsidian))
				return false;
		}
		return true;
	}

	// by not calling the super's method, this also stops colliding entities from teleporting to the end
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
	{
		if (entity instanceof Player player)
			player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, MiscHelper.TICKS_PER_SEC * NAUSEA_LENGTH_IN_SECONDS));
	}
}
