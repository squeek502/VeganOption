package squeek.veganoption.integration.cofh;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.content.modules.FrozenBubble;
import squeek.veganoption.integration.IIntegrator;
import cpw.mods.fml.common.event.FMLInterModComms;

public class ThermalExpansion implements IIntegrator
{
	public static final int RESONANT_ENDER_PER_PEARL = FluidContainerRegistry.BUCKET_VOLUME / 4;
	public static final String RESONANT_ENDER_FLUID_NAME = "ender";

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
		addTransposerFill(4000, new ItemStack(FrozenBubble.frozenBubble), new ItemStack(Items.ender_pearl), new FluidStack(Ender.fluidRawEnder, Ender.RAW_ENDER_PER_PEARL), true);
		addTransposerFill(4000, new ItemStack(FrozenBubble.frozenBubble), new ItemStack(Items.ender_pearl), FluidRegistry.getFluidStack(RESONANT_ENDER_FLUID_NAME, RESONANT_ENDER_PER_PEARL), false);
	}

	@Override
	public void postInit()
	{
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
}
