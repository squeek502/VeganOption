package squeek.veganoption.integration.waila;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import squeek.veganoption.integration.IntegratorBase;

public class Waila extends IntegratorBase
{
	@Override
	public void init()
	{
		super.init();
		FMLInterModComms.sendMessage(modID, "register", "squeek.veganoption.integration.waila.WailaRegistrar.register");
	}
}
