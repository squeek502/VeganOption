package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;
import cpw.mods.fml.common.registry.GameRegistry;

public class DollsEye implements IContentModule
{
	public static Item dollsEye;

	@Override
	public void create()
	{
		dollsEye = new ItemFood(2, 0.8F, false)
				.setPotionEffect(Potion.poison.id, 5, 0, 1.0F)
				.setPotionEffect(PotionHelper.spiderEyeEffect)
				.setUnlocalizedName(ModInfo.MODID + ".dollsEye")
				.setCreativeTab(VeganOption.creativeTab)
				.setTextureName(ModInfo.MODID_LOWER + ":dolls_eye");
		GameRegistry.registerItem(dollsEye, "dollsEye");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.poisonousOreDict, dollsEye);
	}

	@Override
	public void recipes()
	{
		BlockSpecifier forestGrass = new BlockSpecifier(Blocks.tallgrass, 1)
		{
			@Override
			public boolean matches(IBlockAccess world, int x, int y, int z, Block block, int meta)
			{
				boolean blockMatches = super.matches(world, x, y, z, block, meta);

				if (!blockMatches)
					return false;

				BiomeGenBase biome = world.getBiomeGenForCoords(x, z);

				boolean isForest = BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.FOREST);

				if (!isForest)
					return false;

				boolean isTemperate = !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.CONIFEROUS) && !BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.JUNGLE);

				if (!isTemperate)
					return false;

				return true;
			}
		};
		Modifiers.drops.addDropsToBlock(forestGrass, new DropSpecifier(new ItemStack(dollsEye), 0.01f, 1, 2));
	}

	@Override
	public void finish()
	{
	}
}
