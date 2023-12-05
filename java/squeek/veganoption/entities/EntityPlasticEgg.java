package squeek.veganoption.entities;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import squeek.veganoption.content.Modifiers;
import squeek.veganoption.content.modules.Egg;

public class EntityPlasticEgg extends ThrowableItemProjectile
{
	private static final byte EVENT_ID = 1;

	private Item insideEgg;
	public EntityPlasticEgg(EntityType<? extends ThrowableItemProjectile> type, Level level)
	{
		super(type, level);
	}

	public EntityPlasticEgg(Item insideEgg, double x, double y, double z, Level level)
	{
		super(Egg.plasticEggEntityType.get(), x, y, z, level);
		this.insideEgg = insideEgg;
	}

	public EntityPlasticEgg(Item insideEgg, LivingEntity thrower, Level level)
	{
		super(Egg.plasticEggEntityType.get(), thrower, level);
		this.insideEgg = insideEgg;
	}

	@Override
	protected void onHitEntity(EntityHitResult hitResult)
	{
		super.onHitEntity(hitResult);
		Modifiers.eggs.findModifierForItem(insideEgg).onHitEntity(hitResult, this);
		hitResult.getEntity().hurt(damageSources().thrown(this, getOwner()), 0f);
	}

	@Override
	protected void onHitBlock(BlockHitResult hitResult)
	{
		super.onHitBlock(hitResult);
		Modifiers.eggs.findModifierForItem(insideEgg).onHitBlock(hitResult, this);
	}

	@Override
	protected void onHit(HitResult hitResult)
	{
		Modifiers.eggs.findModifierForItem(insideEgg).onHitGeneric(hitResult, this);
		super.onHit(hitResult);
		if (!level().isClientSide())
		{
			level().broadcastEntityEvent(this, EVENT_ID);
			discard();
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == EVENT_ID) {

			for(int i = 0; i < 8; ++i) {
				level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, getItem()), getX(), getY(), getZ(), (random.nextFloat() - 0.5) * 0.08, (random.nextFloat() - 0.5) * 0.08, (random.nextFloat() - 0.5) * 0.08);
			}
		}
	}

	@Override
	protected Item getDefaultItem()
	{
		return Egg.plasticEgg.get();
	}
}
