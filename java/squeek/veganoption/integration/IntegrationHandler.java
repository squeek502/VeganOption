package squeek.veganoption.integration;

import java.util.HashMap;
import java.util.Map;
import cpw.mods.fml.common.Loader;

public class IntegrationHandler
{
	private static Map<String, IntegratorBase> integrators = new HashMap<String, IntegratorBase>();

	public static final String MODID_THERMAL_EXPANSION = "ThermalExpansion";
	public static final String MODID_HARVESTCRAFT = "harvestcraft";
	public static final String MODID_MINEFACTORY_RELOADED = "MineFactoryReloaded";
	public static final String MODID_TINKERS_CONSTRUCT = "TConstruct";
	public static final String MODID_IGUANAS_TINKER_TWEAKS = "IguanaTweaksTConstruct";
	public static final String MODID_WITCHERY = "witchery";
	public static final String MODID_WAILA = "Waila";
	public static final String MODID_VERSION_CHECKER = "VersionChecker";
	public static final String MODID_THAUMCRAFT = "Thaumcraft";
	public static final String MODID_TWILIGHT_FOREST = "TwilightForest";
	public static final String MODID_NATURA = "Natura";
	public static final String MODID_EXTRA_TREES = "ExtraTrees";
	public static final String MODID_BIOMES_O_PLENTY = "BiomesOPlenty";
	public static final String MODID_FOOD_PLUS = "FoodPlus";

	static
	{
		tryIntegration(MODID_THERMAL_EXPANSION, "cofh");
		tryIntegration(MODID_HARVESTCRAFT, "pams", "HarvestCraft");
		tryIntegration(MODID_MINEFACTORY_RELOADED, "mfr");
		tryIntegration(MODID_TINKERS_CONSTRUCT, "tic");
		tryIntegration(MODID_IGUANAS_TINKER_TWEAKS, "tic");
		tryIntegration(MODID_WITCHERY, "witchery", "Witchery");
		tryIntegration(MODID_WAILA, "waila");
		tryIntegration(MODID_VERSION_CHECKER, "versionchecker");
		tryIntegration(MODID_THAUMCRAFT, "thaumcraft");
		tryIntegration(MODID_TWILIGHT_FOREST, "twilightforest");
		tryIntegration(MODID_NATURA, "natura");
		tryIntegration(MODID_EXTRA_TREES, "forestry");
		tryIntegration(MODID_BIOMES_O_PLENTY, "bop");
		tryIntegration(MODID_FOOD_PLUS, "foodplus");
	}

	public static void preInit()
	{
		for (IntegratorBase integrator : integrators.values())
		{
			integrator.preInit();
		}
	}

	public static void init()
	{
		for (IntegratorBase integrator : integrators.values())
		{
			integrator.init();
		}
	}

	public static void postInit()
	{
		for (IntegratorBase integrator : integrators.values())
		{
			integrator.postInit();
		}
	}

	public static boolean tryIntegration(String modID, String packageName)
	{
		return tryIntegration(modID, packageName, modID);
	}

	public static boolean tryIntegration(String modID, String packageName, String className)
	{
		if (Loader.isModLoaded(modID))
		{
			try
			{
				String fullClassName = "squeek.veganoption.integration." + packageName + "." + className;
				Class<?> clazz = Class.forName(fullClassName);
				IntegratorBase integrator = (IntegratorBase) clazz.newInstance();
				integrator.modID = modID;
				integrators.put(modID, integrator);
				return true;
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return false;
	}

	public static boolean integrationExists(String modID)
	{
		return integrators.containsKey(modID);
	}
}
