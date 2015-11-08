package squeek.veganoption.integration.cofh;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.content.modules.Egg;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.content.modules.FrozenBubble;
import squeek.veganoption.integration.IntegratorBase;
import cpw.mods.fml.common.event.FMLInterModComms;

public class ThermalExpansion extends IntegratorBase
{
	public static final int RESONANT_ENDER_PER_PEARL = FluidContainerRegistry.BUCKET_VOLUME / 4;
	public static final String RESONANT_ENDER_FLUID_NAME = "ender";

	@Override
	public void init()
	{
		super.init();

		addTransposerFill(4000, new ItemStack(FrozenBubble.frozenBubble), new ItemStack(Items.ender_pearl), new FluidStack(Ender.fluidRawEnder, Ender.RAW_ENDER_PER_PEARL), true);
		addTransposerFill(4000, new ItemStack(FrozenBubble.frozenBubble), new ItemStack(Items.ender_pearl), FluidRegistry.getFluidStack(RESONANT_ENDER_FLUID_NAME, RESONANT_ENDER_PER_PEARL), false);

		addPulverizerRecipe(1600, new ItemStack(Items.potato), new ItemStack(Egg.potatoStarch, 2), null, 100);
	}

	/**
	 * taken from CoFHLib (cofh.api.modhelpers.ThermalExpansionHelper)
	 */
	public static void addTransposerFill(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible)
	{
		if (input == null || output == null || fluid == null)
		{
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("output", new NBTTagCompound());
		toSend.setTag("fluid", new NBTTagCompound());

		input.writeToNBT(toSend.getCompoundTag("input"));
		output.writeToNBT(toSend.getCompoundTag("output"));
		toSend.setBoolean("reversible", reversible);
		fluid.writeToNBT(toSend.getCompoundTag("fluid"));

		FMLInterModComms.sendMessage("ThermalExpansion", "TransposerFillRecipe", toSend);
	}

	/**
	 * taken from CoFHLib (cofh.api.modhelpers.ThermalExpansionHelper)
	 */
	public static void addPulverizerRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance)
	{

		if (input == null || primaryOutput == null)
		{
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("primaryOutput", new NBTTagCompound());

		if (secondaryOutput != null)
		{
			toSend.setTag("secondaryOutput", new NBTTagCompound());
		}

		input.writeToNBT(toSend.getCompoundTag("input"));
		primaryOutput.writeToNBT(toSend.getCompoundTag("primaryOutput"));

		if (secondaryOutput != null)
		{
			secondaryOutput.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", secondaryChance);
		}

		FMLInterModComms.sendMessage("ThermalExpansion", "PulverizerRecipe", toSend);
	}
}
