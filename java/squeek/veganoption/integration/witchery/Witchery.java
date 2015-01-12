package squeek.veganoption.integration.witchery;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import squeek.veganoption.content.ContentHelper;
import squeek.veganoption.integration.IntegratorBase;

public class Witchery extends IntegratorBase
{
	public static final int woodAshMetadata = 18;

	@Override
	public void oredict()
	{
		Item ingredient = getItem("ingredient");
		if (ingredient != null)
		{
			ItemStack woodAsh = new ItemStack(ingredient, 1, woodAshMetadata);
			OreDictionary.registerOre(ContentHelper.woodAshOreDict, woodAsh);
		}
	}
}
