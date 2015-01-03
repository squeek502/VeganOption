package squeek.veganoption.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import squeek.veganoption.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPumpkinSeedMilk extends BlockFluidClassic
{
	@SideOnly(Side.CLIENT)
	public IIcon stillIcon;
	@SideOnly(Side.CLIENT)
	public IIcon flowIcon;

	public static class MaterialMilk extends MaterialLiquid
	{
		public MaterialMilk()
		{
			super(MapColor.snowColor);
		}
	}

	public BlockPumpkinSeedMilk(Fluid fluid)
	{
		super(fluid, new MaterialMilk());
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
		stillIcon = register.registerIcon(ModInfo.MODID_LOWER + ":" + "pumpkin_seed_milk_still");
		flowIcon = register.registerIcon(ModInfo.MODID_LOWER + ":" + "pumpkin_seed_milk_flow");
		getFluid().setIcons(stillIcon, flowIcon);
	}
}
