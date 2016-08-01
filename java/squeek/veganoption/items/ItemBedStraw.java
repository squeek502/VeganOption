package squeek.veganoption.items;

import java.util.List;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.veganoption.helpers.LangHelper;

public class ItemBedStraw extends ItemBedGeneric
{

	public ItemBedStraw(BlockBed bed)
	{
		super(bed);
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip, boolean advanced)
	{
		toolTip.add(LangHelper.translateRaw(this.getUnlocalizedName() + ".tooltip"));

		super.addInformation(itemStack, player, toolTip, advanced);
	}

}
