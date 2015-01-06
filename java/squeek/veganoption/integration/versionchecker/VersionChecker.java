package squeek.veganoption.integration.versionchecker;

import cpw.mods.fml.common.event.FMLInterModComms;
import squeek.veganoption.integration.IIntegrator;

public class VersionChecker implements IIntegrator
{

	@Override
	public void overrideContent()
	{
	}

	@Override
	public void preInit()
	{
		FMLInterModComms.sendMessage("VersionChecker", "addVersionCheck", "http://www.ryanliptak.com/minecraft/versionchecker/squeek502/VeganOption");
	}

	@Override
	public void init()
	{
	}

	@Override
	public void postInit()
	{
	}

}
