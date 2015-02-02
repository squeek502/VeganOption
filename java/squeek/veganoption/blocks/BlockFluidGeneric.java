package squeek.veganoption.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import squeek.veganoption.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFluidGeneric extends BlockFluidClassic
{
	public String iconName;
	@SideOnly(Side.CLIENT)
	public IIcon stillIcon;
	@SideOnly(Side.CLIENT)
	public IIcon flowIcon;

	public BlockFluidGeneric(Fluid fluid, Material material, String iconName)
	{
		super(fluid, material);
		this.iconName = iconName;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		return (side == 0 || side == 1) ? stillIcon : flowIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		stillIcon = register.registerIcon(ModInfo.MODID_LOWER + ":" + iconName + "_still");
		flowIcon = register.registerIcon(ModInfo.MODID_LOWER + ":" + iconName + "_flow");
		getFluid().setIcons(stillIcon, flowIcon);
	}
}
