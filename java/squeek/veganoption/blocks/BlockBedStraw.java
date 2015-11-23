package squeek.veganoption.blocks;

import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import squeek.veganoption.ModInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

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
		boolean wokenByWakeAllPlayers = event.setSpawn && !event.updateWorld;
		if (!wokenByWakeAllPlayers)
			return;

		ChunkCoordinates chunkcoordinates = event.entityPlayer.playerLocation;

		if (chunkcoordinates == null)
			return;

		Block block = event.entityPlayer.worldObj.getBlock(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ);

		if (block != this)
			return;

		// The player's sleeping bool must be set to false before calling 
		// attackEntityFrom; otherwise, an infinite loop would be created due to 
		// the wakeUpPlayer call in attackEntityFrom
		try
		{
			playerSleepingField.setBoolean(event.entityPlayer, false);
			event.entityPlayer.attackEntityFrom(itchyDamageSource, ITCH_DAMAGE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
