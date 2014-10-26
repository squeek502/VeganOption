package squeek.veganoption.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import squeek.veganoption.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLyeWater extends BlockFluidClassic
{
	@SideOnly(Side.CLIENT)
	public IIcon stillIcon;
	@SideOnly(Side.CLIENT)
	public IIcon flowIcon;

	public static final DamageSource lyeDamage = new DamageSource(ModInfo.MODID + ".lyeWater");

	public BlockLyeWater(Fluid fluid)
	{
		super(fluid, Material.water);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity collidedEntity)
	{
		collidedEntity.attackEntityFrom(lyeDamage, 0.25f);
		super.onEntityCollidedWithBlock(world, x, y, z, collidedEntity);
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
		stillIcon = register.registerIcon(ModInfo.MODID_LOWER + ":" + "lye_water_still");
		flowIcon = register.registerIcon(ModInfo.MODID_LOWER + ":" + "lye_water_flow");
		getFluid().setIcons(stillIcon, flowIcon);
	}
}
