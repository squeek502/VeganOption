package squeek.veganoption.fluids;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.ModInfo;

public class GenericFluidTypeRenderProperties implements IClientFluidTypeExtensions
{
	private final ResourceLocation flowing;
	private final ResourceLocation still;
	private final ResourceLocation overlay;
	private final ResourceLocation underwater;

	public GenericFluidTypeRenderProperties(String name)
	{
		this.still = new ResourceLocation(ModInfo.MODID_LOWER, "block/" + name + "_still");
		this.flowing = new ResourceLocation(ModInfo.MODID_LOWER, "block/" + name + "_flow");
		this.overlay = new ResourceLocation(ModInfo.MODID_LOWER, "block/" + name + "_overlay");
		this.underwater = new ResourceLocation(ModInfo.MODID_LOWER, "textures/block/" + name + "_overlay.png");
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
	public @Nullable ResourceLocation getOverlayTexture()
	{
		return overlay;
	}

	@Override
	public @Nullable ResourceLocation getRenderOverlayTexture(Minecraft mc)
	{
		return underwater;
	}
}
