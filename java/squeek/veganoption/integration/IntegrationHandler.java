package squeek.veganoption.integration;

import java.lang.reflect.Constructor;
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
	public static final String MODID_WITCHERY = "Witchery";
	public static final String MODID_WAILA = "Waila";
	public static final String MODID_VERSION_CHECKER = "VersionChecker";

	static
	{
		tryIntegration(MODID_THERMAL_EXPANSION, "cofh");
		tryIntegration(MODID_HARVESTCRAFT, "pams", "HarvestCraft");
		tryIntegration(MODID_MINEFACTORY_RELOADED, "mfr");
		tryIntegration(MODID_TINKERS_CONSTRUCT, "tic");
		tryIntegration(MODID_WITCHERY, "witchery");
		tryIntegration(MODID_WAILA, "waila");
		tryIntegration(MODID_VERSION_CHECKER, "versionchecker");
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

	public static boolean tryIntegration(String modId, String packageName)
	{
		return tryIntegration(modId, packageName, modId);
	}

	public static boolean tryIntegration(String modId, String packageName, String className)
	{
		if (Loader.isModLoaded(modId))
		{
			try
			{
				String fullClassName = "squeek.veganoption.integration." + packageName + "." + className;
				Class<?> clazz = IntegrationHandler.class.getClassLoader().loadClass(fullClassName);
				Constructor<?> constructor = clazz.getConstructor(String.class);
				IntegratorBase integrator = (IntegratorBase) constructor.newInstance(modId);
				integrators.put(modId, integrator);
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean integrationExists(String modId)
	{
		return integrators.containsKey(modId);
	}
}
