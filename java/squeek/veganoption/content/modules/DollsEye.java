package squeek.veganoption.content.modules;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;

public class DollsEye implements IContentModule
{
	public static Item dollsEye;

	@Override
	public void create()
	{
		dollsEye = new ItemFood(2, 0.8F, false)
				.setPotionEffect(new PotionEffect(MobEffects.POISON, 5, 0), 1F)
				.setUnlocalizedName(ModInfo.MODID + ".dollsEye")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "dollsEye");
		GameRegistry.register(dollsEye);
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.poisonousOreDict, dollsEye);
	}

	@Override
	public void recipes()
	{
		PotionHelper.registerPotionTypeConversion(PotionTypes.AWKWARD, new PotionHelper.ItemPredicateInstance(dollsEye), PotionTypes.POISON);

		BlockSpecifier forestGrass = new BlockSpecifier(Blocks.TALLGRASS, 1)
		{
			@Override
			public boolean matches(IBlockAccess world, BlockPos pos, Block block, int meta)
			{
				boolean blockMatches = super.matches(world, pos, block, meta);

				if (!blockMatches || !(world instanceof World))
					return false;

				Biome biome = ((World) world).provider.getBiomeForCoords(pos);

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
