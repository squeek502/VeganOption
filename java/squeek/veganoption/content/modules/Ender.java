package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockEncrustedObsidian;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.blocks.BlockRawEnder;
import squeek.veganoption.blocks.renderers.RenderEnderRift;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.items.ItemBucketGeneric;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Ender implements IContentModule
{
	public static Block encrustedObsidian;
	public static Block enderRift;
	public static Fluid fluidRawEnder;
	public static Block rawEnder;
	public static Item bucketRawEnder;
	public static int RAW_ENDER_PER_PEARL = FluidContainerRegistry.BUCKET_VOLUME;

	@Override
	public void create()
	{
		encrustedObsidian = new BlockEncrustedObsidian()
				.setHardness(50.0F)
				.setResistance(2000.0F)
				.setStepSound(Block.soundTypePiston)
				.setBlockName(ModInfo.MODID + ".encrustedObsidian")
				.setCreativeTab(VeganOption.creativeTab)
				.setBlockTextureName(ModInfo.MODID_LOWER + ":encrusted_obsidian");
		GameRegistry.registerBlock(encrustedObsidian, "encrustedObsidian");
		encrustedObsidian.setHarvestLevel("pickaxe", 3);

		enderRift = new BlockEnderRift()
				.setHardness(-1.0F)
				.setResistance(6000000.0F)
				.setBlockName(ModInfo.MODID + ".enderRift");
		GameRegistry.registerBlock(enderRift, "enderRift");
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			createEnderRiftRenderer();
		}
		GameRegistry.registerTileEntity(TileEntityEnderRift.class, ModInfo.MODID + ".enderRift");

		fluidRawEnder = new Fluid(ModInfo.MODID + ".rawEnder")
				.setLuminosity(3)
				.setViscosity(3000)
				.setDensity(4000);
		FluidRegistry.registerFluid(fluidRawEnder);
		rawEnder = new BlockRawEnder(fluidRawEnder)
				.setBlockName(ModInfo.MODID + ".rawEnder");
		fluidRawEnder.setBlock(rawEnder);
		fluidRawEnder.setUnlocalizedName(rawEnder.getUnlocalizedName());
		GameRegistry.registerBlock(rawEnder, "rawEnder");

		bucketRawEnder = new ItemBucketGeneric(rawEnder)
				.setUnlocalizedName(ModInfo.MODID + ".bucketRawEnder")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":raw_ender_bucket")
				.setContainerItem(Items.bucket);
		GameRegistry.registerItem(bucketRawEnder, "bucketRawEnder");
		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluidRawEnder, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(bucketRawEnder), new ItemStack(Items.bucket));

	}

	@SideOnly(Side.CLIENT)
	public void createEnderRiftRenderer()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnderRift.class, new RenderEnderRift());
	}

	@Override
	public void oredict()
	{
	}

	@Override
	public void recipes()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(encrustedObsidian, 2), Items.diamond, Blocks.obsidian, Blocks.obsidian, Items.emerald);
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(bucketRawEnder), new ItemStack(rawEnder));
		RelationshipRegistry.addRelationship(new ItemStack(rawEnder), new ItemStack(bucketRawEnder));
		RelationshipRegistry.addRelationship(new ItemStack(rawEnder), new ItemStack(enderRift));
		RelationshipRegistry.addRelationship(new ItemStack(enderRift), new ItemStack(encrustedObsidian));
	}

}
