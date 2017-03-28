package squeek.veganoption.integration.cofh;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import squeek.veganoption.content.modules.Egg;
import squeek.veganoption.content.modules.Ender;
import squeek.veganoption.content.modules.FrozenBubble;
import squeek.veganoption.integration.IntegratorBase;

public class ThermalExpansion extends IntegratorBase
{
	@Override
	public void init()
	{
		super.init();

		addTransposerFill(4000, new ItemStack(FrozenBubble.frozenBubble), new ItemStack(Items.ENDER_PEARL), new FluidStack(Ender.fluidRawEnder, Ender.RAW_ENDER_PER_PEARL), true);
		addTransposerFill(4000, new ItemStack(FrozenBubble.frozenBubble), new ItemStack(Items.ENDER_PEARL), FluidRegistry.getFluidStack("ender", FluidContainerRegistry.BUCKET_VOLUME / 4), false);

		addPulverizerRecipe(1600, new ItemStack(Items.POTATO), new ItemStack(Egg.potatoStarch, 2), null, 100);
	}

	public static void addPulverizerRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance)
	{
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setInteger("secondaryChance", secondaryChance);

		NBTTagCompound inputTag = new NBTTagCompound();
		inputTag = input.writeToNBT(inputTag);
		toSend.setTag("input", inputTag);

		NBTTagCompound primaryOutputTag = new NBTTagCompound();
		primaryOutputTag = primaryOutput.writeToNBT(primaryOutputTag);
		toSend.setTag("primaryOutput", primaryOutputTag);

		if (secondaryOutput != null)
		{
			NBTTagCompound secondaryOutputTag = new NBTTagCompound();
			secondaryOutputTag = secondaryOutput.writeToNBT(secondaryOutputTag);
			toSend.setTag("secondaryOutput", secondaryOutputTag);
		}

		FMLInterModComms.sendMessage(MODID_THERMAL_EXPANSION, "addpulverizerrecipe", toSend);
	}

	public static void addTransposerFill(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean isReversible)
	{
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setInteger("energy", energy);
		toSend.setBoolean("reversible", isReversible);

		NBTTagCompound inputTag = new NBTTagCompound();
		inputTag = input.writeToNBT(inputTag);
		toSend.setTag("input", inputTag);

		NBTTagCompound outputTag = new NBTTagCompound();
		outputTag = output.writeToNBT(outputTag);
		toSend.setTag("output", outputTag);

		NBTTagCompound fluidTag = new NBTTagCompound();
		fluidTag = fluid.writeToNBT(fluidTag);
		toSend.setTag("fluid", fluidTag);

		FMLInterModComms.sendMessage(MODID_THERMAL_EXPANSION, "addtransposerfillrecipe", toSend);
	}
}