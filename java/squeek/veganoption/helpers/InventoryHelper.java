package squeek.veganoption.helpers;

import java.lang.reflect.Method;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class InventoryHelper
{
	public static final Method hopperInsertIntoInventory = ReflectionHelper.findMethod(TileEntityHopper.class, null, new String[]{"func_145899_c", "c"}, IInventory.class, ItemStack.class, int.class, int.class);

	public static IInventory getInventoryAtLocation(World world, int x, int y, int z)
	{
		return TileEntityHopper.func_145893_b(world, x, y, z);
	}

	public static ItemStack insertStackIntoInventory(ItemStack itemStack, IInventory inventory)
	{
		return TileEntityHopper.func_145889_a(inventory, itemStack, ForgeDirection.DOWN.ordinal());
	}

	public static void dropAllInventoryItemsInWorld(World world, int x, int y, int z, IInventory inventory)
	{
		if (inventory == null)
			return;

		for (int slotNum = 0; slotNum < inventory.getSizeInventory(); ++slotNum)
		{
			ItemStack itemstack = inventory.getStackInSlot(slotNum);

			if (itemstack != null)
			{
				float xOffset = RandomHelper.random.nextFloat() * 0.8F + 0.1F;
				float yOffset = RandomHelper.random.nextFloat() * 0.8F + 0.1F;
				float zOffset = RandomHelper.random.nextFloat() * 0.8F + 0.1F;

				while (itemstack.stackSize > 0)
				{
					int stackSizeToDrop = RandomHelper.random.nextInt(21) + 10;

					if (stackSizeToDrop > itemstack.stackSize)
					{
						stackSizeToDrop = itemstack.stackSize;
					}

					itemstack.stackSize -= stackSizeToDrop;
					EntityItem entityitem = new EntityItem(world, (double) ((float) x + xOffset), (double) ((float) y + yOffset), (double) ((float) z + zOffset), new ItemStack(itemstack.getItem(), stackSizeToDrop, itemstack.getItemDamage()));
					float velocityScale = 0.05F;
					entityitem.motionX = (double) ((float) RandomHelper.random.nextGaussian() * velocityScale);
					entityitem.motionY = (double) ((float) RandomHelper.random.nextGaussian() * velocityScale + 0.2F);
					entityitem.motionZ = (double) ((float) RandomHelper.random.nextGaussian() * velocityScale);

					if (itemstack.hasTagCompound())
					{
						entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
					}

					world.spawnEntityInWorld(entityitem);
				}
			}
		}
	}

	public static float getPercentInventoryFilled(IInventory inventory)
	{
		if (inventory == null || inventory.getSizeInventory() == 0)
			return 0;

		float filledPercent = 0.0F;

		for (int slotNum = 0; slotNum < inventory.getSizeInventory(); ++slotNum)
		{
			ItemStack itemstack = inventory.getStackInSlot(slotNum);

			if (itemstack != null)
			{
				filledPercent += (float) itemstack.stackSize / (float) Math.min(inventory.getInventoryStackLimit(), itemstack.getMaxStackSize());
			}
		}

		filledPercent /= (float) inventory.getSizeInventory();
		return filledPercent;
	}
}
