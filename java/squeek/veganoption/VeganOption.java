package squeek.veganoption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.network.NetworkHandler;
import squeek.veganoption.registry.Content;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION, dependencies = "after:" + IntegrationHandler.MODID_HARVESTCRAFT)
public class VeganOption
{
	public static final Logger Log = LogManager.getLogger(ModInfo.MODID);

	@Instance(ModInfo.MODID)
	public static VeganOption instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Content.create();
		IntegrationHandler.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		GuiHelper.init();
		NetworkHandler.init();
		IntegrationHandler.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		Content.finish();
		IntegrationHandler.postInit();
	}
}
