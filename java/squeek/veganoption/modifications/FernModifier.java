package squeek.veganoption.modifications;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import squeek.veganoption.helpers.RandomHelper;
import squeek.veganoption.registry.Content;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FernModifier
{
	public FernModifier()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onGetHarvestDrops(BlockEvent.HarvestDropsEvent event)
	{
		if (event.block == Blocks.double_plant && BlockDoublePlant.func_149890_d(event.blockMetadata) == 3)
		{
			int num = RandomHelper.getRandomIntFromRange(1, 3);
			for (int i=0; i<num; i++)
				event.drops.add(new ItemStack(Content.juteStalk));
		}
	}
}
