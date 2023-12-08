package squeek.veganoption.blocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import squeek.veganoption.content.modules.StrawBed;

import static squeek.veganoption.ModInfo.MODID_LOWER;

public class BlockBedStraw extends BedBlock
{
	private static final ResourceKey<DamageType> ITCHY_DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID_LOWER, "itchy_bed"));
	public static final int ITCH_DAMAGE = 2;

	public BlockBedStraw()
	{
		super(DyeColor.YELLOW, BlockBehaviour.Properties.of()
			.mapColor(state -> state.getValue(BedBlock.PART) == BedPart.FOOT ? MapColor.COLOR_YELLOW : MapColor.WOOL)
			.sound(SoundType.WOOD)
			.strength(0.2F)
			.noOcclusion()
			.ignitedByLava()
			.pushReaction(PushReaction.DESTROY));
		NeoForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onWakeUp(PlayerWakeUpEvent event)
	{
		// this flag combination should only be set when the sleep was successful
		// and the server is waking all sleeping players
		boolean wokenByWakeAllPlayers = !event.wakeImmediately() && !event.updateLevel();
		if (!wokenByWakeAllPlayers)
			return;

		Level level = event.getEntity().level();

		if (!level.getBlockState(event.getEntity().blockPosition()).is(StrawBed.bedStrawBlock.get()))
			return;

		if (!level.isClientSide())
		{
			DamageSource itchyDamageSource = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ITCHY_DAMAGE_TYPE));
			event.getEntity().hurt(itchyDamageSource, ITCH_DAMAGE);
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		// The vanilla bed uses a BlockEntityRenderer in order to render the colors. The straw bed can't be dyed, so we can just use a json model.
		return RenderShape.MODEL;
	}
}
