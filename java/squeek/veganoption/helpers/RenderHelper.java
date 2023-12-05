package squeek.veganoption.helpers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class RenderHelper
{
	public static void putStillFluidCube(FluidStack fluid, AABB bounds, int brightness, VertexConsumer buffer)
	{
		Minecraft mc = Minecraft.getInstance();
		IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluid.getFluid());
		ResourceLocation fluidIconLoc = fluidExt.getStillTexture(fluid);
		TextureAtlasSprite fluidIcon = mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidIconLoc);
		int color = fluidExt.getTintColor(fluid);

		for (Direction face : Direction.values())
		{
			putFace(buffer, fluidIcon, bounds, face, color, brightness);
		}
	}

	public static void putFace(VertexConsumer buffer, TextureAtlasSprite sprite, AABB bounds, Direction face, int color, int brightness)
	{
		putFace(buffer, sprite, bounds, face, color, brightness, 16.0D);
	}

	public static void putFace(VertexConsumer buffer, TextureAtlasSprite sprite, AABB bounds, Direction face, int color, int brightness, double size)
	{
		int[] rgba = ColorHelper.toRGBA(color);
		int r = rgba[0], g = rgba[1], b = rgba[2], a = rgba[3];
		int brightnessHighBits = brightness >> 16 & 0xFFFF;
		int brightnessLowBits = brightness & 0xFFFF;

		float minU, minV, maxU, maxV;
		switch (face)
		{
			case DOWN:
			case UP:
				minU = sprite.getU((float) (bounds.minX * size));
				maxU = sprite.getU((float) (bounds.maxX * size));
				minV = sprite.getV((float) (bounds.minZ * size));
				maxV = sprite.getV((float) (bounds.maxZ * size));
				break;
			case NORTH:
			case SOUTH:
				minU = sprite.getU((float) (bounds.maxX * size));
				maxU = sprite.getU((float) (bounds.minX * size));
				minV = sprite.getV((float) (bounds.minY * size));
				maxV = sprite.getV((float) (bounds.maxY * size));
				break;
			case WEST:
			case EAST:
				minU = sprite.getU((float) (bounds.maxZ * size));
				maxU = sprite.getU((float) (bounds.minZ * size));
				minV = sprite.getV((float) (bounds.minY * size));
				maxV = sprite.getV((float) (bounds.maxY * size));
				break;
			default:
				minU = sprite.getU0();
				maxU = sprite.getU1();
				minV = sprite.getV0();
				maxV = sprite.getV1();
		}

		switch (face)
		{
			case DOWN:
				buffer.vertex(bounds.minX, bounds.minY, bounds.minZ).color(r, g, b, a).uv(minU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.minY, bounds.minZ).color(r, g, b, a).uv(maxU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.minY, bounds.maxZ).color(r, g, b, a).uv(maxU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.minX, bounds.minY, bounds.maxZ).color(r, g, b, a).uv(minU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				break;
			case UP:
				buffer.vertex(bounds.minX, bounds.maxY, bounds.minZ).color(r, g, b, a).uv(minU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.minX, bounds.maxY, bounds.maxZ).color(r, g, b, a).uv(minU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ).color(r, g, b, a).uv(maxU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.maxY, bounds.minZ).color(r, g, b, a).uv(maxU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				break;
			case NORTH:
				buffer.vertex(bounds.minX, bounds.minY, bounds.minZ).color(r, g, b, a).uv(minU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.minX, bounds.maxY, bounds.minZ).color(r, g, b, a).uv(minU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.maxY, bounds.minZ).color(r, g, b, a).uv(maxU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.minY, bounds.minZ).color(r, g, b, a).uv(maxU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				break;
			case SOUTH:
				buffer.vertex(bounds.minX, bounds.minY, bounds.maxZ).color(r, g, b, a).uv(maxU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.minY, bounds.maxZ).color(r, g, b, a).uv(minU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ).color(r, g, b, a).uv(minU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.minX, bounds.maxY, bounds.maxZ).color(r, g, b, a).uv(maxU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				break;
			case WEST:
				buffer.vertex(bounds.minX, bounds.minY, bounds.minZ).color(r, g, b, a).uv(maxU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.minX, bounds.minY, bounds.maxZ).color(r, g, b, a).uv(minU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.minX, bounds.maxY, bounds.maxZ).color(r, g, b, a).uv(minU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.minX, bounds.maxY, bounds.minZ).color(r, g, b, a).uv(maxU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				break;
			case EAST:
				buffer.vertex(bounds.maxX, bounds.minY, bounds.minZ).color(r, g, b, a).uv(minU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.maxY, bounds.minZ).color(r, g, b, a).uv(minU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ).color(r, g, b, a).uv(maxU, minV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				buffer.vertex(bounds.maxX, bounds.minY, bounds.maxZ).color(r, g, b, a).uv(maxU, maxV).uv2(brightnessHighBits, brightnessLowBits).endVertex();
				break;
		}
	}
}
