package squeek.veganoption.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import squeek.veganoption.entities.EntityPlasticEgg;
import squeek.veganoption.helpers.LangHelper;

import java.util.List;

public class ItemPlasticEgg extends ItemThrowableGeneric
{
	public ItemPlasticEgg()
	{
		super(EntityPlasticEgg.class);
	}

	@Override
	public EntityThrowable getNewThrownEntity(ItemStack thrownItem, World world, EntityLivingBase thrower)
	{
		if (thrownItem.hasTagCompound() && thrownItem.getTagCompound().hasKey("ContainedItem"))
			return new EntityPlasticEgg(getContainedItem(thrownItem), world, thrower);

		return super.getNewThrownEntity(thrownItem, world, thrower);
	}

	@Override
	public EntityThrowable getNewThrownEntity(ItemStack thrownItem, World world, double x, double y, double z)
	{
		if (thrownItem.hasTagCompound() && thrownItem.getTagCompound().hasKey("ContainedItem"))
			return new EntityPlasticEgg(getContainedItem(thrownItem), world, x, y, z);

		return super.getNewThrownEntity(thrownItem, world, x, y, z);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ContainedItem"))
		{
			ItemStack containedItem = getContainedItem(stack);
			tooltip.add(LangHelper.translateRaw(getUnlocalizedName() + ".tooltip", LangHelper.translateRaw(containedItem.getItem().getUnlocalizedName(containedItem) + ".name")));
		}
	}

	private ItemStack getContainedItem(ItemStack self)
	{
		return ItemStack.loadItemStackFromNBT(self.getTagCompound().getCompoundTag("ContainedItem"));
	}
}
