package squeek.veganoption;

import net.minecraft.creativetab.CreativeTabs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.veganoption.content.ContentModuleHandler;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.helpers.CreativeTabHelper;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.network.NetworkHandler;
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

	// creative tab
	public static CreativeTabs creativeTab = CreativeTabHelper.createTab(ModInfo.MODID, ModInfo.MODID_LOWER + ":creative_tab");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ContentModuleHandler.preInit();
		IntegrationHandler.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		GuiHelper.init();
		NetworkHandler.init();
		ContentModuleHandler.init();
		IntegrationHandler.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		ContentModuleHandler.postInit();
		IntegrationHandler.postInit();
		Modifiers.recipes.replaceRecipes();
	}
}
