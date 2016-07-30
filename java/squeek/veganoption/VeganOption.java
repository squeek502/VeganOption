package squeek.veganoption;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.veganoption.content.ContentModuleHandler;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.crafting.PistonCraftingHandler;
import squeek.veganoption.helpers.CreativeTabHelper;
import squeek.veganoption.helpers.FluidContainerHelper;
import squeek.veganoption.helpers.GuiHelper;
import squeek.veganoption.helpers.TooltipHelper;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.network.NetworkHandler;

// dependency of after:* seems necessary to ensure that the RecipeModifier doesn't miss any recipes
@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION, dependencies = "required-after:Forge@10.13.1.1225;after:*")
public class VeganOption
{
	public static final Logger Log = LogManager.getLogger(ModInfo.MODID);

	@Mod.Instance(ModInfo.MODID)
	public static VeganOption instance;

	// creative tab
	public static CreativeTabs creativeTab = CreativeTabHelper.createTab(ModInfo.MODID, ModInfo.MODID_LOWER + ":creative_tab");

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ContentModuleHandler.preInit();
		IntegrationHandler.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		FluidContainerHelper.init();
		GuiHelper.init();
		TooltipHelper.init();
		NetworkHandler.init();
		ContentModuleHandler.init();
		IntegrationHandler.init();
		PistonCraftingHandler.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		ContentModuleHandler.postInit();
		IntegrationHandler.postInit();
		Modifiers.recipes.replaceRecipes();
	}
}
