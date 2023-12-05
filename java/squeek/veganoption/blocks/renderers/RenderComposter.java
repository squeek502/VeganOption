package squeek.veganoption.blocks.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockComposter;
import squeek.veganoption.blocks.tiles.TileEntityComposter;

import java.util.Calendar;

@OnlyIn(Dist.CLIENT)
public class RenderComposter implements BlockEntityRenderer<TileEntityComposter>
{
	private static final ResourceLocation OVERLAY_BLUE = new ResourceLocation(ModInfo.MODID_LOWER, "textures/entity/composter/temperature_overlay_blue.png");
	private static final ResourceLocation OVERLAY_YELLOW = new ResourceLocation(ModInfo.MODID_LOWER, "textures/entity/composter/temperature_overlay_yellow.png");
	private static final ResourceLocation OVERLAY_ORANGE = new ResourceLocation(ModInfo.MODID_LOWER, "textures/entity/composter/temperature_overlay_orange.png");
	private static final ResourceLocation OVERLAY_RED = new ResourceLocation(ModInfo.MODID_LOWER, "textures/entity/composter/temperature_overlay_red.png");

	private final boolean isChristmas;
	private static final String BOTTOM = "bottom";
	private static final String LID = "lid";
	private static final String LOCK = "lock";
	private final ModelPart lid;
	private final ModelPart bottom;
	private final ModelPart lock;

	public RenderComposter(BlockEntityRendererProvider.Context context)
	{
		Calendar calendar = Calendar.getInstance();
		int today = calendar.get(Calendar.DAY_OF_MONTH);
		isChristmas = calendar.get(Calendar.MONTH) == Calendar.DECEMBER && today >= 24 && today <= 26;

		ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
		bottom = modelpart.getChild(BOTTOM);
		lid = modelpart.getChild(LID);
		lock = modelpart.getChild(LOCK);
	}

	@Override
	public void render(TileEntityComposter tile, float partialTickTime, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay)
	{
		BlockState state = tile.getBlockState();
		Direction dir = state.getValue(BlockComposter.FACING);
		Direction.Axis axis = dir.getAxis();

		pose.popPose();
		pose.translate((axis == Direction.Axis.X ? 0.1F : 0F), 1F, (axis == Direction.Axis.X ? 1F : 0.9F));
		pose.mulPose(Axis.YP.rotationDegrees(-dir.toYRot()));
		pose.translate(0.5f, 0.5f, 0.5f);

		if (tile.isAerating())
		{
			final int num_turns = 3;
			pose.mulPose(new Quaternionf(axis == Direction.Axis.X ? 0F : 1F, 0F, axis == Direction.Axis.X ? 1F : 0F, -(tile.getAeratingPercent() * 360F * num_turns)));
		}

		float openness = tile.getLidOpenness(partialTickTime);
		openness = 1.0F - openness;
		openness = 1.0F - openness * openness * openness;

		Material material = isChristmas ? Sheets.CHEST_XMAS_LOCATION : Sheets.CHEST_LOCATION;
		VertexConsumer consumer = material.buffer(buffer, RenderType::entityCutout);

		lid.xRot = -(openness * (float) (Math.PI / 2));
		lock.xRot = lid.xRot;
		lid.render(pose, consumer, packedLight, packedOverlay);
		lock.render(pose, consumer, packedLight, packedOverlay);
		bottom.render(pose, consumer, packedLight, packedOverlay);

		// idk...
		AbstractTexture overlay = Minecraft.getInstance().getTextureManager().getTexture(getOverlayTextureFromTemperature(tile.getCompostTemperature()));
		RenderSystem.setShaderTexture(0, overlay.getId());
		overlay.bind();

		pose.popPose();
	}

	public ResourceLocation getOverlayTextureFromTemperature(float temperature)
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
}
