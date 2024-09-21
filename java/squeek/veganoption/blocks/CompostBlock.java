package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.IPlantable;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.content.modules.ToxicMushroom;
import squeek.veganoption.helpers.BlockHelper;
import squeek.veganoption.helpers.TemperatureHelper;

public class CompostBlock extends Block
{
	public CompostBlock()
	{
		super(BlockBehaviour.Properties.of()
			.strength(0.4f)
			.randomTicks()
			.sound(SoundType.GRASS)
			.mapColor(MapColor.DIRT));
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
	{
		soilBuildingTick(level, pos, rand);
		mushroomGrowTick(level, pos, rand);
	}

	protected void soilBuildingTick(ServerLevel level, BlockPos pos, RandomSource rand)
	{
		// passive and subtle soil building

		// skip Direction.DOWN
		Direction randomDirection = Direction.DOWN;
		while (randomDirection == Direction.DOWN)
		{
			randomDirection = Direction.getRandom(rand);
		}
		attemptSoilBuilding(level, pos.relative(randomDirection), rand, randomDirection == Direction.UP);
	}

	protected void mushroomGrowTick(ServerLevel level, BlockPos pos, RandomSource rand)
	{
		// mushroom growth: 2% (half of mushroom spread) chance on random tick given adequate conditions: high humidity
		// (or adjacent to water), air above, moderate temperature, and mushroom-sustaining light level
		if (rand.nextInt(50) == 0 && canGrowMushrooms(level, pos))
		{
			Block mushroom;
			if (rand.nextBoolean())
				mushroom = ToxicMushroom.falseMorelBlock.get();
			else
				mushroom = rand.nextBoolean() ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
			level.setBlockAndUpdate(pos.above(), mushroom.defaultBlockState());
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		MushroomCompostBlock.Variant mushroomVariant = MushroomCompostBlock.Variant.byItem(player.getItemInHand(hand).getItem());
		if (mushroomVariant != null && canGrowMushrooms(level, pos) && !player.isCrouching())
		{
			player.getItemInHand(hand).shrink(1);
			level.setBlockAndUpdate(pos, Composting.mushroomCompost.get().defaultBlockState().setValue(MushroomCompostBlock.VARIANT, mushroomVariant));
			level.playSound(player, pos, SoundEvents.NETHER_WART_PLANTED, SoundSource.PLAYERS);
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return super.use(state, level, pos, player, hand, hit);
	}

	public static boolean canGrowMushrooms(Level level, BlockPos pos)
	{
		float tempF = TemperatureHelper.celsiusToFahrenheit(TemperatureHelper.getBiomeTemperature(level, pos));
		if (tempF >= 57 && tempF <= 75 && level.getBlockState(pos.above()).isAir() && level.getRawBrightness(pos.above(), 0) < 13)
		{
			float humidity = level.getBiome(pos).value().getModifiedClimateSettings().downfall();
			return humidity >= 0.75f || BlockHelper.isAdjacentToWater(level, pos);
		}
		return false;
	}

	public static boolean tryGrowthTickAt(ServerLevel level, BlockPos pos, RandomSource random)
	{
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		if ((block instanceof IPlantable || block instanceof BonemealableBlock) && block.isRandomlyTicking(state))
		{
			block.randomTick(state, level, pos, random);
			return true;
		}
		return false;
	}

	public void attemptSoilBuilding(ServerLevel level, BlockPos pos, RandomSource random, boolean growPlantDirectly)
	{
		tryGrowthTickAt(level, pos.above(), random);

		if (growPlantDirectly)
			tryGrowthTickAt(level, pos, random);
	}
}
