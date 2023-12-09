package squeek.veganoption.content;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import squeek.veganoption.ModInfo;
import squeek.veganoption.content.modules.*;
import squeek.veganoption.content.modules.compat.CompatEnderBubble;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Modules should only depend on eachother through the use of tags. If modules are more intertwined,
 * then only the intertwined parts should be put in compatModules instead
 * <br/>
 * This forces consistent and tested use of tags for maximum compatibility with other mods
 * and for potential configurable modularity down the line
 */
@Mod.EventBusSubscriber(modid = ModInfo.MODID_LOWER, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContentModuleHandler
{
	private static Map<String, IContentModule> modules = new LinkedHashMap<String, IContentModule>();
	// TODO: resolve dependent modules for compat modules
	private static Map<String, IContentModule> compatModules = new HashMap<String, IContentModule>();

	static
	{
		modules.put("CreativeTab", new CreativeTabProxy());
		modules.put("Bioplastic", new Bioplastic());
		modules.put("Burlap", new Burlap());
		modules.put("Composting", new Composting());
		modules.put("EggReplacers", new Egg());
		modules.put("Ender", new Ender());
		modules.put("Feather", new Feather());
		modules.put("Fossils", new Fossils());
		modules.put("FrozenBubble", new FrozenBubble());
		modules.put("Gunpowder", new Gunpowder());
		modules.put("Ink", new Ink());
		modules.put("Jute", new Jute());
		modules.put("Kapok", new Kapok());
		modules.put("MobHeads", new MobHeads());
		modules.put("PlantMilk", new PlantMilk());
		modules.put("Resin", new Resin());
		modules.put("Soap", new Soap());
		modules.put("StrawBed", new StrawBed());
		modules.put("ToxicMushroom", new ToxicMushroom());
		modules.put("VegetableOil", new VegetableOil());
		modules.put("ProofOfSuffering", new ProofOfSuffering());
		modules.put("DollsEye", new DollsEye());
		modules.put("Basin", new Basin());
		modules.put("Seitan", new Seitan());

		compatModules.put("EnderBubble", new CompatEnderBubble());
	}

	/**
	 * Iterates over all modules, regular and compat, and performs the passed function on them.
	 * @param consumer passes an {@link IContentModule}
	 */
	public static void iterateOverModules(Consumer<IContentModule> consumer)
	{
		modules.values().forEach(consumer);
		compatModules.values().forEach(consumer);
	}

	public static void init()
	{
		iterateOverModules(IContentModule::create);
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		iterateOverModules(module -> module.registerRenderers(event));
	}
}
