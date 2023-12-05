package squeek.veganoption.integration;

import net.neoforged.fml.ModList;

public abstract class IntegrationBase
{
	public static boolean integrationExists(String modID)
	{
		return IntegrationHandler.integratorExists(modID);
	}

	public static boolean modExists(String modID)
	{
		return ModList.get().isLoaded(modID);
	}
}
