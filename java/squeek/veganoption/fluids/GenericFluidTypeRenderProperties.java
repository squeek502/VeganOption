package squeek.veganoption.fluids;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Vector3f;
import squeek.veganoption.ModInfo;
import squeek.veganoption.helpers.ColorHelper;

public class GenericFluidTypeRenderProperties implements IClientFluidTypeExtensions
{
	private final ResourceLocation flowing;
	private final ResourceLocation still;
	private final ResourceLocation overlay;
	private final ResourceLocation underwater;
	private final Vector3f fogColor;

	public GenericFluidTypeRenderProperties(String name, int fogColor)
	{
		this.still = new ResourceLocation(ModInfo.MODID_LOWER, "block/" + name + "_still");
		this.flowing = new ResourceLocation(ModInfo.MODID_LOWER, "block/" + name + "_flow");
		this.overlay = new ResourceLocation(ModInfo.MODID_LOWER, "block/" + name + "_overlay");
		this.underwater = new ResourceLocation(ModInfo.MODID_LOWER, "textures/block/" + name + "_overlay.png");
		this.fogColor = new Vector3f(ColorHelper.toNormalizedRGB(fogColor));
	}

	@Override
	public ResourceLocation getStillTexture()
	{
		return still;
	}

	@Override
	public ResourceLocation getFlowingTexture()
	{
		return flowing;
	}

	@Override
	public ResourceLocation getOverlayTexture()
	{
		return overlay;
	}

	@Override
	public ResourceLocation getRenderOverlayTexture(Minecraft mc)
	{
		return underwater;
	}

	@Override
	public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
	{
		return fogColor;
	}
}
