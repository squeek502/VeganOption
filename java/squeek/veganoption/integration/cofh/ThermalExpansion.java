package squeek.veganoption.integration.cofh;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.integration.IIntegrator;
import squeek.veganoption.registry.Content;
import cpw.mods.fml.common.event.FMLInterModComms;

public class ThermalExpansion implements IIntegrator
{
	@Override
	public void preInit()
	{
	}

	@Override
	public void init()
	{
		addTransposerFill(4000, new ItemStack(Content.frozenBubble), new ItemStack(Items.ender_pearl), new FluidStack(Content.fluidRawEnder, FluidContainerRegistry.BUCKET_VOLUME), true);
		addTransposerFill(4000, new ItemStack(Content.frozenBubble), new ItemStack(Items.ender_pearl), FluidRegistry.getFluidStack("ender", FluidContainerRegistry.BUCKET_VOLUME / 4), false);
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
