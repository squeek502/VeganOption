package squeek.veganoption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.veganoption.registry.Content;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION, dependencies = "after:*")
public class VeganOption
{
	public static final Logger Log = LogManager.getLogger(ModInfo.MODID);

	@EventHandler
	public void preInit(FMLInitializationEvent event)
	{
		Content.create();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		FMLInterModComms.sendMessage("Waila", "register", "squeek.veganoption.integration.Waila.callbackRegister");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		FMLInterModComms.sendRuntimeMessage(ModInfo.MODID, "VersionChecker", "addVersionCheck", "http://www.ryanliptak.com/minecraft/versionchecker/squeek502/VeganOption");

		Content.finish();
	}
}
