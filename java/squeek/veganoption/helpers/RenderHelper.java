package squeek.veganoption.helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class RenderHelper
{
	public static void putStillFluidCube(FluidStack fluid, AABB bounds, int brightness, VertexConsumer buffer, PoseStack ps)
	{
		Minecraft mc = Minecraft.getInstance();
		IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluid.getFluid());
		ResourceLocation fluidIconLoc = fluidExt.getStillTexture(fluid);
		TextureAtlasSprite fluidIcon = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidIconLoc);
		int color = fluidExt.getTintColor(fluid);

		for (Direction face : Direction.values())
		{
			putFace(ps, buffer, fluidIcon, bounds, face, color, brightness);
		}
	}

	public static void putFace(PoseStack ps, VertexConsumer buffer, TextureAtlasSprite sprite, AABB bounds, Direction face, int color, int brightness)
	{
		int[] rgba = ColorHelper.toRGBA(color);
		int r = rgba[0], g = rgba[1], b = rgba[2], a = rgba[3];

		float minU, minV, maxU, maxV;
		switch (face.getAxis())
		{
			case Y:
				minU = sprite.getU((float) (bounds.minX));
				maxU = sprite.getU((float) (bounds.maxX));
				minV = sprite.getV((float) (bounds.minZ));
				maxV = sprite.getV((float) (bounds.maxZ));
				break;
			case Z:
				minU = sprite.getU((float) (bounds.maxX));
				maxU = sprite.getU((float) (bounds.minX));
				minV = sprite.getV((float) (bounds.minY));
				maxV = sprite.getV((float) (bounds.maxY));
				break;
			case X:
				minU = sprite.getU((float) (bounds.maxZ));
				maxU = sprite.getU((float) (bounds.minZ));
				minV = sprite.getV((float) (bounds.minY));
				maxV = sprite.getV((float) (bounds.maxY));
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
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.minY, (float) bounds.minZ, r, g, b, a, minU, minV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.minY, (float) bounds.minZ, r, g, b, a, maxU, minV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.minY, (float) bounds.maxZ, r, g, b, a, maxU, maxV, brightness, face);
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.minY, (float) bounds.maxZ, r, g, b, a, minU, maxV, brightness, face);
				break;
			case UP:
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.maxY, (float) bounds.minZ, r, g, b, a, minU, minV, brightness, face);
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.maxY, (float) bounds.maxZ, r, g, b, a, minU, maxV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ, r, g, b, a, maxU, maxV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.minZ, r, g, b, a, maxU, minV, brightness, face);
				break;
			case NORTH:
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.minY, (float) bounds.minZ, r, g, b, a, minU, maxV, brightness, face);
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.maxY, (float) bounds.minZ, r, g, b, a, minU, minV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.minZ, r, g, b, a, maxU, minV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.minY, (float) bounds.minZ, r, g, b, a, maxU, maxV, brightness, face);
				break;
			case SOUTH:
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.minY, (float) bounds.maxZ, r, g, b, a, maxU, maxV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.minY, (float) bounds.maxZ, r, g, b, a, minU, maxV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ, r, g, b, a, minU, minV, brightness, face);
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.maxY, (float) bounds.maxZ, r, g, b, a, maxU, minV, brightness, face);
				break;
			case WEST:
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.minY, (float) bounds.minZ, r, g, b, a, maxU, maxV, brightness, face);
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.minY, (float) bounds.maxZ, r, g, b, a, minU, maxV, brightness, face);
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.maxY, (float) bounds.maxZ, r, g, b, a, minU, minV, brightness, face);
				putVertex(ps, buffer, (float) bounds.minX, (float) bounds.maxY, (float) bounds.minZ, r, g, b, a, maxU, minV, brightness, face);
				break;
			case EAST:
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.minY, (float) bounds.minZ, r, g, b, a, minU, maxV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.minZ, r, g, b, a, minU, minV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.maxY, (float) bounds.maxZ, r, g, b, a, maxU, minV, brightness, face);
				putVertex(ps, buffer, (float) bounds.maxX, (float) bounds.minY, (float) bounds.maxZ, r, g, b, a, maxU, maxV, brightness, face);
				break;
		}
	}

	public static void putVertex(PoseStack ps, VertexConsumer buffer, float x, float y, float z, int r, int g, int b, int a, float u, float v, int brightness, Direction face)
	{
		PoseStack.Pose pose
			= ps.last();
		Vec3i normal = face.getNormal();

		buffer
			.vertex(pose.pose(), x, y, z)
			.color(r, g, b, a)
			.uv(u, v)
			.overlayCoords(OverlayTexture.NO_OVERLAY)
			.uv2(brightness)
			.normal(pose.normal(), normal.getX(), normal.getY(), normal.getZ())
			.endVertex();
	}
}
