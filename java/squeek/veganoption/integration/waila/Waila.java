package squeek.veganoption.integration.waila;

import cpw.mods.fml.common.event.FMLInterModComms;
import squeek.veganoption.integration.IIntegrator;

public class Waila implements IIntegrator
{

	@Override
	public void overrideContent()
	{
	}

	@Override
	public void preInit()
	{
	}

	@Override
	public void init()
	{
		FMLInterModComms.sendMessage("Waila", "register", "squeek.veganoption.integration.waila.WailaRegistrar.register");
	}

	@Override
	public void postInit()
	{
	}
	
}
