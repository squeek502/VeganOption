package squeek.veganoption.items;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import squeek.veganoption.content.modules.Seitan;
import squeek.veganoption.helpers.FluidHelper;

import java.util.function.Function;

public class ItemWashableWheat extends Item
{
	private final Stage stage;

	public ItemWashableWheat(Stage stage)
	{
		super(stage.getProperties());
		this.stage = stage;
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
	{
		if (tryWash(entity))
			return true;
		return super.onEntityItemUpdate(stack, entity);
	}

	public boolean tryWash(ItemEntity entity)
	{
		if (entity == null || entity.level().isClientSide() || entity.getItem().isEmpty())
			return false;

		FluidStack consumedFluid = FluidHelper.consumeExactFluid(entity.level(), entity.blockPosition(), Fluids.WATER, FluidType.BUCKET_VOLUME);

		if (consumedFluid != null)
		{
			ItemEntity entityItemToWash = entity;
			ItemStack doughToWash = entityItemToWash.getItem();

			if (doughToWash.getCount() > 1)
			{
				doughToWash = doughToWash.split(1);
				entityItemToWash = new ItemEntity(entityItemToWash.level(), entityItemToWash.getBlockX(), entityItemToWash.getBlockY(), entityItemToWash.getBlockZ(), doughToWash);
				entityItemToWash.setPickUpDelay(10);
				entityItemToWash.level().addFreshEntity(entityItemToWash);
			}

			ItemStack washedItemStack = stage.wash(doughToWash);
			entityItemToWash.setItem(washedItemStack);

			return true;
		}
		return false;
	}

	public enum Stage
	{
		FLOUR(new Item.Properties(), (stack) -> new ItemStack(Seitan.wheatDough.get())),
		DOUGH(new Item.Properties(), (stack) -> {
			ItemStack unwashed = new ItemStack(Seitan.seitanUnwashed.get());
			unwashed.setDamageValue(3);
			return unwashed;
		}),
		UNWASHED(new Item.Properties().durability(3), (stack) -> {
			stack.setDamageValue(stack.getDamageValue() - 1);
			if (stack.getDamageValue() == 0)
				return new ItemStack(Seitan.seitanRaw.get());
			return stack;
		});

		private final Item.Properties properties;
		private final Function<ItemStack, ItemStack> wash;

		Stage(Item.Properties properties, Function<ItemStack, ItemStack> wash)
		{
			this.properties = properties;
			this.wash = wash;
		}

		public Item.Properties getProperties()
		{
			return properties;
		}

		public ItemStack wash(ItemStack stack)
		{
			return wash.apply(stack);
		}
	}
}
