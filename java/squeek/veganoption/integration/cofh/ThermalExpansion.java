package squeek.veganoption.integration.cofh;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import squeek.veganoption.content.modules.Egg;
import squeek.veganoption.integration.IntegratorBase;

public class ThermalExpansion extends IntegratorBase
{
	@Override
	public void init()
	{
		super.init();

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
}