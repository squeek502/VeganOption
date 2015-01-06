package squeek.veganoption.integration.mfr;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import squeek.veganoption.content.modules.Composting;
import squeek.veganoption.integration.IIntegrator;
import squeek.veganoption.integration.IntegrationHandler;
import cpw.mods.fml.common.event.FMLInterModComms;

public class MineFactoryReloaded implements IIntegrator
{
	@Override
	public void overrideContent()
	{
	}

	@Override
	public void preInit()
	{
	}

	@Override
	public void init()
	{
		registerFertilizer(new ItemStack(Composting.fertilizer), FertilizerType.GrowPlant);
	}

	@Override
	public void postInit()
	{
	}

	// copied from powercrystals.minefactoryreloaded.api.FertilizerType
	public static enum FertilizerType
	{
		/**
		* The fertilizer will fertilize nothing.
		*/
		None,
		/**
		* The fertilizer will fertilize grass.
		*/
		Grass,
		/**
		* The fertilizer will grow a plant.
		*/
		GrowPlant,
		/**
		* The fertilizer will grow magical crops.
		*/
		GrowMagicalCrop,
	}

	/**
	 * registerFertilizer_Standard | An NBTTag with the fert (Item, String identifier), meta (Integer), and
	 * type (Integer, index into FertilizerType.values()) attributes set.
	 */
	public void registerFertilizer(ItemStack itemStack, FertilizerType type)
	{
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setString("fert", Item.itemRegistry.getNameForObject(itemStack.getItem()));
		toSend.setInteger("meta", itemStack.getItemDamage());
		toSend.setInteger("type", type.ordinal());

		FMLInterModComms.sendMessage(IntegrationHandler.MODID_MINEFACTORY_RELOADED, "registerFertilizer_Standard", toSend);
	}
}
