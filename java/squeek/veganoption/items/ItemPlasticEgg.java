package squeek.veganoption.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import squeek.veganoption.entities.EntityPlasticEgg;
import squeek.veganoption.helpers.LangHelper;

import java.util.List;

public class ItemPlasticEgg extends ItemThrowableGeneric
{
	public ItemPlasticEgg()
	{
		super();
	}

	@Override
	public ThrowableItemProjectile getNewProjectile(ItemStack thrownItem, Level level, Player thrower)
	{
		if (hasItem(thrownItem))
			return new EntityPlasticEgg(getContainedItem(thrownItem), thrower, level);

		return new EntityPlasticEgg(null, thrower, level);
	}

	@Override
	public ThrowableItemProjectile getNewProjectile(ItemStack thrownItem, Level level, double x, double y, double z)
	{
		if (hasItem(thrownItem))
			return new EntityPlasticEgg(getContainedItem(thrownItem), x, y, z, level);

		return new EntityPlasticEgg(null, x, y, z, level);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag advanced)
	{
		if (hasItem(stack))
		{
			Item containedItem = getContainedItem(stack);
			tooltip.add(Component.literal(LangHelper.translateRaw(getDescriptionId() + ".tooltip", LangHelper.translateRaw(containedItem.getDescriptionId() + ".name"))));
		}
	}

	private Item getContainedItem(ItemStack self)
	{
		return ItemStack.of(self.getTag().getCompound("ContainedItem")).getItem();
	}

	private boolean hasItem(ItemStack self)
	{
		return self.hasTag() && self.getTag().contains("ContainedItem");
	}
}
