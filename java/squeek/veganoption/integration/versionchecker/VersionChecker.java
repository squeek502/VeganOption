package squeek.veganoption.integration.versionchecker;

import squeek.veganoption.integration.IntegratorBase;
import cpw.mods.fml.common.event.FMLInterModComms;

public class VersionChecker extends IntegratorBase
{
	@Override
	public void preInit()
	{
		FMLInterModComms.sendMessage(modID, "addVersionCheck", "http://www.ryanliptak.com/minecraft/versionchecker/squeek502/VeganOption");
	}
}
