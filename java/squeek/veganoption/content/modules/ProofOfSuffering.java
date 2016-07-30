package squeek.veganoption.content.modules;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;

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
				.setRegistryName(ModInfo.MODID_LOWER, "sufferingFragment");
		GameRegistry.register(fragmentOfSuffering);

		proofOfSuffering = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".sufferingProof")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "sufferingProof");
		GameRegistry.register(proofOfSuffering);
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.tearOreDict, new ItemStack(Items.GHAST_TEAR));
		OreDictionary.registerOre(ContentHelper.tearOreDict, new ItemStack(proofOfSuffering));
	}

	@Override
	public void recipes()
	{
		PotionHelper.registerPotionTypeConversion(PotionTypes.AWKWARD, new PotionHelper.ItemPredicateInstance(proofOfSuffering), PotionTypes.REGENERATION);

		Modifiers.recipes.convertInput(new ItemStack(Items.GHAST_TEAR), ContentHelper.tearOreDict);

		Modifiers.drops.addDropsToBlock(new BlockSpecifier(Blocks.SOUL_SAND), new DropSpecifier(new ItemStack(fragmentOfSuffering), 0.05f, 1, 3));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(proofOfSuffering), "xxx", "x*x", "xxx", 'x', fragmentOfSuffering, '*', Items.GOLD_NUGGET));
	}

	@Override
	public void finish()
	{
	}
}
