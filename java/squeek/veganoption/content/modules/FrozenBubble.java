package squeek.veganoption.content.modules;

import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.registry.RelationshipRegistry;
import squeek.veganoption.entities.EntityBubble;
import squeek.veganoption.entities.EntityBubbleDispenserBehavior;
import squeek.veganoption.items.ItemFrozenBubble;
import squeek.veganoption.items.ItemSoapSolution;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FrozenBubble implements IContentModule
{
	public static Item soapSolution;
	public static Item frozenBubble;

	public static final ItemStack pufferFish = new ItemStack(Items.fish, 1, ItemFishFood.FishType.PUFFERFISH.ordinal());

	@Override
	public void create()
	{
		soapSolution = new ItemSoapSolution()
				.setUnlocalizedName(ModInfo.MODID + ".soapSolution")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":soap_solution")
				.setContainerItem(Items.glass_bottle);
		GameRegistry.registerItem(soapSolution, "soapSolution");

		frozenBubble = new ItemFrozenBubble()
				.setUnlocalizedName(ModInfo.MODID + ".frozenBubble")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":frozen_bubble");
		GameRegistry.registerItem(frozenBubble, "frozenBubble");

		EntityRegistry.registerModEntity(EntityBubble.class, "bubble", EntityRegistry.findGlobalUniqueEntityId(), ModInfo.MODID, 80, 1, true);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			createBubbleRenderer();
		}
	}

	@SideOnly(Side.CLIENT)
	public void createBubbleRenderer()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityBubble.class, new RenderSnowball(frozenBubble));
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
				new ItemStack(Items.water_bucket),
				new ItemStack(Items.sugar),
				new ItemStack(Items.glass_bottle)));
		Modifiers.crafting.addInputsToKeepForOutput(new ItemStack(soapSolution), ContentHelper.soapOreDict);

		GameRegistry.addShapedRecipe(new ItemStack(frozenBubble), "iii", "isi", "iii", 'i', Blocks.ice, 's', soapSolution);
		GameRegistry.addShapelessRecipe(new ItemStack(frozenBubble), Blocks.packed_ice, soapSolution);

		BlockDispenser.dispenseBehaviorRegistry.putObject(soapSolution, new EntityBubbleDispenserBehavior());
	}

	@Override
	public void finish()
	{
		RelationshipRegistry.addRelationship(new ItemStack(frozenBubble), new ItemStack(soapSolution));
		RelationshipRegistry.addRelationship(new ItemStack(frozenBubble, 1, 1), new ItemStack(frozenBubble));
		RelationshipRegistry.addRelationship(new ItemStack(Items.ender_pearl), new ItemStack(frozenBubble, 1, 1));
	}

}
