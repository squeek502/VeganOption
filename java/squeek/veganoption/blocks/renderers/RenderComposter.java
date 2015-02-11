package squeek.veganoption.blocks.renderers;

import java.util.Calendar;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import squeek.veganoption.ModInfo;
import squeek.veganoption.blocks.tiles.TileEntityComposter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderComposter extends TileEntitySpecialRenderer implements IItemRenderer
{
	private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
	private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
	private static final ResourceLocation TEXTURE_LEGS = new ResourceLocation(ModInfo.MODID_LOWER, "textures/entity/composter_legs.png");
	private ModelChest modelChest = new ModelChest();
	private ModelComposterLegs modelLegs = new ModelComposterLegs();
	private boolean isChristmas;

	private TileEntityComposter dummyItemRenderTile = new TileEntityComposter();

	protected static enum Axis
	{
		X, Y;
	}

	public RenderComposter()
	{
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26)
		{
			this.isChristmas = true;
		}
	}

	public void renderTileEntityAt(TileEntityComposter tile, double x, double y, double z, float partialTickTime)
	{
		int meta;

		if (!tile.hasWorldObj())
		{
			meta = ForgeDirection.WEST.ordinal();
		}
		else
		{
			meta = tile.getBlockMetadata();
		}

		short rotation = 0;

		if (meta == ForgeDirection.NORTH.ordinal())
			rotation = 0;
		else if (meta == ForgeDirection.SOUTH.ordinal())
			rotation = 180;
		else if (meta == ForgeDirection.EAST.ordinal())
			rotation = 90;
		else if (meta == ForgeDirection.WEST.ordinal())
			rotation = -90;

		Axis axis = Math.abs(rotation) == 90 ? Axis.X : Axis.Y;

		this.bindTexture(TEXTURE_LEGS);

		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float) x + 0.5f, (float) y + 1.5F, (float) z + 0.5f);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glRotatef((float) rotation, 0.0F, 1.0F, 0.0F);

		modelLegs.renderAll();

		GL11.glPopMatrix();

		ModelChest modelchest = this.modelChest;

		if (this.isChristmas)
		{
			this.bindTexture(TEXTURE_CHRISTMAS);
		}
		else
		{
			this.bindTexture(TEXTURE_NORMAL);
		}

		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glTranslatef((float) x + (axis == Axis.X ? 0.1F : 0F), (float) y + 1.0F, (float) z + (axis == Axis.X ? 1F : 0.9F));
		GL11.glScalef(axis == Axis.X ? 0.8F : 1.0F, -0.8F, axis == Axis.X ? -1.0F : -0.8F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		if (tile.hasWorldObj() && tile.isAerating())
		{
			final int num_turns = 3;
			GL11.glRotatef(-(tile.getAeratingPercent() * 360f * num_turns), axis == Axis.X ? 0F : 1F, 0.0F, axis == Axis.X ? 1F : 0F);
		}

		GL11.glRotatef((float) rotation, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		float f1 = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTickTime;

		f1 = 1.0F - f1;
		f1 = 1.0F - f1 * f1 * f1;
		modelchest.chestLid.rotateAngleX = -(f1 * (float) Math.PI / 2.0F);
		modelchest.renderAll();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
	{
		this.renderTileEntityAt((TileEntityComposter) p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		switch (type)
		{
			case ENTITY:
			{
				renderTileEntityAt(dummyItemRenderTile, -0.5D, 0.0D, -0.5D, 0.0F);
				return;
			}
			case EQUIPPED:
			{
				renderTileEntityAt(dummyItemRenderTile, 0.0F, 0.0F, 0.0F, 0.0F);
				return;
			}
			case EQUIPPED_FIRST_PERSON:
			{
				renderTileEntityAt(dummyItemRenderTile, 0F, 0F, 0F, 0.0F);
				return;
			}
			case INVENTORY:
			{
				renderTileEntityAt(dummyItemRenderTile, 0.5F, 0.3F, 0.5F, 0.0F);
				return;
			}
			default:
				return;
		}
	}
}
