package squeek.veganoption.content.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import squeek.veganoption.content.modules.Egg;

import javax.annotation.Nullable;

public class EggRecipe implements IRecipe
{
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		return getItemToEgg(inv) != null;
	}

	@Nullable
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		ItemStack toEgg = getItemToEgg(inv);
		if (toEgg != null)
		{
			ItemStack eggStack = new ItemStack(Egg.plasticEgg, 1);
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagCompound egg = toEgg.writeToNBT(new NBTTagCompound());
			nbt.setTag("ContainedItem", egg);
			eggStack.setTagCompound(nbt);
			return eggStack;
		}
		return null;
	}

	@Nullable
	private ItemStack getItemToEgg(InventoryCrafting inv)
	{
		ItemStack output = null;
		int eggs = 0;
		int nonEggs = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack itemStack = inv.getStackInSlot(i);
			if (itemStack != null) {
				if (itemStack.getItem() == Egg.plasticEgg)
					eggs++;
				else
				{
					nonEggs++;
					if (output == null)
						output = itemStack.copy();
				}
			}
		}

		return eggs == 1 && nonEggs == 1 ? output : null;
	}

	@Override
	public int getRecipeSize()
	{
		return 2;
	}

	@Nullable
	@Override
	public ItemStack getRecipeOutput()
	{
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv)
	{
		return new ItemStack[inv.getSizeInventory()];
	}
}
