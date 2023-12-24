package squeek.veganoption.integration.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import squeek.veganoption.helpers.LangHelper;

import java.util.List;

public class ComposterProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor>
{
	static final ResourceLocation UID = new ResourceLocation(ModInfo.MODID_LOWER, "composter");
	private static final String DATA_PERCENT = "Percent";
	private static final String DATA_TEMPERATURE = "Temperature";
	private static final String DATA_COMPOSTING = "IsComposting";

	private static ComposterProvider INSTANCE;

	static ComposterProvider getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new ComposterProvider();
		return INSTANCE;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
	{
		CompoundTag data = accessor.getServerData();
		if (data.getBoolean(DATA_COMPOSTING))
		{
			VeganOptionPlugin.addPercentInfoToTooltip(tooltip, "waila.composter.percent", data.getFloat(DATA_PERCENT));
			VeganOptionPlugin.addInfoToTooltip(tooltip, "waila.composter.temperature", String.format("%1$dºC", (int) Math.floor(data.getFloat(DATA_TEMPERATURE))));
		}
		else
		{
			tooltip.add(Component.translatable(LangHelper.prependModId("waila.composter.empty")));
		}
	}

	@Override
	public ResourceLocation getUid()
	{
		return UID;
	}

	@Override
	public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor)
	{
		TileEntityComposter te = (TileEntityComposter) accessor.getBlockEntity();
		compoundTag.putBoolean(DATA_COMPOSTING, te.isComposting());
		compoundTag.putFloat(DATA_PERCENT, te.getCompostingPercent());
		compoundTag.putFloat(DATA_TEMPERATURE, te.getCompostTemperature());
	}

	static class HideInventory implements IServerExtensionProvider<TileEntityComposter, ItemStack>
	{
		static final ResourceLocation UID = new ResourceLocation(ModInfo.MODID_LOWER, "hide_inventory");
		private static HideInventory INSTANCE;

		static HideInventory getInstance()
		{
			if (INSTANCE == null)
				INSTANCE = new HideInventory();
			return INSTANCE;
		}

		@Override
		public @Nullable List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor, TileEntityComposter tileEntityComposter)
		{
			return List.of();
		}

		@Override
		public ResourceLocation getUid()
		{
			return UID;
		}
	}
}
