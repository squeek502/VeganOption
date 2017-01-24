package squeek.veganoption.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import squeek.veganoption.ModInfo;

import java.lang.reflect.Field;

public class BlockBedStraw extends BlockBedGeneric
{
	public static final DamageSource itchyDamageSource = new DamageSource(ModInfo.MODID + ".itchyBed");
	public static final int ITCH_DAMAGE = 1;
	public static final Field playerSleepingField = ReflectionHelper.findField(EntityPlayer.class, "sleeping", "field_71083_bS", "bA");

	public BlockBedStraw()
	{
		super();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onWakeUp(PlayerWakeUpEvent event)
	{
		// this flag combination should only be set when the sleep was successful
		// and the server is waking all sleeping players
		boolean wokenByWakeAllPlayers = event.shouldSetSpawn() && !event.updateWorld();
		if (!wokenByWakeAllPlayers)
			return;

		BlockPos pos = event.getEntityPlayer().playerLocation;

		if (pos == null)
			return;

		Block block = event.getEntityPlayer().worldObj.getBlockState(pos).getBlock();

		if (block != this)
			return;

		// The player's sleeping bool must be set to false before calling 
		// attackEntityFrom; otherwise, an infinite loop would be created due to 
		// the wakeUpPlayer call in attackEntityFrom
		try
		{
			playerSleepingField.setBoolean(event.getEntityPlayer(), false);
			event.getEntityPlayer().attackEntityFrom(itchyDamageSource, ITCH_DAMAGE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
