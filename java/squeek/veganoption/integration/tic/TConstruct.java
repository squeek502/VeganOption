package squeek.veganoption.integration.tic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.modules.Bioplastic;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.helpers.LangHelper;
import squeek.veganoption.integration.IIntegrator;
import squeek.veganoption.integration.IntegrationHandler;
import cpw.mods.fml.common.event.FMLInterModComms;

public class TConstruct implements IIntegrator
{
	public static final String modID = IntegrationHandler.MODID_TINKERS_CONSTRUCT;

	public static final int MATID_PLASTIC = 1000; // what MFR uses
	public static final String MATNAME_PLASTIC = "Plastic"; // what MFR uses
	/**
	 * VO specific, necessary to make sure that VO's MaterialSet (shard/rod combo)
	 * doesn't overwrite the MaterialSet that uses {@link MATNAME_PLASTIC} added
	 * by the addPartBuilderMaterial IMC (in either VO or MFR)
	 */
	public static final String KEY_PLASTICROD_MATERIALSET = "BioplasticRodSet";

	public static final String ITEMNAME_TOOLROD = modID + ":toolRod";
	public static final String ITEMNAME_JERKY = modID + ":jerky";
	public static final String ITEMNAME_GOLDENHEAD = modID + ":goldHead";
	public static final String ITEMNAME_DIAMONDAPPLE = modID + ":diamondApple";
	public static final String ITEMNAME_STRANGEFOOD = modID + ":strangeFood";

	@Override
	public void preInit()
	{
	}

	@Override
	public void init()
	{
		registerPlasticToolMaterial();

		CompostRegistry.blacklist(new FoodSpecifier()
		{
			@Override
			public boolean matches(ItemStack itemStack)
			{
				// meat and diamonds are bad for composting
				String itemName = Item.itemRegistry.getNameForObject(itemStack.getItem());
				return itemName.equals(ITEMNAME_JERKY) || itemName.equals(ITEMNAME_GOLDENHEAD) || itemName.equals(ITEMNAME_DIAMONDAPPLE) || itemName.equals(ITEMNAME_STRANGEFOOD);
			}
		});
	}

	@Override
	public void postInit()
	{
	}

	public void registerPlasticToolMaterial()
	{
		NBTTagCompound tag = new NBTTagCompound();

		if (!IntegrationHandler.modExists(IntegrationHandler.MODID_MINEFACTORY_RELOADED))
		{
			// material values mirrored from from MFR's plastic
			tag.setInteger("Id", MATID_PLASTIC);
			tag.setString("Name", MATNAME_PLASTIC);
			tag.setString("localizationString", LangHelper.prependModId("tic.material.plastic"));
			tag.setInteger("Durability", 1500);
			tag.setInteger("MiningSpeed", 600);
			tag.setInteger("HarvestLevel", 1);
			tag.setInteger("Attack", -1);
			tag.setFloat("HandleModifier", 0.1f);
			tag.setFloat("Bow_ProjectileSpeed", 4.2f);
			tag.setInteger("Bow_DrawSpeed", 20);
			tag.setFloat("Projectile_Mass", 0.25f);
			tag.setFloat("Projectile_Fragility", 0.5f);
			tag.setString("Style", EnumChatFormatting.GRAY.toString());
			tag.setInteger("Color", 0xFFADADAD);
			FMLInterModComms.sendMessage(modID, "addMaterial", tag);

			tag = new NBTTagCompound();
			tag.setInteger("MaterialId", MATID_PLASTIC);
			tag.setTag("Item", new ItemStack(Bioplastic.bioplastic).writeToNBT(new NBTTagCompound()));
			tag.setInteger("Value", 1);
			FMLInterModComms.sendMessage(modID, "addPartBuilderMaterial", tag);

			// without MFR, there is no need to register a shard, so just add the rod
			// note: this doesn't really do much afaik
			registerShardAndRod(KEY_PLASTICROD_MATERIALSET, null, new ItemStack(Bioplastic.plasticRod), MATID_PLASTIC);
		}
		else
		{
			// mfr registers plastic sheets as shards, so do the same
			registerShardAndRod(KEY_PLASTICROD_MATERIALSET, new ItemStack(Bioplastic.bioplastic), new ItemStack(Bioplastic.plasticRod), MATID_PLASTIC);
		}
	}

	// avoid the oredict lookup every getRealHandle call
	public static List<ItemStack> plasticRodItems = null;

	/**
	 * Allow plasticRod to be used as a tool rod directly by replacing it at build-time with an actual ToolRod
	 * 
	 * Called from squeek.veganoption.asm.Hooks, which is called from tconstruct.tools.TinkerToolEvents.buildTool
	 * I really don't want to have the build depend on TiC, so I just hook in through ASM.
	 * Simply including ToolBuildEvent.java and listening for the event didn't seem to work.
	 * 
	 * This seems to be the 'correct' way to allow usage of non-ToolRod items as ToolRods
	 */
	public static ItemStack getRealHandle(ItemStack itemStack)
	{
		if (plasticRodItems == null)
			plasticRodItems = OreDictionary.getOres(ContentHelper.plasticRodOreDict);

		for (ItemStack item : plasticRodItems)
		{
			if (OreDictionary.itemMatches(item, itemStack, false))
			{
				Item toolRodItem = (Item) Item.itemRegistry.getObject(ITEMNAME_TOOLROD);
				if (toolRodItem != null)
				{
					return new ItemStack(toolRodItem, 1, MATID_PLASTIC);
				}
			}
		}
		return itemStack;
	}

	public static final String patternBuilderClassName = "tconstruct.library.crafting.PatternBuilder";
	public static Object PatternBuilder = null;
	public static Method registerMaterialSet = null;
	static
	{
		try
		{
			Class<?> patternBuilderClass = Class.forName(patternBuilderClassName);
			Field instanceField = patternBuilderClass.getDeclaredField("instance");
			PatternBuilder = instanceField.get(null);
			registerMaterialSet = patternBuilderClass.getDeclaredMethod("registerMaterialSet", String.class, ItemStack.class, ItemStack.class, int.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void registerShardAndRod(String key, ItemStack shard, ItemStack rod, int matID)
	{
		try
		{
			registerMaterialSet.invoke(PatternBuilder, key, shard, rod, matID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
