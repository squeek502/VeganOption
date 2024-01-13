package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
import squeek.veganoption.content.modules.Syrup;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

import static squeek.veganoption.ModInfo.MODID_LOWER;

@ParametersAreNonnullByDefault
public class SapCauldronBlock extends AbstractCauldronBlock
{
	private static final ResourceKey<DamageType> BOILING_SAP_DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID_LOWER, "boiling_sap"));
	private static final List<Block> HEAT_SOURCES = List.of(Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.LAVA);
	public static final int BOIL_TIME_TICKS = 400;

	public SapCauldronBlock(Properties properties, Map<Item, CauldronInteraction> interactions)
	{
		super(properties, interactions);
		NeoForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean isFull(BlockState state)
	{
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
	{
		if (isEntityInsideContent(state, pos, entity))
		{
			if (HEAT_SOURCES.contains(level.getBlockState(pos.below()).getBlock()))
			{
				DamageSource damageSource = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(BOILING_SAP_DAMAGE_TYPE));
				entity.hurt(damageSource, 2f);
			}
		}
	}

	// Block#neighborChanged is called before the blockstate is actually set in the world, so we have to use this
	@SubscribeEvent
	public void onNeighborPlaced(BlockEvent.EntityPlaceEvent event)
	{
		BlockSnapshot snapshot = event.getBlockSnapshot();
		BlockPos cauldronPos = snapshot.getPos().above();
		LevelAccessor level = snapshot.getLevel();
		if (level != null && level.getBlockState(cauldronPos).is(this) && HEAT_SOURCES.contains(snapshot.getCurrentBlock().getBlock()))
			level.scheduleTick(cauldronPos, this, BOIL_TIME_TICKS);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		if (HEAT_SOURCES.contains(level.getBlockState(pos.below()).getBlock()))
			level.setBlockAndUpdate(pos, Syrup.syrupCauldron.get().defaultBlockState());
	}

	@Override
	protected double getContentHeight(BlockState state)
	{
		// taken from LavaCauldronBlock
		return 0.9375d;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		return 3;
	}
}
