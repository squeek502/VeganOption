package squeek.veganoption.content.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import squeek.veganoption.content.modules.Egg;

import javax.annotation.Nonnull;

public class EggRecipe extends CustomRecipe
{
	public EggRecipe(CraftingBookCategory category)
	{
		super(category);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn)
	{
		return !getItemToEgg(inv).isEmpty();
	}

	@Override
	public ItemStack getResultItem(RegistryAccess access)
	{
		return new ItemStack(Egg.plasticEgg.get());
	}

	@Override
	public ItemStack assemble(CraftingContainer container, RegistryAccess access)
	{
		ItemStack toEgg = getItemToEgg(container);
		if (!toEgg.isEmpty())
		{
			ItemStack eggStack = new ItemStack(Egg.plasticEgg.get());
			CompoundTag nbt = new CompoundTag();
			CompoundTag egg = toEgg.serializeNBT();
			nbt.put("ContainedItem", egg);
			eggStack.setTag(nbt);
			return eggStack;
		}
		return ItemStack.EMPTY;
	}

	@Nonnull
	private ItemStack getItemToEgg(Container container)
	{
		ItemStack output = null;
		int eggs = 0;
		int nonEggs = 0;
		for (int i = 0; i < container.getContainerSize(); i++)
		{
			ItemStack itemStack = container.getItem(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() == Egg.plasticEgg.get())
					eggs++;
				else
				{
					nonEggs++;
					if (output == null)
						output = itemStack.copy();
				}
			}
		}

		return eggs == 1 && nonEggs == 1 ? output : ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return width > 1 || height > 1;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return Egg.eggRecipeSerializer.get();
	}
}
