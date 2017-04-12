package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockEncrustedObsidian;
import squeek.veganoption.blocks.BlockEnderRift;
import squeek.veganoption.blocks.BlockRawEnder;
import squeek.veganoption.blocks.renderers.RenderEnderRift;
import squeek.veganoption.blocks.tiles.TileEntityEnderRift;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.registry.RelationshipRegistry;

public class Ender implements IContentModule
{
	public static Block encrustedObsidian;
	public static Block enderRift;
	public static Fluid fluidRawEnder;
	public static Block rawEnder;
	public static ItemStack bucketRawEnder;
	public static int RAW_ENDER_PER_PEARL = Fluid.BUCKET_VOLUME;

	@Override
	public void create()
	{
		encrustedObsidian = new BlockEncrustedObsidian()
			.setHardness(50.0F)
			.setResistance(2000.0F)
			.setUnlocalizedName(ModInfo.MODID + ".encrustedObsidian")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "encrustedObsidian");
		GameRegistry.register(encrustedObsidian);
		GameRegistry.register(new ItemBlock(encrustedObsidian).setRegistryName(encrustedObsidian.getRegistryName()));
		encrustedObsidian.setHarvestLevel("pickaxe", 3);

		enderRift = new BlockEnderRift()
			.setHardness(-1.0F)
			.setResistance(6000000.0F)
			.setUnlocalizedName(ModInfo.MODID + ".enderRift")
			.setRegistryName(ModInfo.MODID_LOWER, "enderRift");
		GameRegistry.register(enderRift);
		GameRegistry.register(new ItemBlock(enderRift).setRegistryName(enderRift.getRegistryName()));
		GameRegistry.registerTileEntity(TileEntityEnderRift.class, ModInfo.MODID + ".enderRift");

		fluidRawEnder = new Fluid("raw_ender", new ResourceLocation(ModInfo.MODID_LOWER, "blocks/raw_ender_still"), new ResourceLocation(ModInfo.MODID_LOWER, "blocks/raw_ender_flow"))
			.setLuminosity(3)
			.setViscosity(3000)
			.setDensity(4000);
		FluidRegistry.registerFluid(fluidRawEnder);
		rawEnder = new BlockRawEnder(fluidRawEnder)
			.setUnlocalizedName(ModInfo.MODID + ".rawEnder")
			.setRegistryName(ModInfo.MODID_LOWER, "rawEnder");
		fluidRawEnder.setBlock(rawEnder);
		fluidRawEnder.setUnlocalizedName(rawEnder.getUnlocalizedName());
		GameRegistry.register(rawEnder);
		GameRegistry.register(new ItemBlock(rawEnder).setRegistryName(rawEnder.getRegistryName()));

		FluidRegistry.addBucketForFluid(fluidRawEnder);

		UniversalBucket bucket = ForgeModContainer.getInstance().universalBucket;
		bucketRawEnder = new ItemStack(bucket);
		bucket.fill(bucketRawEnder, new FluidStack(fluidRawEnder, Fluid.BUCKET_VOLUME), true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePost()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnderRift.class, new RenderEnderRift());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePre()
	{
		ContentHelper.registerTypicalItemModel(Item.getItemFromBlock(encrustedObsidian));
		ContentHelper.registerFluidMapperAndMeshDef(rawEnder, "raw_ender");
		ContentHelper.registerTypicalItemModel(Item.getItemFromBlock(enderRift));
	}

	@Override
	public void oredict()
	{
	}

	@Override
	public void recipes()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(encrustedObsidian, 2), Items.DIAMOND, Blocks.OBSIDIAN, Blocks.OBSIDIAN, Items.EMERALD);
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(bucketRawEnder.copy(), new ItemStack(rawEnder));
		RelationshipRegistry.addRelationship(new ItemStack(rawEnder), bucketRawEnder.copy());
		RelationshipRegistry.addRelationship(new ItemStack(rawEnder), new ItemStack(enderRift));
		RelationshipRegistry.addRelationship(new ItemStack(enderRift), new ItemStack(encrustedObsidian));
	}

}
