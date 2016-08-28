package squeek.veganoption.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;

public class RenderHelper
{
	public static void putStillFluidCube(FluidStack fluid, AxisAlignedBB bounds, int brightness, VertexBuffer buffer)
	{
		Minecraft mc = Minecraft.getMinecraft();
		TextureAtlasSprite fluidIcon = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
		int color = fluid.getFluid().getColor(fluid);

		for (EnumFaceDirection face : EnumFaceDirection.values())
		{
			putFace(buffer, fluidIcon, bounds, face, color, brightness);
		}
	}

	public static void putFace(VertexBuffer buffer, TextureAtlasSprite sprite, AxisAlignedBB bounds, EnumFaceDirection face, int color, int brightness)
	{
		putFace(buffer, sprite, bounds, face, color, brightness, 16.0D);
	}

	public static void putFace(VertexBuffer buffer, TextureAtlasSprite sprite, AxisAlignedBB bounds, EnumFaceDirection face, int color, int brightness, double size)
	{
		int[] rgba = ColorHelper.toRGBA(color);
		int r = rgba[0], g = rgba[1], b = rgba[2], a = rgba[3];
		int brightnessHighBits = brightness >> 16 & 0xFFFF;
		int brightnessLowBits = brightness & 0xFFFF;

		double minU, minV, maxU, maxV;
		switch (face)
		{
			case DOWN:
			case UP:
				minU = sprite.getInterpolatedU(bounds.minX * size);
				maxU = sprite.getInterpolatedU(bounds.maxX * size);
				minV = sprite.getInterpolatedV(bounds.minZ * size);
				maxV = sprite.getInterpolatedV(bounds.maxZ * size);
				break;
			case NORTH:
			case SOUTH:
				minU = sprite.getInterpolatedU(bounds.maxX * size);
				maxU = sprite.getInterpolatedU(bounds.minX * size);
				minV = sprite.getInterpolatedV(bounds.minY * size);
				maxV = sprite.getInterpolatedV(bounds.maxY * size);
				break;
			case WEST:
			case EAST:
				minU = sprite.getInterpolatedU(bounds.maxZ * size);
				maxU = sprite.getInterpolatedU(bounds.minZ * size);
				minV = sprite.getInterpolatedV(bounds.minY * size);
				maxV = sprite.getInterpolatedV(bounds.maxY * size);
				break;
			default:
				minU = sprite.getMinU();
				maxU = sprite.getMaxU();
				minV = sprite.getMinV();
				maxV = sprite.getMaxV();
		}

		switch (face)
		{
			case DOWN:
				buffer.pos(bounds.minX, bounds.minY, bounds.minZ).color(r, g, b, a).tex(minU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.minY, bounds.minZ).color(r, g, b, a).tex(maxU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.minY, bounds.maxZ).color(r, g, b, a).tex(maxU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.minX, bounds.minY, bounds.maxZ).color(r, g, b, a).tex(minU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				break;
			case UP:
				buffer.pos(bounds.minX, bounds.maxY, bounds.minZ).color(r, g, b, a).tex(minU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.minX, bounds.maxY, bounds.maxZ).color(r, g, b, a).tex(minU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.maxY, bounds.maxZ).color(r, g, b, a).tex(maxU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.maxY, bounds.minZ).color(r, g, b, a).tex(maxU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				break;
			case NORTH:
				buffer.pos(bounds.minX, bounds.minY, bounds.minZ).color(r, g, b, a).tex(minU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.minX, bounds.maxY, bounds.minZ).color(r, g, b, a).tex(minU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.maxY, bounds.minZ).color(r, g, b, a).tex(maxU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.minY, bounds.minZ).color(r, g, b, a).tex(maxU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				break;
			case SOUTH:
				buffer.pos(bounds.minX, bounds.minY, bounds.maxZ).color(r, g, b, a).tex(maxU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.minY, bounds.maxZ).color(r, g, b, a).tex(minU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.maxY, bounds.maxZ).color(r, g, b, a).tex(minU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.minX, bounds.maxY, bounds.maxZ).color(r, g, b, a).tex(maxU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				break;
			case WEST:
				buffer.pos(bounds.minX, bounds.minY, bounds.minZ).color(r, g, b, a).tex(maxU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.minX, bounds.minY, bounds.maxZ).color(r, g, b, a).tex(minU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.minX, bounds.maxY, bounds.maxZ).color(r, g, b, a).tex(minU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.minX, bounds.maxY, bounds.minZ).color(r, g, b, a).tex(maxU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				break;
			case EAST:
				buffer.pos(bounds.maxX, bounds.minY, bounds.minZ).color(r, g, b, a).tex(minU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.maxY, bounds.minZ).color(r, g, b, a).tex(minU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.maxY, bounds.maxZ).color(r, g, b, a).tex(maxU, minV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.pos(bounds.maxX, bounds.minY, bounds.maxZ).color(r, g, b, a).tex(maxU, maxV).lightmap(brightnessHighBits, brightnessLowBits).endVertex();
				break;
		}
	}
}
