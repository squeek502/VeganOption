package squeek.veganoption.integration;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.HashMap;
import java.util.Map;

public class IntegrationHandler extends IntegrationBase
{
	private static Map<String, IntegratorBase> integrators = new HashMap<String, IntegratorBase>();

	static
	{
		// todo: reimplement mod integration
	}

	public static void init()
	{
		integrators.values().forEach(IntegratorBase::create);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		integrators.values().forEach(module -> module.registerRenderers(event));
	}

	public static void finish()
	{
		integrators.values().forEach(IntegratorBase::finish);
	}

	public static boolean tryIntegration(String modID, String packageName)
	{
		return tryIntegration(modID, packageName, modID);
	}

	public static boolean tryIntegration(String modID, String packageName, String className)
	{
		if (IntegrationBase.modExists(modID))
		{
			try
			{
				String fullClassName = "squeek.veganoption.integration." + packageName + "." + className;
				Class<?> clazz = Class.forName(fullClassName);
				IntegratorBase integrator = (IntegratorBase) clazz.newInstance();
				integrator.modID = modID;
				integrators.put(modID, integrator);
				return true;
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return false;
	}

	public static boolean integratorExists(String modID)
	{
		return integrators.containsKey(modID);
	}
}
