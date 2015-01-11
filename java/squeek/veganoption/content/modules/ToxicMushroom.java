package squeek.veganoption.content.modules;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import cpw.mods.fml.common.registry.GameRegistry;

public class ToxicMushroom implements IContentModule
{
	public static Item falseMorel;
	public static Item falseMorelFermented;

	@Override
	public void create()
	{
		falseMorel = new ItemFood(2, 0.8F, false)
				.setPotionEffect(Potion.poison.id, 5, 0, 1.0F)
				.setPotionEffect(PotionHelper.spiderEyeEffect)
				.setUnlocalizedName(ModInfo.MODID + ".falseMorel")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":false_morel");
		GameRegistry.registerItem(falseMorel, "falseMorel");

		falseMorelFermented = new Item()
				.setPotionEffect(PotionHelper.fermentedSpiderEyeEffect)
				.setUnlocalizedName(ModInfo.MODID + ".falseMorelFermented")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":false_morel_fermented");
		GameRegistry.registerItem(falseMorelFermented, "falseMorelFermented");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.poisonousOreDict, Items.spider_eye);
		OreDictionary.registerOre(ContentHelper.poisonousOreDict, falseMorel);
		OreDictionary.registerOre(ContentHelper.fermentedOreDict, Items.fermented_spider_eye);
		OreDictionary.registerOre(ContentHelper.fermentedOreDict, falseMorelFermented);
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(new ItemStack(Items.spider_eye), ContentHelper.poisonousOreDict);
		Modifiers.recipes.excludeOutput(new ItemStack(Items.fermented_spider_eye));

		Modifiers.recipes.convertInput(new ItemStack(Items.fermented_spider_eye), ContentHelper.fermentedOreDict);

		DropSpecifier dontDropWhenSilkTouching = new DropSpecifier(new ItemStack(falseMorel), 0.15f)
		{
			@Override
			public boolean shouldDrop(EntityPlayer harvester, int fortuneLevel, boolean isSilkTouching)
			{
				return !isSilkTouching && super.shouldDrop(harvester, fortuneLevel, isSilkTouching);
			}
		};
		Modifiers.drops.addDropsToBlock(new BlockSpecifier(Blocks.mycelium), dontDropWhenSilkTouching);

		GameRegistry.addShapelessRecipe(new ItemStack(falseMorelFermented), new ItemStack(falseMorel), new ItemStack(Blocks.brown_mushroom), new ItemStack(Items.sugar));
	}

	@Override
	public void finish()
	{
	}
}
