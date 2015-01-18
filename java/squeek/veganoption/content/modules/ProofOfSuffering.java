package squeek.veganoption.content.modules;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import cpw.mods.fml.common.registry.GameRegistry;

public class ProofOfSuffering implements IContentModule
{
	public static Item fragmentOfSuffering;
	public static Item proofOfSuffering;

	@Override
	public void create()
	{
		fragmentOfSuffering = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".sufferingFragment")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":fragment_of_suffering");
		GameRegistry.registerItem(fragmentOfSuffering, "sufferingFragment");

		proofOfSuffering = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".sufferingProof")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":proof_of_suffering")
				.setPotionEffect(PotionHelper.ghastTearEffect);
		GameRegistry.registerItem(proofOfSuffering, "sufferingProof");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.tearOreDict, new ItemStack(Items.ghast_tear));
		OreDictionary.registerOre(ContentHelper.tearOreDict, new ItemStack(proofOfSuffering));
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(new ItemStack(Items.ghast_tear), ContentHelper.tearOreDict);

		Modifiers.drops.addDropsToBlock(new BlockSpecifier(Blocks.soul_sand), new DropSpecifier(new ItemStack(fragmentOfSuffering), 0.05f, 1, 3));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(proofOfSuffering), "xxx", "x*x", "xxx", 'x', fragmentOfSuffering, '*', Items.gold_nugget));
	}

	@Override
	public void finish()
	{
	}
}
