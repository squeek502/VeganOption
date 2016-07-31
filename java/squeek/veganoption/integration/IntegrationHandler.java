package squeek.veganoption.integration;

import net.minecraftforge.fml.common.Loader;

import java.util.HashMap;
import java.util.Map;

public class IntegrationHandler extends IntegrationBase
{
	private static Map<String, IntegratorBase> integrators = new HashMap<String, IntegratorBase>();

	static
	{
		tryIntegration(MODID_HARVESTCRAFT, "pams", "HarvestCraft");
		tryIntegration(MODID_TINKERS_CONSTRUCT, "tic");
		tryIntegration(MODID_IGUANAS_TINKER_TWEAKS, "tic");
		tryIntegration(MODID_WAILA, "waila");
		tryIntegration(MODID_THAUMCRAFT, "thaumcraft");
		tryIntegration(MODID_BIOMES_O_PLENTY, "bop");
		tryIntegration(MODID_STILL_HUNGRY, "stillhungry", "StillHungry");
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
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return false;
	}

	public static boolean integratorExists(String modID)
	{
		return integrators.containsKey(modID);
	}
}
