package squeek.veganoption.items;

import java.util.List;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import squeek.veganoption.helpers.LangHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBedStraw extends ItemBedGeneric
{

	public ItemBedStraw(BlockBed bed)
	{
		super(bed);
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, @SuppressWarnings("rawtypes") List toolTip, boolean advanced)
	{
		toolTip.add(LangHelper.translateRaw(this.getUnlocalizedName() + ".tooltip"));

		super.addInformation(itemStack, player, toolTip, advanced);
	}

}
