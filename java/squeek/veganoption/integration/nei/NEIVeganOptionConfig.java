package squeek.veganoption.integration.nei;

import squeek.veganoption.ModInfo;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIVeganOptionConfig implements IConfigureNEI
{
	@Override
	public String getName()
	{
		return ModInfo.MODID;
	}

	@Override
	public String getVersion()
	{
		return ModInfo.VERSION;
	}

	@Override
	public void loadConfig()
	{
		// TODO: Make sure all the strings are localized
		// TODO: Composter handler
		API.registerRecipeHandler(new TextHandler());
		API.registerUsageHandler(new TextHandler());
		API.registerRecipeHandler(new DropsHandler());
		API.registerUsageHandler(new DropsHandler());
	}
}
