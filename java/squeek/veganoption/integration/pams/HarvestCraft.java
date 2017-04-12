package squeek.veganoption.integration.pams;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import squeek.veganoption.ModInfo;
import squeek.veganoption.VeganOption;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modules.PlantMilk;
import squeek.veganoption.content.recipes.InputItemStack;
import squeek.veganoption.content.recipes.PistonCraftingRecipe;
import squeek.veganoption.content.registry.CompostRegistry;
import squeek.veganoption.content.registry.CompostRegistry.FoodSpecifier;
import squeek.veganoption.content.registry.PistonCraftingRegistry;
import squeek.veganoption.helpers.TooltipHelper;
import squeek.veganoption.integration.IntegratorBase;

import java.util.ArrayList;
import java.util.List;

public class HarvestCraft extends IntegratorBase
{
	public static final String rootPackage = "com.pam.harvestcraft.";
	public static Item bbqSauce;
	public static Item bbqTofu;

	@Override
	public void create()
	{
		bbqSauce = new Item()
			.setUnlocalizedName(ModInfo.MODID + ".bbqSauce")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "bbqSauce");
		GameRegistry.register(bbqSauce);

		bbqTofu = new ItemFood(14, 1.0F, false)
			.setUnlocalizedName(ModInfo.MODID + ".bbqTofu")
			.setCreativeTab(VeganOption.creativeTab)
			.setRegistryName(ModInfo.MODID_LOWER, "bbqTofu");
		GameRegistry.register(bbqTofu);
		TooltipHelper.registerItem(bbqTofu);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void clientSidePre()
	{
		ContentHelper.registerTypicalItemModel(bbqSauce);
		ContentHelper.registerTypicalItemModel(bbqTofu);
	}

	@Override
	public void oredict()
	{
		OreDictionary.registerOre(ContentHelper.oilPresserOreDict, new ItemStack(getItem("juicerItem")));
		OreDictionary.registerOre(ContentHelper.eggBakingOreDict, new ItemStack(getItem("firmtofuItem")));
		OreDictionary.registerOre(ContentHelper.bbqSauceOreDict, new ItemStack(bbqSauce));
	}

	@Override
	public void recipes()
	{
		// exclude fresh milk so that fresh milk -> fresh milk doesn't get inadvertently created
		Item freshMilkItem = getItem("freshmilkItem");
		if (freshMilkItem != null)
			Modifiers.recipes.excludeOutput(new ItemStack(freshMilkItem));
		// add plant milk -> fresh milk specifically
		GameRegistry.addShapelessRecipe(new ItemStack(freshMilkItem, 4), PlantMilk.bucketPlantMilk.copy());

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(bbqSauce), "toolSaucepan", "foodKetchup", "foodVinegar", Items.SUGAR, "foodMustard", "listAllwater", "foodSalt", "foodBlackpepper"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(bbqTofu), "toolBakeware", "foodFirmtofu", ContentHelper.bbqSauceOreDict));

		if (getItem("doughItem") != null)
			PistonCraftingRegistry.register(new PistonCraftingRecipe(getItem("doughItem"), FluidRegistry.WATER, "foodFlour", "foodSalt"));
		if (getItem("batterItem") != null)
			PistonCraftingRegistry.register(new PistonCraftingRecipe(getItem("batterItem"), "foodFlour", ContentHelper.eggBakingOreDict));
		if (getItem("mashedpotatoesItem") != null)
			PistonCraftingRegistry.register(new PistonCraftingRecipe(getItem("mashedpotatoesItem"), "foodButteredpotato", "foodSalt"));

		// exclude non-baked goods from egg replacer conversion
		final String[] foodNamesToExclude = new String[]
			{
				"boiledeggItem",
				"scrambledeggItem",
				"friedriceItem",
				"stuffedeggplantItem",
				"asparagusquicheItem",
				"custardItem",
				"omeletItem",
				"marshmellowsItem",
				"mayoItem",
				"coconutshrimpItem",
				"eggnogItem",
				"zucchinifriesItem",
				"friedeggItem"
			};
		for (String foodNameToExclude : foodNamesToExclude)
		{
			Item item = getItem(foodNameToExclude);

			if (item != null)
				Modifiers.recipes.excludeOutput(new ItemStack(item));
		}
	}

	@Override
	public void init()
	{
		super.init();
		CompostRegistry.blacklist(new FoodSpecifier()
		{
			@Override
			public boolean matches(ItemStack itemStack)
			{
				// this is a bad way to do this, but pam's foods do not make much distinction between foods with
				// and without meat, so effectively blacklist all but raw plant foods as a shortcut to weeding out meat.
				// (raw plants use cropX oredict while prepared foods use foodX oredict)
				int[] oreIDs = OreDictionary.getOreIDs(itemStack);
				for (int oreID : oreIDs)
				{
					String oreName = OreDictionary.getOreName(oreID);
					if (oreName.startsWith("food"))
						return true;
				}
				return false;
			}
		});
	}

	@Override
	public void postInit()
	{
		super.postInit();

		// do this late because I think HarvestCraft registers its recipes late
		for (ItemStack juice : OreDictionary.getOres("listAlljuice"))
		{
			if (juice.getItem() == null || juice.getItem().hasContainerItem(juice))
				continue;

			List<InputItemStack> juiceInputs = new ArrayList<InputItemStack>();

			@SuppressWarnings("unchecked")
			List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
			for (IRecipe recipe : recipes)
			{
				if (recipe.getRecipeOutput() != null && OreDictionary.itemMatches(juice, recipe.getRecipeOutput(), false))
				{
					@SuppressWarnings("rawtypes")
					List ingredients = null;

					if (recipe instanceof ShapelessRecipes)
						ingredients = ((ShapelessRecipes) recipe).recipeItems;
					else if (recipe instanceof ShapelessOreRecipe)
						ingredients = ((ShapelessOreRecipe) recipe).getInput();
					else
						continue;

					for (Object ingredient : ingredients)
					{
						if (ingredient instanceof ItemStack && ((ItemStack) ingredient).getItem() == getItem("juicerItem"))
							continue;
						else if (ingredient instanceof ArrayList && ingredient == OreDictionary.getOres("toolJuicer"))
							continue;

						juiceInputs.add(new InputItemStack(ingredient));
					}
				}
			}

			PistonCraftingRegistry.register(new PistonCraftingRecipe(juice.copy(), juiceInputs.toArray()));
		}
	}
}
