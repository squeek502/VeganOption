package squeek.veganoption;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.veganoption.content.ContentModuleHandler;
import squeek.veganoption.content.DataGenProviders;
import squeek.veganoption.content.IContentModule;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.crafting.PistonCraftingHandler;
import squeek.veganoption.content.recipes.RecipeRegistration;
import squeek.veganoption.helpers.CreativeTabHelper;
import squeek.veganoption.integration.IntegrationHandler;
import squeek.veganoption.loot.LootRegistration;
import squeek.veganoption.network.NetworkHandler;

@Mod(ModInfo.MODID_LOWER)
@Mod.EventBusSubscriber(modid = ModInfo.MODID_LOWER)
public class VeganOption
{
	public static final DeferredRegister<Block> REGISTER_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModInfo.MODID_LOWER);
	public static final DeferredRegister<Item> REGISTER_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModInfo.MODID_LOWER);
	public static final DeferredRegister<BlockEntityType<?>> REGISTER_BLOCKENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModInfo.MODID_LOWER);
	public static final DeferredRegister<EntityType<?>> REGISTER_ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModInfo.MODID_LOWER);
	public static final DeferredRegister<FluidType> REGISTER_FLUIDTYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ModInfo.MODID_LOWER);
	public static final DeferredRegister<Fluid> REGISTER_FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ModInfo.MODID_LOWER);
	public static final DeferredRegister<RecipeSerializer<?>> REGISTER_RECIPESERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModInfo.MODID_LOWER);
	public static final DeferredRegister<MenuType<?>> REGISTER_MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ModInfo.MODID_LOWER);
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTER_LOOTMODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ModInfo.MODID_LOWER);
	public static final Logger Log = LogManager.getLogger(ModInfo.MODID_LOWER);

	public VeganOption()
	{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		LootRegistration.init();
		ContentModuleHandler.init();
		NetworkHandler.init();
		IntegrationHandler.init();
		PistonCraftingHandler.init();
		RecipeRegistration.init();

		REGISTER_BLOCKS.register(bus);
		REGISTER_ITEMS.register(bus);
		REGISTER_BLOCKENTITIES.register(bus);
		REGISTER_ENTITIES.register(bus);
		REGISTER_FLUIDTYPES.register(bus);
		REGISTER_FLUIDS.register(bus);
		REGISTER_RECIPESERIALIZERS.register(bus);
		REGISTER_MENUS.register(bus);
		REGISTER_LOOTMODIFIERS.register(bus);
		CreativeTabHelper.createTab(bus);

		bus.addListener(DataGenProviders::generateData);
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event)
	{
		ContentModuleHandler.iterateOverModules(IContentModule::finish);
		IntegrationHandler.finish();
		Modifiers.recipes.replaceRecipes();
	}
}
