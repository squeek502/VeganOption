package squeek.veganoption.content;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import squeek.veganoption.content.modules.*;
import squeek.veganoption.content.modules.compat.CompatEnderBubble;

/**
 * Modules should only depend on eachother through the OreDictionary. If modules are more intertwined,
 * then only the intertwined parts should be put in compatModules instead
 * 
 * This forces consistent and tested use of the OreDictionary for maximum compatibility with other mods
 * and for potential configurable modularity down the line
 */
public class ContentModuleHandler
{
	private static Map<String, IContentModule> modules = new LinkedHashMap<String, IContentModule>();
	// TODO: resolve dependent modules for compat modules
	private static Map<String, IContentModule> compatModules = new HashMap<String, IContentModule>();
	static
	{
		// CreativeTabProxy must be first.
		modules.put("CreativeTab", new CreativeTabProxy());
		modules.put("Bioplastic", new Bioplastic());
		modules.put("Burlap", new Burlap());
		modules.put("Composting", new Composting());
		modules.put("EggReplacers", new Egg());
		modules.put("Ender", new Ender());
		modules.put("Feather", new Feather());
		modules.put("Fossils", new Fossils());
		modules.put("FrozenBubble", new FrozenBubble());
		modules.put("Gunpowder", new Gunpowder());
		modules.put("Ink", new Ink());
		modules.put("Jute", new Jute());
		modules.put("Kapok", new Kapok());
		modules.put("MobHeads", new MobHeads());
		modules.put("PlantMilk", new PlantMilk());
		modules.put("Resin", new Resin());
		modules.put("Soap", new Soap());
		modules.put("StrawBed", new StrawBed());
		modules.put("ToxicMushroom", new ToxicMushroom());
		modules.put("VegetableOil", new VegetableOil());
		modules.put("ProofOfSuffering", new ProofOfSuffering());
		modules.put("DollsEye", new DollsEye());
		modules.put("Basin", new Basin());
		modules.put("Seitan", new Seitan());

		compatModules.put("EnderBubble", new CompatEnderBubble());
	}

	public static void preInit()
	{
		boolean isClient = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
		for (IContentModule module : modules.values())
		{
			module.create();
			module.oredict();
			if (isClient)
			{
				module.clientSidePre();
			}
		}
		for (IContentModule compatModule : compatModules.values())
		{
			compatModule.create();
			compatModule.oredict();
			if (isClient)
			{
				compatModule.clientSidePre();
			}
		}
	}

	public static void init()
	{
		for (IContentModule module : modules.values())
		{
			module.recipes();
		}
		for (IContentModule compatModule : compatModules.values())
		{
			compatModule.recipes();
		}
	}

	public static void postInit()
	{
		boolean isClient = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
		for (IContentModule module : modules.values())
		{
			if (isClient)
			{
				module.clientSidePost();
			}
			module.finish();
		}
		for (IContentModule compatModule : compatModules.values())
		{
			if (isClient)
			{
				compatModule.clientSidePost();
			}
			compatModule.finish();
		}
	}

}
