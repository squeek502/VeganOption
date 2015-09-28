package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockBasin;
import squeek.veganoption.blocks.renderers.RenderBasin;
import squeek.veganoption.blocks.tiles.TileEntityBasin;
import squeek.veganoption.content.IContentModule;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Basin implements IContentModule
{
	public static Block basin;

	@Override
	public void create()
	{
		basin = new BlockBasin(Material.iron)
				.setHardness(2.5F)
				.setStepSound(Block.soundTypeMetal)
				.setBlockName(ModInfo.MODID + ".basin")
				.setCreativeTab(VeganOption.creativeTab)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":basin");
		GameRegistry.registerBlock(basin, "basin");
		GameRegistry.registerTileEntity(TileEntityBasin.class, ModInfo.MODID + ".basin");
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			createBasinRenderer();
		}
	}

	@SideOnly(Side.CLIENT)
	public void createBasinRenderer()
	{
		RenderBasin basinRenderer = new RenderBasin();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBasin.class, basinRenderer);
		MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(basin), basinRenderer);
	}

	@Override
	public void oredict()
	{
	}

	@Override
	public void recipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(basin), " g ", "gcg", " g ", 'g', "blockGlassColorless", 'c', Items.cauldron));
	}

	@Override
	public void finish()
	{
	}

}
