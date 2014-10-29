package squeek.veganoption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.network.NetworkHandler;
import squeek.veganoption.registry.Content;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION, dependencies = "after:*")
public class VeganOption
{
	public static final Logger Log = LogManager.getLogger(ModInfo.MODID);

	@Instance(ModInfo.MODID)
	public static VeganOption instance;

	@EventHandler
	public void preInit(FMLInitializationEvent event)
	{
		Content.create();

		FMLInterModComms.sendRuntimeMessage(ModInfo.MODID, "VersionChecker", "addVersionCheck", "http://www.ryanliptak.com/minecraft/versionchecker/squeek502/VeganOption");
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		GuiHelper.init();
		NetworkHandler.init();

		FMLInterModComms.sendMessage("Waila", "register", "squeek.veganoption.integration.Waila.callbackRegister");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		Content.finish();
	}
}
