package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.EggModifier;
import squeek.veganoption.content.recipes.EggRecipe;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.entities.EntityPlasticEgg;
import squeek.veganoption.items.ItemFoodContainered;
import squeek.veganoption.items.ItemPlasticEgg;

public class Egg implements IContentModule
{
	public static Item potatoStarch;
	public static Item appleSauce;
	public static Item plasticEgg;

	public static final ItemStack potatoCrusher = new ItemStack(Blocks.PISTON);

	@Override
	public void create()
	{
		appleSauce = new ItemFoodContainered(3, 1f, false)
			.setUnlocalizedName(ModInfo.MODID + ".appleSauce")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "apple_sauce")
			.setContainerItem(Items.BOWL);
		GameRegistry.register(appleSauce);

		potatoStarch = new Item()
			.setUnlocalizedName(ModInfo.MODID + ".potatoStarch")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "potato_starch");
		GameRegistry.register(potatoStarch);

		plasticEgg = new ItemPlasticEgg()
			.setUnlocalizedName(ModInfo.MODID + ".plasticEgg")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "plastic_egg");
		GameRegistry.register(plasticEgg);

		EntityRegistry.registerModEntity(EntityPlasticEgg.class, "plasticEgg", ContentHelper.ENTITYID_PLASTIC_EGG, ModInfo.MODID, 80, 1, true);

		RecipeSorter.register(ModInfo.MODID_LOWER + ":egg_recipe", EggRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePost()
	{
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePre()
	{
		ContentHelper.registerTypicalItemModel(appleSauce);
		ContentHelper.registerTypicalItemModel(potatoStarch);
		ContentHelper.registerTypicalItemModel(plasticEgg);

		RenderingRegistry.registerEntityRenderingHandler(EntityPlasticEgg.class, new IRenderFactory<EntityPlasticEgg>()
		{
			@Override
			public Render<? super EntityPlasticEgg> createRenderFor(RenderManager manager)
			{
				return new RenderSnowball<EntityPlasticEgg>(manager, plasticEgg, Minecraft.getMinecraft().getRenderItem());
			}
		});
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.eggObjectOreDict, new ItemStack(Items.EGG));
		OreDictionary.registerOre(ContentHelper.eggObjectOreDict, new ItemStack(plasticEgg));

		OreDictionary.registerOre(ContentHelper.eggBakingOreDict, new ItemStack(Items.EGG));
		OreDictionary.registerOre(ContentHelper.eggBakingOreDict, new ItemStack(appleSauce));
		OreDictionary.registerOre(ContentHelper.eggBakingOreDict, new ItemStack(potatoStarch));

		OreDictionary.registerOre(ContentHelper.starchOreDict, potatoStarch);
	}

	@Override
	public void recipes()
	{
		// there's no good way to handle this that I can think of... so this is just a mess of remapping
		// and conversion to attempt to separate egg oredict entries into more specific categories
		ContentHelper.remapOre(ContentHelper.eggForgeOreDict, ContentHelper.eggBakingOreDict);
		ContentHelper.remapOre(ContentHelper.eggForgeOreDict, ContentHelper.eggObjectOreDict);

		Modifiers.recipes.convertOreDict(ContentHelper.eggFoodOreDict, ContentHelper.eggBakingOreDict);
		Modifiers.recipes.convertInputForFoodOutput(new ItemStack(Items.EGG), ContentHelper.eggBakingOreDict);
		Modifiers.recipes.convertInputForNonFoodOutput(new ItemStack(Items.EGG), ContentHelper.eggObjectOreDict);
		Modifiers.recipes.convertOreDictForFoodOutput(ContentHelper.eggForgeOreDict, ContentHelper.eggBakingOreDict);
		Modifiers.recipes.convertOreDictForNonFoodOutput(ContentHelper.eggForgeOreDict, ContentHelper.eggObjectOreDict);

		GameRegistry.addShapelessRecipe(new ItemStack(appleSauce), new ItemStack(Items.APPLE), new ItemStack(Items.BOWL));

		GameRegistry.addShapelessRecipe(new ItemStack(potatoStarch), potatoCrusher, new ItemStack(Items.POTATO));
		Modifiers.crafting.addInputsToKeepForOutput(new ItemStack(potatoStarch), potatoCrusher);

		PistonCraftingRegistry.register(new PistonCraftingRecipe(new ItemStack(potatoStarch), Items.POTATO));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(plasticEgg), " o ", "o o", " o ", 'o', ContentHelper.plasticOreDict));
		GameRegistry.addRecipe(new EggRecipe());
		Modifiers.eggs.addItem(new ItemStack(Items.GUNPOWDER), new EggModifier()
		{
			@Override
			public void onImpactGeneric(RayTraceResult rayTraceResult, EntityPlasticEgg eggEntity)
			{
				eggEntity.worldObj.createExplosion(eggEntity.getThrower(), eggEntity.posX, eggEntity.posY, eggEntity.posZ, 2F, true);
			}
		});
		EggModifier growModifier = new EggModifier()
		{
			@Override
			public void onImpactGeneric(RayTraceResult rayTraceResult, EntityPlasticEgg eggEntity)
			{
				if (eggEntity.worldObj.isRemote)
					return;
				BlockPos posToGrow = rayTraceResult.getBlockPos();
				IBlockState stateToGrow = eggEntity.worldObj.getBlockState(posToGrow);
				Block blockToGrow = stateToGrow.getBlock();
				if (blockToGrow instanceof IGrowable)
				{
					IGrowable growable = (IGrowable) blockToGrow;
					if (growable.canUseBonemeal(eggEntity.worldObj, eggEntity.worldObj.rand, posToGrow, stateToGrow) &&
						growable.canGrow(eggEntity.worldObj, posToGrow, stateToGrow, eggEntity.worldObj.isRemote))
						growable.grow(eggEntity.worldObj, eggEntity.worldObj.rand, posToGrow, stateToGrow);
				}
			}
		};
		Modifiers.eggs.addItem(new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), growModifier);
		Modifiers.eggs.addItem(new ItemStack(Composting.fertilizer), growModifier);
	}

	@Override
	public void finish()
	{
	}
}
