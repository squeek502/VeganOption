package squeek.veganoption.integration;

import net.minecraftforge.fml.common.Loader;

public abstract class IntegrationBase
{
	public static final String MODID_THERMAL_EXPANSION = "ThermalExpansion";
	public static final String MODID_HARVESTCRAFT = "harvestcraft";
	public static final String MODID_MINEFACTORY_RELOADED = "MineFactoryReloaded";
	public static final String MODID_TINKERS_CONSTRUCT = "TConstruct";
	public static final String MODID_IGUANAS_TINKER_TWEAKS = "IguanaTweaksTConstruct";
	public static final String MODID_WAILA = "Waila";
	public static final String MODID_THAUMCRAFT = "Thaumcraft";
	public static final String MODID_BIOMES_O_PLENTY = "BiomesOPlenty";
	public static final String MODID_STILL_HUNGRY = "stillhungry";

	public static boolean integrationExists(String modID)
	{
		return IntegrationHandler.integratorExists(modID);
	}

	public static boolean modExists(String modID)
	{
		return Loader.isModLoaded(modID);
	}
}
