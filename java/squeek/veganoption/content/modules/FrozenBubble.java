package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.blocks.BlockFluidGeneric;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.entities.EntityBubble;
import squeek.veganoption.items.ItemFrozenBubble;
import squeek.veganoption.items.ItemSoapSolution;

public class FrozenBubble implements IContentModule
{
	public static Item soapSolution;
	public static Item frozenBubble;
	public static Fluid fluidSoapSolution;
	public static Block blockFluidSoapSolution;

	public static final ItemStack pufferFish = new ItemStack(Items.FISH, 1, ItemFishFood.FishType.PUFFERFISH.ordinal());

	@Override
	public void create()
	{
		fluidSoapSolution = new Fluid(ModInfo.MODID + ".fluidSoapSolution", new ResourceLocation(ModInfo.MODID_LOWER, "blocks/soap_solution_still"), new ResourceLocation(ModInfo.MODID_LOWER, "blocks/soap_solution_flow"));
		FluidRegistry.registerFluid(fluidSoapSolution);
		blockFluidSoapSolution = new BlockFluidGeneric(fluidSoapSolution, Material.WATER, "soap_solution")
				.setUnlocalizedName(ModInfo.MODID + ".fluidSoapSolution")
				.setRegistryName(ModInfo.MODID_LOWER, "fluidSoapSolution");
		fluidSoapSolution.setBlock(blockFluidSoapSolution);
		fluidSoapSolution.setUnlocalizedName(blockFluidSoapSolution.getUnlocalizedName());
		GameRegistry.register(blockFluidSoapSolution);

		soapSolution = new ItemSoapSolution()
				.setUnlocalizedName(ModInfo.MODID + ".soapSolution")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "soapSolution")
				.setContainerItem(Items.GLASS_BOTTLE);
		GameRegistry.register(soapSolution);

		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluidSoapSolution, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(soapSolution), new ItemStack(soapSolution.getContainerItem()));

		frozenBubble = new ItemFrozenBubble()
				.setUnlocalizedName(ModInfo.MODID + ".frozenBubble")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "frozenBubble");
		GameRegistry.register(frozenBubble);

		EntityRegistry.registerModEntity(EntityBubble.class, "bubble", ContentHelper.ENTITYID_BUBBLE, ModInfo.MODID, 80, 1, true);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			createBubbleRenderer();
		}
	}

	@SideOnly(Side.CLIENT)
	public void createBubbleRenderer()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityBubble.class, new IRenderFactory<EntityBubble>() {
			@Override
			public Render<? super EntityBubble> createRenderFor(RenderManager manager) {
				return new RenderSnowball<EntityBubble>(manager, frozenBubble, Minecraft.getMinecraft().getRenderItem());
			}
		});
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.pufferFishOreDict, pufferFish.copy());
		OreDictionary.registerOre(ContentHelper.pufferFishOreDict, frozenBubble);
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(pufferFish, ContentHelper.pufferFishOreDict);

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(soapSolution),
				ContentHelper.soapOreDict,
				new ItemStack(Items.WATER_BUCKET),
				new ItemStack(Items.SUGAR),
				new ItemStack(Items.GLASS_BOTTLE)));
		Modifiers.crafting.addInputsToKeepForOutput(new ItemStack(soapSolution), ContentHelper.soapOreDict);

		GameRegistry.addShapedRecipe(new ItemStack(frozenBubble), "iii", "isi", "iii", 'i', Blocks.ICE, 's', soapSolution);
		GameRegistry.addShapelessRecipe(new ItemStack(frozenBubble), Blocks.PACKED_ICE, soapSolution);

		PistonCraftingRegistry.register(new PistonCraftingRecipe(fluidSoapSolution, FluidRegistry.WATER, ContentHelper.soapOreDict, new ItemStack(Items.SUGAR)));
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(frozenBubble), new ItemStack(soapSolution));
		RelationshipRegistry.addRelationship(new ItemStack(frozenBubble, 1, 1), new ItemStack(frozenBubble));
		RelationshipRegistry.addRelationship(new ItemStack(Items.ENDER_PEARL), new ItemStack(frozenBubble, 1, 1));
		RelationshipRegistry.addRelationship(new ItemStack(soapSolution), new ItemStack(blockFluidSoapSolution));
		RelationshipRegistry.addRelationship(new ItemStack(blockFluidSoapSolution), new ItemStack(soapSolution));
	}

}
