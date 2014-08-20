package squeek.veganoption.modifications;

import squeek.veganoption.helpers.RandomHelper;
import squeek.veganoption.registry.Content;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

public class JungleLeavesModifier
{
	public JungleLeavesModifier()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onGetHarvestDrops(BlockEvent.HarvestDropsEvent event)
	{
		if (event.block == Blocks.leaves && (event.blockMetadata & 3) == 3)
		{
			int dropNothingOffset = 10;
			int num = RandomHelper.getRandomIntFromRange(0, 2 + dropNothingOffset) - dropNothingOffset;

			for (int i = 0; i < num; i++)
				event.drops.add(new ItemStack(Content.kapokTuft));
		}
	}
}
