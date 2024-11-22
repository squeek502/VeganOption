package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.content.modules.ToxicMushroom;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MushroomCompostBlock extends CompostBlock
{
	public static final EnumProperty<Variant> VARIANT = EnumProperty.create("variant", Variant.class);
	public static final BooleanProperty HAS_SPROUTED = BooleanProperty.create("has_sprouted");

	public MushroomCompostBlock()
	{
		super();
		registerDefaultState(getStateDefinition().any().setValue(HAS_SPROUTED, false).setValue(VARIANT, Variant.RED));
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
	{
		// Skip mushroom grow tick for our own mushroom mechanics.
		super.soilBuildingTick(level, pos, rand);

		// first growth: 2% chance (half regular mushroom growth); after that, 8.333...% chance
		int growthChance = state.getValue(HAS_SPROUTED) ? 12 : 50;
		if (rand.nextInt(growthChance) == 0 && canGrowMushrooms(level, pos))
		{
			level.setBlockAndUpdate(pos.above(), state.getValue(VARIANT).block.get().defaultBlockState());
			if (state.getValue(HAS_SPROUTED))
			{
				// 5% chance of nutrients being depleted.
				if (rand.nextInt(20) == 0)
					level.setBlockAndUpdate(pos, Composting.spentCompost.get().defaultBlockState());
			}
			else
			{
				level.setBlockAndUpdate(pos, state.setValue(HAS_SPROUTED, true));
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		return InteractionResult.PASS;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(VARIANT);
		builder.add(HAS_SPROUTED);
	}

	public enum Variant implements StringRepresentable
	{
		RED("red", () -> Items.RED_MUSHROOM, () -> Blocks.RED_MUSHROOM),
		BROWN("brown", () -> Items.BROWN_MUSHROOM, () -> Blocks.BROWN_MUSHROOM),
		TOXIC("toxic", ToxicMushroom.falseMorel, ToxicMushroom.falseMorelBlock);

		private final String name;
		private final Supplier<? extends Item> item;
		private final Supplier<? extends Block> block;

		Variant(String name, Supplier<? extends Item> item, Supplier<? extends Block> block)
		{
			this.name = name;
			this.item = item;
			this.block = block;
		}

		public String getSerializedName()
		{
			return name;
		}

		public Supplier<? extends Item> getItem()
		{
			return item;
		}

		@Nullable
		public static Variant byItem(Item item)
		{
			for (Variant v : values())
			{
				if (v.item.get() == item)
					return v;
			}
			return null;
		}
	}
}
