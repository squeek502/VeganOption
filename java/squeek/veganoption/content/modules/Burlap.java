package squeek.veganoption.content.modules;

import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.RegistryObject;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;

import static squeek.veganoption.ModInfo.MODID_LOWER;
import static squeek.veganoption.VeganOption.REGISTER_ITEMS;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID_LOWER, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Burlap implements IContentModule
{
	public static RegistryObject<Item> burlap;
	public static RegistryObject<Item> burlapHelmet;
	public static RegistryObject<Item> burlapChestplate;
	public static RegistryObject<Item> burlapLeggings;
	public static RegistryObject<Item> burlapBoots;

	@Override
	public void create()
	{
		burlap = REGISTER_ITEMS.register("burlap", () -> new Item(new Item.Properties()));
		burlapBoots = REGISTER_ITEMS.register("burlap_boots", () -> new DyeableArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new Item.Properties()));
		burlapLeggings = REGISTER_ITEMS.register("burlap_leggings", () -> new DyeableArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties()));
		burlapChestplate = REGISTER_ITEMS.register("burlap_chestplate", () -> new DyeableArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
		burlapHelmet = REGISTER_ITEMS.register("burlap_helmet", () -> new DyeableArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties()));
	}

	@Override
	public void datagenItemTags(DataGenProviders.ItemTags provider)
	{
		provider.tagW(ContentHelper.ItemTags.LEATHER).add(burlap.get());
		provider.tagW(ContentHelper.ItemTags.ARMOR_BOOTS).add(burlapBoots.get());
		provider.tagW(ContentHelper.ItemTags.ARMOR_LEGGINGS).add(burlapLeggings.get());
		provider.tagW(ContentHelper.ItemTags.ARMOR_CHESTPLATES).add(burlapChestplate.get());
		provider.tagW(ContentHelper.ItemTags.ARMOR_HELMETS).add(burlapHelmet.get());

		// For recipe replacements
		provider.tagW(ContentHelper.ItemTags.LEATHER_BOOTS)
			.add(burlapBoots.get())
			.add(Items.LEATHER_BOOTS);
		provider.tagW(ContentHelper.ItemTags.LEATHER_LEGGINGS)
			.add(burlapLeggings.get())
			.add(Items.LEATHER_LEGGINGS);
		provider.tagW(ContentHelper.ItemTags.LEATHER_CHESTPLATES)
			.add(burlapChestplate.get())
			.add(Items.LEATHER_CHESTPLATE);
		provider.tagW(ContentHelper.ItemTags.LEATHER_HELMETS)
			.add(burlapHelmet.get())
			.add(Items.LEATHER_HELMET);
	}

	@Override
	public void datagenRecipes(RecipeOutput output, DataGenProviders.Recipes provider)
	{
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, burlap.get())
			.pattern("~~")
			.pattern("~~")
			.define('~', ContentHelper.ItemTags.FIBRES)
			.unlockedBy("unlock_right_away", PlayerTrigger.TriggerInstance.tick()) // todo
			.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, burlapBoots.get())
			.pattern("X X")
			.pattern("X X")
			.define('X', burlap.get())
			.unlockedBy("has_burlap", provider.hasW(burlap.get()))
			.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, burlapLeggings.get())
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.define('X', burlap.get())
			.unlockedBy("has_burlap", provider.hasW(burlap.get()))
			.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, burlapChestplate.get())
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.define('X', burlap.get())
			.unlockedBy("has_burlap", provider.hasW(burlap.get()))
			.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, burlapHelmet.get())
			.pattern("XXX")
			.pattern("X X")
			.define('X', burlap.get())
			.unlockedBy("has_burlap", provider.hasW(burlap.get()))
			.save(output);
	}

	@Override
	public void finish()
	{
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.LEATHER), () -> Ingredient.of(ContentHelper.ItemTags.LEATHER));
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.LEATHER_BOOTS), () -> Ingredient.of(ContentHelper.ItemTags.LEATHER_BOOTS));
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.LEATHER_LEGGINGS), () -> Ingredient.of(ContentHelper.ItemTags.LEATHER_LEGGINGS));
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.LEATHER_CHESTPLATE), () ->  Ingredient.of(ContentHelper.ItemTags.LEATHER_CHESTPLATES));
		Modifiers.recipes.convertInput(() -> Ingredient.of(Items.LEATHER_HELMET), () -> Ingredient.of(ContentHelper.ItemTags.LEATHER_HELMETS));
	}

	@Override
	public void datagenItemModels(ItemModelProvider provider)
	{
		provider.basicItem(burlap.get());
		provider.withExistingParent(burlapBoots.getId().getPath(), provider.mcLoc("leather_boots"));
		provider.withExistingParent(burlapChestplate.getId().getPath(), provider.mcLoc("leather_chestplate"));
		provider.withExistingParent(burlapLeggings.getId().getPath(), provider.mcLoc("leather_leggings"));
		provider.withExistingParent(burlapHelmet.getId().getPath(), provider.mcLoc("leather_helmet"));
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerColorHandlers(RegisterColorHandlersEvent.Item event)
	{
		event.register(new ItemColor() {
			@Override
			public int getColor(ItemStack stack, int tintIndex)
			{
				return tintIndex > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).getColor(stack);
			}
		}, burlapBoots.get(), burlapLeggings.get(), burlapChestplate.get(), burlapHelmet.get());
	}
}
