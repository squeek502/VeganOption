package squeek.veganoption.blocks.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelComposterLegs extends ModelBase
{
	ModelRenderer leg1;
	ModelRenderer leg2;
	ModelRenderer leg3;
	ModelRenderer leg4;

	public ModelComposterLegs()
	{
		textureWidth = 32;
		textureHeight = 32;

		leg1 = new ModelRenderer(this, 0, 0);
		leg1.addBox(-0.5F, 0F, -0.5F, 1, 12, 1);
		leg1.setRotationPoint(7.5F, 13.5F, -1F);
		leg1.setTextureSize(32, 32);
		leg1.mirror = true;
		setRotation(leg1, 0.5235988F, 0F, 0F);
		leg2 = new ModelRenderer(this, 0, 0);
		leg2.addBox(-0.5F, 0F, -0.5F, 1, 12, 1);
		leg2.setRotationPoint(7.5F, 13.5F, 1F);
		leg2.setTextureSize(32, 32);
		leg2.mirror = true;
		setRotation(leg2, -0.5235988F, 0F, 0F);
		leg3 = new ModelRenderer(this, 0, 0);
		leg3.addBox(-0.5F, 0F, -0.5F, 1, 12, 1);
		leg3.setRotationPoint(-7.5F, 13.5F, -1F);
		leg3.setTextureSize(32, 32);
		leg3.mirror = true;
		setRotation(leg3, 0.5235988F, 0F, 0F);
		leg4 = new ModelRenderer(this, 0, 0);
		leg4.addBox(-0.5F, 0F, -0.5F, 1, 12, 1);
		leg4.setRotationPoint(-7.5F, 13.5F, 1F);
		leg4.setTextureSize(32, 32);
		leg4.mirror = true;
		setRotation(leg4, -0.5235988F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		leg1.render(f5);
		leg2.render(f5);
		leg3.render(f5);
		leg4.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void renderAll()
	{
		leg1.render(0.0625F);
		leg2.render(0.0625F);
		leg3.render(0.0625F);
		leg4.render(0.0625F);
	}

}
