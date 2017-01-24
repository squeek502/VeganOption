package squeek.veganoption.integration.jei;

import mezz.jei.api.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.content.modules.CreativeTabProxy;
import squeek.veganoption.integration.jei.piston.PistonRecipeCategory;
import squeek.veganoption.integration.jei.piston.PistonRecipeHandler;
import squeek.veganoption.integration.jei.piston.PistonRecipeMaker;

@JEIPlugin
public class VOPlugin extends BlankModPlugin
{
	public static IJeiHelpers jeiHelpers;
	public static class VORecipeCategoryUid
	{
		public static final String PISTON = ModInfo.MODID + ".piston";
		public static final String COMPOSTING = ModInfo.MODID + ".composting";
		public static final String DROPS = ModInfo.MODID + ".drops";
		public static final String DESCRIPTION = ModInfo.MODID + ".description";
	}

	@Override
	public void register(IModRegistry registry)
	{
		jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		IItemBlacklist itemBlacklist = jeiHelpers.getItemBlacklist();
		itemBlacklist.addItemToBlacklist(new ItemStack(CreativeTabProxy.proxyItem));


		registry.addRecipeCategories(
			new PistonRecipeCategory(guiHelper)
		);

		registry.addRecipeHandlers(
			new PistonRecipeHandler()
		);

		registry.addRecipeCategoryCraftingItem(new ItemStack(Blocks.PISTON), VORecipeCategoryUid.PISTON);
		registry.addRecipeCategoryCraftingItem(new ItemStack(Composting.composter), VORecipeCategoryUid.COMPOSTING);

		registry.addRecipes(PistonRecipeMaker.getRecipes());
	}
}
