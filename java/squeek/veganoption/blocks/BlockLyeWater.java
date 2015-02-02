package squeek.veganoption.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import squeek.veganoption.ModInfo;

public class BlockLyeWater extends BlockFluidGeneric
{
	public static final DamageSource lyeDamage = new DamageSource(ModInfo.MODID + ".lyeWater");
	public static Material lyeMaterial = new MaterialLiquid(MapColor.waterColor);

	public BlockLyeWater(Fluid fluid)
	{
		super(fluid, lyeMaterial, "lye_water");
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity collidedEntity)
	{
		if (collidedEntity instanceof EntityLivingBase)
		{
			collidedEntity.attackEntityFrom(lyeDamage, 0.25f);
		}
		super.onEntityCollidedWithBlock(world, x, y, z, collidedEntity);
	}
}
