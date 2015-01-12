package squeek.veganoption.integration.waila;

import squeek.veganoption.integration.IntegratorBase;
import cpw.mods.fml.common.event.FMLInterModComms;

public class Waila extends IntegratorBase
{
	@Override
	public void init()
	{
		super.init();
		FMLInterModComms.sendMessage(modID, "register", "squeek.veganoption.integration.waila.WailaRegistrar.register");
	}
}
