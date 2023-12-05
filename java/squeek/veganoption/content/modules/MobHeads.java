package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.registry.CompostRegistry;

import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

// currently depends on potatoStarch
public class MobHeads implements IContentModule
{
	public static RegistryObject<Item> papierMache;
	public static RegistryObject<Item> mobHeadBlank;

	@Override
	public void create()
	{
		papierMache = REGISTER_ITEMS.register("papier_mache", () -> new Item(new Item.Properties()));
		mobHeadBlank = REGISTER_ITEMS.register("blank_mob_head", () -> new Item(new Item.Properties()));
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, papierMache.get(), 8)
			.requires(Items.WATER_BUCKET)
			.requires(ContentHelper.ItemTags.STARCH)
			.requires(Items.PAPER)
			.requires(Items.PAPER)
			.requires(Items.PAPER)
			.requires(Items.PAPER)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick())
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, mobHeadBlank.get())
			.pattern("///")
			.pattern("/m/")
			.pattern("///")
			.define('/', papierMache.get())
			.define('m', Items.MELON)
			.unlockedBy("has_papier_mache", provider.hasW(papierMache.get()))
			.save(output);

		mobHeadRecipe(ContentHelper.ItemTags.DYES_LIGHT_GRAY, Items.SKELETON_SKULL, output, provider);
		mobHeadRecipe(ContentHelper.ItemTags.DYES_BLACK, Items.WITHER_SKELETON_SKULL, output, provider);
		mobHeadRecipe(ContentHelper.ItemTags.DYES_BROWN, Items.PLAYER_HEAD, output, provider);
		mobHeadRecipe(ContentHelper.ItemTags.DYES_GREEN, Items.ZOMBIE_HEAD, output, provider);
		mobHeadRecipe(ContentHelper.ItemTags.DYES_LIME, Items.CREEPER_HEAD, output, provider);
		// todo: piglin head
	}

	private void mobHeadRecipe(TagKey<Item> dye, Item mobHead, RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, mobHead)
			.pattern("ddd")
			.pattern("dhd")
			.pattern("ddd")
			.define('d', dye)
			.define('h', mobHeadBlank.get())
			.unlockedBy("has_papier_mache", provider.hasW(papierMache.get()))
			.save(output);
	}

	@Override
	public void finish()
	{
		CompostRegistry.addBrown(papierMache.get());
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(papierMache.get());
		provider.basicItem(mobHeadBlank.get());
	}
}
