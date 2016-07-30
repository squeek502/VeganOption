package squeek.veganoption.content.modules;

import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modifiers.DropsModifier.BlockSpecifier;
import squeek.veganoption.content.modifiers.DropsModifier.DropSpecifier;

public class Resin implements IContentModule
{
	public static Item resin;
	public static Item rosin;

	@Override
	public void create()
	{
		resin = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".resin")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "resin");
		GameRegistry.register(resin);


		rosin = new Item()
				.setUnlocalizedName(ModInfo.MODID + ".rosin")
				.setCreativeTab(VeganOption.creativeTab)
				.setRegistryName(ModInfo.MODID_LOWER, "rosin");
		GameRegistry.registerItem(rosin, "rosin");
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.slimeballOreDict, new ItemStack(Items.SLIME_BALL));
		OreDictionary.registerOre(ContentHelper.slimeballOreDict, new ItemStack(resin));
		OreDictionary.registerOre(ContentHelper.resinOreDict, new ItemStack(resin));
		OreDictionary.registerOre(ContentHelper.resinMaterialOreDict, new ItemStack(resin));
		OreDictionary.registerOre(ContentHelper.rosinOreDict, new ItemStack(rosin));
		OreDictionary.registerOre(ContentHelper.rosinMaterialOreDict, new ItemStack(rosin));
	}

	@Override
	public void recipes()
	{
		Modifiers.recipes.convertInput(new ItemStack(Items.SLIME_BALL), ContentHelper.slimeballOreDict);

		BlockSpecifier spruceLogSpecifier = new BlockSpecifier(Blocks.LOG, 1)
		{
			@Override
			public boolean metaMatches(int meta)
			{
				return this.meta == BlockPlanks.EnumType.byMetadata(meta).getMetadata();
			}
		};
		Modifiers.drops.addDropsToBlock(spruceLogSpecifier, new DropSpecifier(new ItemStack(resin), 0.1f));

		GameRegistry.addSmelting(resin, new ItemStack(rosin), 0.2f);
	}

	@Override
	public void finish()
	{
	}

}
