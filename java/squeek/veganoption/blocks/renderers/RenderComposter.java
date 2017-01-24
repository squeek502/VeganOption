package squeek.veganoption.blocks.renderers;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.BlockComposter;
import squeek.veganoption.blocks.tiles.TileEntityComposter;

import java.util.Calendar;

@SideOnly(Side.CLIENT)
public class RenderComposter extends TileEntitySpecialRenderer<TileEntityComposter>
{
	private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
	private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
	private static final ResourceLocation[] TEXTURE_TEMPERATURE_OVERLAYS = new ResourceLocation[]{
		new ResourceLocation(ModInfo.MODID_LOWER, "textures/entity/composter/temperature_overlay_blue.png"),
		new ResourceLocation(ModInfo.MODID_LOWER, "textures/entity/composter/temperature_overlay_yellow.png"),
		new ResourceLocation(ModInfo.MODID_LOWER, "textures/entity/composter/temperature_overlay_orange.png"),
		new ResourceLocation(ModInfo.MODID_LOWER, "textures/entity/composter/temperature_overlay_red.png")
	};
	private ModelChest modelChest = new ModelChest();
	private boolean isChristmas;

	protected enum Axis
	{
		X, Y
	}

	public RenderComposter()
	{
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
		{
			isChristmas = true;
		}
	}

	@Override
	public void renderTileEntityAt(TileEntityComposter tile, double x, double y, double z, float partialTickTime, int destroyStage)
	{
		EnumFacing side = tile.hasWorldObj() ? tile.getWorld().getBlockState(tile.getPos()).getValue(BlockComposter.FACING) : EnumFacing.WEST;

		short rotation = 0;
		switch (side)
		{
			case NORTH:
				rotation = 0;
				break;
			case SOUTH:
				rotation = 180;
				break;
			case EAST:
				rotation = 90;
				break;
			case WEST:
				rotation = -90;
				break;
		}

		Axis axis = Math.abs(rotation) == 90 ? Axis.X : Axis.Y;

		ModelChest modelchest = modelChest;

		if (MinecraftForgeClient.getRenderPass() <= 0)
		{
			bindTexture(isChristmas ? TEXTURE_CHRISTMAS : TEXTURE_NORMAL);
		}
		else
			bindTexture(TEXTURE_TEMPERATURE_OVERLAYS[getOverlayIndexFromTemperature(tile.getCompostTemperature())]);

		GlStateManager.pushMatrix();
		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.translate(x + (axis == Axis.X ? 0.1F : 0F), y + 1F, z + (axis == Axis.X ? 1F : 0.9F));
		GlStateManager.scale(axis == Axis.X ? 0.8F : 1F, -0.8F, axis == Axis.X ? -1F : -0.8F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);

		if (tile.hasWorldObj() && tile.isAerating())
		{
			final int num_turns = 3;
			GlStateManager.rotate(-(tile.getAeratingPercent() * 360F * num_turns), axis == Axis.X ? 0F : 1F, 0F, axis == Axis.X ? 1F : 0F);
		}

		GlStateManager.rotate(rotation, 0F, 1F, 0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		float f1 = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTickTime;

		f1 = 1.0F - f1;
		f1 = 1.0F - f1 * f1 * f1;
		modelchest.chestLid.rotateAngleX = -(f1 * (float) Math.PI / 2.0F);
		modelchest.renderAll();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
	}

	public int getOverlayIndexFromTemperature(float temperature)
	{
		temperature = Math.round(temperature);

		if (temperature >= TileEntityComposter.MAX_COMPOST_TEMPERATURE)
			return 3;
		else if (temperature >= TileEntityComposter.THERMOPHILIC_RANGE_START)
			return 2;
		else if (temperature >= TileEntityComposter.MESOPHILIC_RANGE_START)
			return 1;
		else
			return 0;
	}
}
