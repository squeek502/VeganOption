package squeek.veganoption.integration.versionchecker;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import squeek.veganoption.integration.IntegratorBase;

public class VersionChecker extends IntegratorBase
{
	@Override
	public void preInit()
	{
		FMLInterModComms.sendMessage(modID, "addVersionCheck", "http://www.ryanliptak.com/minecraft/versionchecker/squeek502/VeganOption");
	}
}
