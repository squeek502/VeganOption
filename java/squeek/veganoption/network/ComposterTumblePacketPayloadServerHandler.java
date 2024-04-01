package squeek.veganoption.network;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import squeek.veganoption.gui.ComposterMenu;

public class ComposterTumblePacketPayloadServerHandler
{
	public static void handle(ComposterTumblePacketPayload payload, PlayPayloadContext context)
	{
		context.workHandler().submitAsync(() -> {
			Player player = context.player().orElseThrow();
			if (player.hasContainerOpen() && player.containerMenu instanceof ComposterMenu container && !container.composter.isAerating())
				container.composter.aerate();
		});
	}
}
