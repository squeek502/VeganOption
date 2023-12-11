package squeek.veganoption.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import squeek.veganoption.content.modules.Soap;

import static squeek.veganoption.ModInfo.MODID_LOWER;

public class BlockLyeWater extends LiquidBlock
{
	private static final ResourceKey<DamageType> LYE_DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID_LOWER, "lye_water"));

	public BlockLyeWater()
	{
		super((FlowingFluid) Soap.fluidLyeWaterStill.get(), BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_BROWN)
			.replaceable()
			.noCollission()
			.pushReaction(PushReaction.DESTROY)
			.noLootTable()
			.liquid()
			.sound(SoundType.EMPTY));
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
	{
		if (entity instanceof LivingEntity)
		{
			DamageSource source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(LYE_DAMAGE_TYPE));
			entity.hurt(source, 0.5f);
		}
		super.entityInside(state, level, pos, entity);
	}
}
