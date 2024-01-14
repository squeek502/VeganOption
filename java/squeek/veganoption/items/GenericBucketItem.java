package squeek.veganoption.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class GenericBucketItem extends BucketItem
{
	public GenericBucketItem(Supplier<? extends Fluid> supplier, Properties builder)
	{
		super(supplier, builder);
		DispenserBlock.registerBehavior(this, new GenericBucketItem.DispenserBehavior());
	}

	private static class DispenserBehavior extends DefaultDispenseItemBehavior
	{
		@Override
		public ItemStack execute(BlockSource blockSource, ItemStack itemStack)
		{
			BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
			if (((DispensibleContainerItem) itemStack.getItem()).emptyContents(null, blockSource.level(), blockpos, null, itemStack))
				return new ItemStack(Items.BUCKET);
			else
				return super.execute(blockSource, itemStack);
		}
	}
}
