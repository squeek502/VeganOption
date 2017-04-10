package squeek.veganoption.integration.jei.description;

import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import squeek.veganoption.helpers.FluidHelper;
import squeek.veganoption.helpers.LangHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class UsageDescWrapper extends DescriptionWrapper
{
	public UsageDescWrapper(ItemStack itemStack, List<ItemStack> related, List<ItemStack> referenced, List<String> text)
	{
		super(itemStack, related, referenced, text);
	}

	@Override
	protected String getRelatedTitle()
	{
		return LangHelper.translate("nei.byproducts");
	}

	@Override
	boolean isMainSlotInput()
	{
		return true;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients)
	{
		FluidStack fluidStack = FluidHelper.fromItemStack(itemStack);
		if (fluidStack != null)
			ingredients.setInput(FluidStack.class, fluidStack);

		ingredients.setInput(ItemStack.class, itemStack);
	}
}
