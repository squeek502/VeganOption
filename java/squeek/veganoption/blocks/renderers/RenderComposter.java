package squeek.veganoption.blocks.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockComposter;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.content.modules.Composting;

import java.util.Calendar;

@OnlyIn(Dist.CLIENT)
public class RenderComposter implements BlockEntityRenderer<TileEntityComposter>
{
	private static final ResourceLocation OVERLAY_BLUE = createOverlayResourceLocation("blue");
	private static final ResourceLocation OVERLAY_YELLOW = createOverlayResourceLocation("yellow");
	private static final ResourceLocation OVERLAY_ORANGE = createOverlayResourceLocation("orange");
	private static final ResourceLocation OVERLAY_RED = createOverlayResourceLocation("red");

	public static final ResourceLocation LEGS_MODEL = new ResourceLocation(ModInfo.MODID_LOWER, "block/composter_legs");

	private final boolean isChristmas;
	private static final String BOTTOM = "bottom";
	private static final String LID = "lid";
	private static final String LOCK = "lock";
	private final ModelPart lid;
	private final ModelPart bottom;
	private final ModelPart lock;
	private BakedModel legs;
	private final BlockRenderDispatcher blockRenderer;

	public RenderComposter(BlockEntityRendererProvider.Context context)
	{
		Calendar calendar = Calendar.getInstance();
		int today = calendar.get(Calendar.DAY_OF_MONTH);
		isChristmas = calendar.get(Calendar.MONTH) == Calendar.DECEMBER && today >= 24 && today <= 26;

		ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
		bottom = modelpart.getChild(BOTTOM);
		lid = modelpart.getChild(LID);
		lock = modelpart.getChild(LOCK);

		blockRenderer = context.getBlockRenderDispatcher();
	}

	@Override
	public void render(TileEntityComposter tile, float partialTickTime, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay)
	{
		// level is null for the item renderer -- rotate it to face the proper direction for rendering in-inventory
		BlockState state = tile.hasLevel() ? tile.getBlockState() : Composting.composter.get().defaultBlockState().setValue(BlockComposter.FACING, Direction.SOUTH);
		Direction dir = state.getValue(BlockComposter.FACING);

		if (legs == null)
			legs = Minecraft.getInstance().getModelManager().getModel(LEGS_MODEL);
		pose.pushPose();
		pose.translate(0.5f, 0.5f, 0.5f);
		pose.mulPose(Axis.YP.rotationDegrees(-dir.toYRot()));
		pose.translate(-0.5f, -0.5f, -0.5f);
		blockRenderer.getModelRenderer().renderModel(pose.last(), buffer.getBuffer(RenderType.cutout()), state, legs, 1f, 1f, 1f, packedLight, packedOverlay);
		pose.popPose();

		pose.pushPose();
		pose.translate(0.5f, 0.5f, 0.5f);
		rotateAndTumble(tile, dir, pose, 1f);
		pose.mulPose(Axis.YP.rotationDegrees(-dir.toYRot()));
		pose.translate(-0.5f, -0.5f, -0.5f);

		float openness = tile.getLidOpenness(partialTickTime);
		openness = 1.0F - openness;
		openness = 1.0F - openness * openness * openness;

		lid.xRot = -(openness * (float) (Math.PI / 2));
		lock.xRot = lid.xRot;

		Material material = isChristmas ? Sheets.CHEST_XMAS_LOCATION : Sheets.CHEST_LOCATION;
		VertexConsumer baseConsumer = material.buffer(buffer, RenderType::entityCutout);

		lid.render(pose, baseConsumer, packedLight, packedOverlay);
		lock.render(pose, baseConsumer, packedLight, packedOverlay);
		bottom.render(pose, baseConsumer, packedLight, packedOverlay);

		pose.popPose();

		// only render overlay texture for the actual block, not for the item which cannot have a temperature
		if (tile.hasLevel())
		{
			pose.pushPose();
			pose.translate(0.5f, 0.5f, 0.5f);
			rotateAndTumble(tile, dir, pose, 1.001f);
			pose.mulPose(Axis.YP.rotationDegrees(-dir.toYRot()));
			pose.translate(-0.5f, -0.5f, -0.5f);

			VertexConsumer overlayConsumer = buffer.getBuffer(RenderType.entityCutout(getOverlayTextureFromTemperature(tile.getCompostTemperature())));

			lid.render(pose, overlayConsumer, packedLight, packedOverlay);
			lock.render(pose, overlayConsumer, packedLight, packedOverlay);
			bottom.render(pose, overlayConsumer, packedLight, packedOverlay);

			pose.popPose();
		}
	}

	private void rotateAndTumble(TileEntityComposter tile, Direction dir, PoseStack pose, float scaleFactor)
	{
		Direction.Axis axis = dir.getAxis();
		pose.scale(scaleFactor * (axis == Direction.Axis.X ? 0.8F : 1F), scaleFactor * 0.8F, scaleFactor * (axis == Direction.Axis.X ? 1F : 0.8F));

		// if level is null, tile is the dummy BE for the item renderer and cannot aerate
		if (tile.hasLevel() && tile.isAerating())
		{
			final int num_turns = 3;
			float angle = tile.getAeratingPercent() * 360f * num_turns;
			if (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
				pose.mulPose(axis == Direction.Axis.X ? Axis.ZN.rotationDegrees(angle) : Axis.XP.rotationDegrees(angle));
			else
				pose.mulPose(axis == Direction.Axis.X ? Axis.ZP.rotationDegrees(angle) : Axis.XN.rotationDegrees(angle));
		}
	}

	private ResourceLocation getOverlayTextureFromTemperature(float temperature)
	{
		temperature = Math.round(temperature);

		if (temperature >= TileEntityComposter.MAX_COMPOST_TEMPERATURE)
			return OVERLAY_RED;
		else if (temperature >= TileEntityComposter.THERMOPHILIC_RANGE_START)
			return OVERLAY_ORANGE;
		else if (temperature >= TileEntityComposter.MESOPHILIC_RANGE_START)
			return OVERLAY_YELLOW;
		else
			return OVERLAY_BLUE;
	}

	private static ResourceLocation createOverlayResourceLocation(String color)
	{
		return new ResourceLocation(ModInfo.MODID_LOWER, "textures/entity/composter/temperature_overlay_" + color + ".png");
	}

	public static class ComposterItemRenderer extends BlockEntityWithoutLevelRenderer
	{
		private final TileEntityComposter dummyBlockEntity = new TileEntityComposter(BlockPos.ZERO, Composting.composter.get().defaultBlockState());
		private final BlockEntityRenderDispatcher dispatcher;

		public ComposterItemRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet)
		{
			super(renderDispatcher, modelSet);
			this.dispatcher = renderDispatcher;
		}

		@Override
		public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay)
		{
			dispatcher.renderItem(dummyBlockEntity, pose, buffer, packedLight, packedOverlay);
		}
	}
}
