package pl.pabilo8.immersiveintelligence.common.entity;

import blusunrize.immersiveengineering.client.ClientUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import pl.pabilo8.immersiveintelligence.Config.IIConfig.Vehicles.FieldHowitzer;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.api.utils.IEntitySpecialRepairable;
import pl.pabilo8.immersiveintelligence.api.utils.vehicles.ITowable;
import pl.pabilo8.immersiveintelligence.api.utils.vehicles.IVehicleMultiPart;
import pl.pabilo8.immersiveintelligence.client.ClientProxy;
import pl.pabilo8.immersiveintelligence.common.network.IIPacketHandler;
import pl.pabilo8.immersiveintelligence.common.network.MessageEntityNBTSync;

import javax.annotation.Nullable;

/**
 * @author Pabilo8
 * @since 18.07.2020
 */
public class EntityFieldHowitzer extends Entity implements IVehicleMultiPart, IEntitySpecialRepairable, ITowable
{
	private static final DataParameter<Boolean> dataMarkerForward = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> dataMarkerBackward = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> dataMarkerTurnLeft = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> dataMarkerTurnRight = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Boolean> dataMarkerGunPitchUp = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> dataMarkerGunPitchDown = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Float> dataMarkerAcceleration = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> dataMarkerSpeed = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> dataMarkerShootingProgress = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> dataMarkerReloadProgress = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> dataMarkerGunPitch = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.FLOAT);

	private static final DataParameter<Integer> dataMarkerWheelRightDurability = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> dataMarkerWheelLeftDurability = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> dataMarkerMainDurability = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> dataMarkerGunDurability = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> dataMarkerShieldDurability = EntityDataManager.createKey(EntityFieldHowitzer.class, DataSerializers.VARINT);
	public int rightWheelDurability, leftWheelDurability, mainDurability, gunDurability, shieldDurability;
	public int setupTime = 0;
	public boolean towingOperation = false;
	public int wheelTraverse = 0;
	//Yes
	public float acceleration = 0f, speed = 0f;
	public float shootingProgress = 0f, reloadProgress = 0f, gunPitch = 0f;

	public boolean forward = false, backward = false, turnLeft = false, turnRight = false, reloadKeyPress = false, fireKeyPress = false, gunPitchUp = false, gunPitchDown = false;

	public EntityVehicleMultiPart[] partArray;

	public EntityVehicleWheel partWheelRight = new EntityVehicleWheel(this, "wheel_right", 1F, 1.0F);
	public EntityVehicleWheel partWheelLeft = new EntityVehicleWheel(this, "wheel_left", 1F, 1.0F);
	public EntityVehicleMultiPart partMain = new EntityVehicleMultiPart(this, "main", 1F, 1.0F);
	public EntityVehicleMultiPart partGun = new EntityVehicleMultiPart(this, "gun", 1F, 1.0F);
	public EntityVehicleMultiPart partShieldRight = new EntityVehicleMultiPart(this, "shield_right", 1F, 1.0F);
	public EntityVehicleMultiPart partShieldLeft = new EntityVehicleMultiPart(this, "shield_left", 1F, 1.0F);

	public int destroyTimer = -1;


	static AxisAlignedBB aabb = new AxisAlignedBB(-1.5, 0, -1.5, 1.5, 1.75, 1.5);
	static AxisAlignedBB aabb_wheel = new AxisAlignedBB(-0.25, 0d, 0.5, 0.25, 1d, -0.5);
	static AxisAlignedBB aabb_main = new AxisAlignedBB(-0.5, 0.125d, 0.5, 0.5, 0.625, -1.25);
	static AxisAlignedBB aabb_gun = new AxisAlignedBB(-0.25, 0d, 0.75, 0.25, 1d, -0.75);
	static AxisAlignedBB aabb_shield = new AxisAlignedBB(-0.5, 0d, 0.5, 0.5, 0.45d, -0.5);

	public EntityFieldHowitzer(World worldIn)
	{
		super(worldIn);
		partArray = new EntityVehicleMultiPart[]{partWheelRight, partWheelLeft, partMain, partGun, partShieldRight, partShieldLeft};
	}

	@Override
	protected void entityInit()
	{
		this.dataManager.register(dataMarkerForward, forward);
		this.dataManager.register(dataMarkerBackward, backward);
		this.dataManager.register(dataMarkerTurnLeft, turnLeft);
		this.dataManager.register(dataMarkerTurnRight, turnRight);
		this.dataManager.register(dataMarkerGunPitchUp, gunPitchUp);
		this.dataManager.register(dataMarkerGunPitchDown, gunPitchDown);

		this.dataManager.register(dataMarkerAcceleration, acceleration);
		this.dataManager.register(dataMarkerSpeed, speed);
		this.dataManager.register(dataMarkerShootingProgress, shootingProgress);
		this.dataManager.register(dataMarkerReloadProgress, reloadProgress);
		this.dataManager.register(dataMarkerGunPitch, gunPitch);

		this.dataManager.register(dataMarkerWheelRightDurability, rightWheelDurability);
		this.dataManager.register(dataMarkerWheelLeftDurability, leftWheelDurability);
		this.dataManager.register(dataMarkerMainDurability, mainDurability);
		this.dataManager.register(dataMarkerGunDurability, gunDurability);
		this.dataManager.register(dataMarkerShieldDurability, shieldDurability);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		forward = compound.getBoolean("forward");
		backward = compound.getBoolean("backward");
		turnLeft = compound.getBoolean("turnLeft");
		turnRight = compound.getBoolean("turnRight");
		gunPitchUp = compound.getBoolean("gunPitchUp");
		gunPitchDown = compound.getBoolean("gunPitchDown");

		acceleration = compound.getFloat("acceleration");
		speed = compound.getFloat("speed");
		shootingProgress = compound.getFloat("shootingProgress");
		reloadProgress = compound.getFloat("reloadProgress");
		gunPitch = compound.getFloat("gunPitch");


		if(compound.hasKey("rightWheelDurability"))
			rightWheelDurability = compound.getInteger("rightWheelDurability");
		if(compound.hasKey("leftWheelDurability"))
			leftWheelDurability = compound.getInteger("leftWheelDurability");
		if(compound.hasKey("mainDurability"))
			mainDurability = compound.getInteger("mainDurability");
		if(compound.hasKey("gunDurability"))
			gunDurability = compound.getInteger("gunDurability");
		if(compound.hasKey("shieldDurability"))
			shieldDurability = compound.getInteger("shieldDurability");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
		compound.setBoolean("forward", forward);
		compound.setBoolean("backward", backward);
		compound.setBoolean("turnLeft", turnLeft);
		compound.setBoolean("turnRight", turnRight);
		compound.setBoolean("gunPitchUp", gunPitchUp);
		compound.setBoolean("gunPitchDown", gunPitchDown);

		compound.setFloat("acceleration", acceleration);
		compound.setFloat("speed", speed);
		compound.setFloat("shootingProgress", shootingProgress);
		compound.setFloat("reloadProgress", reloadProgress);
		compound.setFloat("gunPitch", gunPitch);

		compound.setInteger("rightWheelDurability", rightWheelDurability);
		compound.setInteger("leftWheelDurability", leftWheelDurability);
		compound.setInteger("mainDurability", mainDurability);
		compound.setInteger("gunDurability", gunDurability);
		compound.setInteger("shieldDurability", shieldDurability);
	}

	@Override
	public AxisAlignedBB getEntityBoundingBox()
	{
		return aabb.offset(posX, posY, posZ);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox()
	{
		return null;
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn)
	{
		return getEntityBoundingBox();
	}

	@Override
	public void onUpdate()
	{

		//spawn seats
		if(firstUpdate&&!world.isRemote)
		{
			//commander
			EntityVehicleSeat.getOrCreateSeat(this, 0);
			//gunner
			EntityVehicleSeat.getOrCreateSeat(this, 1);
		}
		//super.onUpdate();

		if(towingOperation)
		{
			acceleration = Math.round((acceleration-Math.signum(acceleration)*2)/2)*2;
			setupTime += 1;
			if(setupTime > FieldHowitzer.towingTime)
				towingOperation = false;
		}
		else if(!isRiding())
		{
			if(world.isRemote)
			{
				Entity pre = ClientUtils.mc().player.getRidingEntity();
				if(pre instanceof EntityVehicleSeat&&pre.getRidingEntity()==this)
				{
					//Handle, send to server, get other from server
					int seat = ((EntityVehicleSeat)pre).seatID;
					handleClientKeyInput(seat);
					//handleClientKeyOutput(seat);
				}
				else
				{
					//Get from server
					handleClientKeyOutput(-1);
				}
			}
			else
			{
				handleServerKeyInput();
			}
		}

		if(turnLeft)
			turn(-5, 0);
		else if(turnRight)
			turn(5, 0);
		else if(forward)
			acceleration = Math.min(acceleration+0.1f, 1f);
		else if(backward)
			acceleration = Math.max(acceleration-0.15f, -1f);

		else if(gunPitchUp)
			gunPitch = Math.min(gunPitch+0.5f, 85);
		else if(gunPitchDown)
			gunPitch = Math.max(gunPitch-0.5f, -5);

		speed = 0;

		//handleMovement();
		updateParts();

	}

	private void handleMovement()
	{
		float r = rotationYaw;
		double true_angle = Math.toRadians((r) > 180?360f-(r): (r));
		//ImmersiveIntelligence.logger.info(true_angle);

		Vec3d pos1_x = Utils.offsetPosDirection(-0.5f, true_angle, 0);

		partWheelLeft.rotationYaw = this.rotationYaw;
		partWheelLeft.travel(0, 0, 1f, -0.0125f, speed*0.0125*2f);

		partWheelRight.rotationYaw = this.rotationYaw;
		partWheelRight.travel(0, 0, 1f, -0.0125f, speed*0.0125*2f);

		if(!world.isRemote&&!partWheelLeft.isEntityInsideOpaqueBlock()&&!partWheelLeft.isEntityInsideOpaqueBlock())
		{
			Vec3d currentPos = new Vec3d(partWheelRight.posX+pos1_x.x, partWheelRight.posY, partWheelRight.posZ+pos1_x.z);
			setPosition(currentPos.x, currentPos.y, currentPos.z);
			setVelocity(partWheelRight.motionX, partWheelRight.motionY, partWheelRight.motionZ);
		}
	}

	@Override
	public boolean shouldRiderSit()
	{
		return false;
	}

	@Override
	public boolean canRepair()
	{
		return gunDurability < FieldHowitzer.gunDurability||
				mainDurability < FieldHowitzer.mainDurability||
				leftWheelDurability < FieldHowitzer.wheelDurability||
				rightWheelDurability < FieldHowitzer.wheelDurability||
				shieldDurability < FieldHowitzer.wheelDurability;
	}

	@Override
	public boolean repair(int repairPoints)
	{
		if(gunDurability < FieldHowitzer.gunDurability)
			gunDurability = Math.min(gunDurability+repairPoints, FieldHowitzer.gunDurability);
		else if(mainDurability < FieldHowitzer.mainDurability)
			mainDurability = Math.min(mainDurability+repairPoints, FieldHowitzer.mainDurability);
		else if(leftWheelDurability < FieldHowitzer.wheelDurability)
			leftWheelDurability = Math.min(leftWheelDurability+repairPoints, FieldHowitzer.wheelDurability);
		else if(rightWheelDurability < FieldHowitzer.wheelDurability)
			rightWheelDurability = Math.min(rightWheelDurability+repairPoints, FieldHowitzer.wheelDurability);
		else return false;
		return true;
	}

	@Override
	public int getRepairCost()
	{
		return 1;
	}

	@Override
	public Entity getTowingEntity()
	{
		return getRidingEntity();
	}

	@Override
	public boolean startTowing(Entity tower)
	{
		if(getTowingEntity()==null&&getRecursivePassengers().stream().allMatch(entity -> entity instanceof EntityVehicleSeat))
		{
			towingOperation = true;
			setupTime = 0;
			startRiding(tower);
			return true;
		}
		return false;
	}

	@Override
	public boolean stopTowing()
	{
		if(getTowingEntity()!=null)
		{
			towingOperation = true;
			setupTime = 0;
			dismountRidingEntity();
			return true;
		}
		return false;
	}

	@Override
	public boolean canMoveTowed()
	{
		return !towingOperation;
	}

	@Override
	protected boolean canFitPassenger(Entity passenger)
	{
		return passenger instanceof EntityVehicleSeat;
	}

	@Override
	public void getSeatRidingPosition(int seatID, Entity passenger)
	{
		double true_angle = Math.toRadians((-rotationYaw) > 180?360f-(-rotationYaw): (-rotationYaw));
		double true_angle2 = Math.toRadians((-rotationYaw-90) > 180?360f-(-rotationYaw-90): (-rotationYaw-90));

		Vec3d pos2 = Utils.offsetPosDirection(-0.65f, true_angle, 0);
		Vec3d pos3 = Utils.offsetPosDirection(seatID==0?-0.75f: 0.75f, true_angle2, 0);

		passenger.setPosition(posX+pos2.x+pos3.x, posY+pos3.y, posZ+pos2.z+pos3.z);
	}

	@Override
	public void getSeatRidingAngle(int seatID, Entity passenger)
	{
		passenger.setRenderYawOffset(this.rotationYaw);

		float f = MathHelper.wrapDegrees(passenger.rotationYaw-this.rotationYaw);
		float f1 = MathHelper.clamp(f, -75.0F, 75.0F);
		passenger.prevRotationYaw += f1-f;
		passenger.rotationYaw += f1-f;
		passenger.setRotationYawHead(passenger.rotationYaw);
	}

	@Override
	public boolean shouldSeatPassengerSit(int seatID, Entity passenger)
	{
		return false;
	}

	@Override
	public void updatePassenger(Entity passenger)
	{
		if(isPassenger(passenger))
			passenger.setPosition(posX, posY, posZ);
	}

	@Override
	public void applyOrientationToEntity(Entity passenger)
	{
		if(passenger!=null&&isPassenger(passenger))
		{
			passenger.rotationYaw = this.rotationYaw;
			passenger.rotationPitch = this.rotationPitch;
		}
	}

	@Override
	public boolean onInteractWithPart(EntityVehicleMultiPart part, EntityPlayer player, EnumHand hand)
	{
		if(!world.isRemote&&!towingOperation)
		{
			if(part==partShieldRight||part==partWheelRight)
				player.startRiding(EntityVehicleSeat.getOrCreateSeat(this, 0));
			else if(part==partShieldLeft||part==partWheelLeft)
				player.startRiding(EntityVehicleSeat.getOrCreateSeat(this, 1));
			return true;
		}
		return false;
	}

	@Override
	public String[] getOverlayTextOnPart(EntityVehicleMultiPart part, EntityPlayer player, RayTraceResult mop, boolean hammer)
	{
		return new String[0];
	}

	@Override
	public World getWorld()
	{
		return this.getEntityWorld();
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float amount)
	{
		if(part==partGun&&source.isProjectile()||source.isExplosion()||source.isFireDamage())
		{
			gunDurability -= amount*0.85;
			dataManager.set(dataMarkerGunDurability, gunDurability);
			return true;
		}
		else if(part==partMain&&source.isProjectile()||source.isExplosion()||source.isFireDamage())
		{
			mainDurability -= amount*0.85;
			dataManager.set(dataMarkerMainDurability, mainDurability);
		}
		else if(part==partWheelRight)
		{
			rightWheelDurability -= amount;
			dataManager.set(dataMarkerWheelRightDurability, rightWheelDurability);
		}
		else if(part==partWheelLeft)
		{
			leftWheelDurability -= amount;
			dataManager.set(dataMarkerWheelLeftDurability, leftWheelDurability);
		}
		else if((part==partShieldLeft||part==partShieldRight)&&(source.isProjectile()||source.isExplosion()||source.isFireDamage())&&amount > 8)
		{
			shieldDurability -= amount;
			dataManager.set(dataMarkerShieldDurability, shieldDurability);
		}
		else
			return false;
		return true;
	}

	@Override
	public boolean canRenderOnFire()
	{
		return false;
	}

	public Entity[] getParts()
	{
		return partArray;
	}

	private void updateParts()
	{
		double true_angle = Math.toRadians((-rotationYaw) > 180?360f-(-rotationYaw): (-rotationYaw));
		double true_angle2 = Math.toRadians((-rotationYaw-90) > 180?360f-(-rotationYaw-90): (-rotationYaw-90));

		Vec3d pos1_x = Utils.offsetPosDirection(0.5f, true_angle, 0);
		Vec3d pos2_x = pos1_x.scale(1);
		Vec3d pos1_z = Utils.offsetPosDirection(0.75f, true_angle2, 0);
		Vec3d pos2_z = Utils.offsetPosDirection(-0.75f, true_angle2, 0);

		aabb_shield = new AxisAlignedBB(-0.5, 0d, 0.125, 0.5, 1.325d, -0.125);

		this.partWheelLeft.setLocationAndAngles(posX+pos1_z.x, posY, posZ+pos1_z.z, 0.0F, 0);
		this.partWheelLeft.setEntityBoundingBox(aabb_wheel.offset(this.partWheelLeft.posX, this.partWheelLeft.posY, this.partWheelLeft.posZ));
		this.partWheelLeft.setVelocity(this.motionX, this.motionY, this.motionZ);
		this.partWheelLeft.onUpdate();

		this.partWheelRight.setLocationAndAngles(posX+pos2_z.x, posY, posZ+pos2_z.z, 0.0F, 0);
		this.partWheelRight.setEntityBoundingBox(aabb_wheel.offset(this.partWheelRight.posX, this.partWheelRight.posY, this.partWheelRight.posZ));
		this.partWheelRight.setVelocity(this.motionX, this.motionY, this.motionZ);
		this.partWheelRight.onUpdate();

		this.partMain.setLocationAndAngles(posX, posY, posZ, 0.0F, 0);
		this.partMain.setEntityBoundingBox(aabb_main.offset(this.partMain.posX, this.partMain.posY, this.partMain.posZ));
		this.partMain.setVelocity(this.motionX, this.motionY, this.motionZ);
		this.partMain.onUpdate();

		this.partGun.setLocationAndAngles(posX+pos1_x.x, posY+0.65, posZ+pos1_x.z, 0.0F, 0);
		this.partGun.setEntityBoundingBox(aabb_gun.offset(this.partGun.posX, this.partGun.posY, this.partGun.posZ));
		this.partGun.setVelocity(this.motionX, this.motionY, this.motionZ);
		this.partGun.onUpdate();

		this.partShieldLeft.setLocationAndAngles(posX+pos1_z.x+pos2_x.x, posY+0.375, posZ+pos1_z.z+pos2_x.z, 0.0F, 0);
		this.partShieldLeft.setEntityBoundingBox(aabb_shield.offset(this.partShieldLeft.posX, this.partShieldLeft.posY, this.partShieldLeft.posZ));
		this.partShieldLeft.setVelocity(this.motionX, this.motionY, this.motionZ);
		this.partShieldLeft.onUpdate();

		this.partShieldRight.setLocationAndAngles(posX+pos2_z.x+pos2_x.x, posY+0.375, posZ+pos2_z.z+pos2_x.z, 0.0F, 0);
		this.partShieldRight.setEntityBoundingBox(aabb_shield.offset(this.partShieldRight.posX, this.partShieldRight.posY, this.partShieldRight.posZ));
		this.partShieldRight.setVelocity(this.motionX, this.motionY, this.motionZ);
		this.partShieldRight.onUpdate();
	}

	@Override
	public void setDead()
	{
		getPassengers().forEach(Entity::setDead);
		super.setDead();
	}

	private void handleClientKeyInput(int seat)
	{
		boolean hasChanged;

		if(seat==1)
		{
			boolean f = forward, b = backward, tl = turnLeft, tr = turnRight;
			forward = ClientUtils.mc().gameSettings.keyBindForward.isKeyDown();
			backward = ClientUtils.mc().gameSettings.keyBindBack.isKeyDown();
			turnLeft = ClientUtils.mc().gameSettings.keyBindLeft.isKeyDown();
			turnRight = ClientUtils.mc().gameSettings.keyBindRight.isKeyDown();
			hasChanged = f^forward||b^backward||tl^turnLeft||tr^turnRight;
		}
		else
		{
			boolean u = gunPitchUp, d = gunPitchDown, fk = fireKeyPress, rr = reloadKeyPress;
			gunPitchUp = ClientUtils.mc().gameSettings.keyBindForward.isKeyDown();
			gunPitchDown = ClientUtils.mc().gameSettings.keyBindBack.isKeyDown();
			fireKeyPress = Mouse.isButtonDown(0);
			reloadKeyPress = ClientProxy.keybind_manualReload.isKeyDown();
			hasChanged = u^gunPitchUp||d^gunPitchDown||fk^fireKeyPress||rr^reloadKeyPress;
		}


		if(hasChanged)
			IIPacketHandler.INSTANCE.sendToServer(new MessageEntityNBTSync(this, updateKeys(seat)));
	}

	@SideOnly(Side.CLIENT)
	private NBTTagCompound updateKeys(int seat)
	{
		NBTTagCompound compound = new NBTTagCompound();
		if(seat==1)
		{
			compound.setBoolean("forward", forward);
			compound.setBoolean("backward", backward);
			compound.setBoolean("turnLeft", turnLeft);
			compound.setBoolean("turnRight", turnRight);
		}
		else
		{
			compound.setBoolean("gunPitchUp", gunPitchUp);
			compound.setBoolean("gunPitchDown", gunPitchDown);
			compound.setBoolean("fireKeyPress", fireKeyPress);
			compound.setBoolean("reloadKeyPress", reloadKeyPress);
		}

		return compound;
	}

	public void syncKeyPress(NBTTagCompound tag)
	{
		if(tag.hasKey("forward"))
			forward = tag.getBoolean("forward");
		if(tag.hasKey("backward"))
			backward = tag.getBoolean("backward");
		if(tag.hasKey("turnLeft"))
			turnLeft = tag.getBoolean("turnLeft");
		if(tag.hasKey("turnRight"))
			turnRight = tag.getBoolean("turnRight");
		if(tag.hasKey("gunPitchUp"))
			gunPitchUp = tag.getBoolean("gunPitchUp");
		if(tag.hasKey("gunPitchDown"))
			gunPitchDown = tag.getBoolean("gunPitchDown");
		if(tag.hasKey("fireKeyPress"))
			fireKeyPress = tag.getBoolean("fireKeyPress");
		if(tag.hasKey("reloadKeyPress"))
			reloadKeyPress = tag.getBoolean("reloadKeyPress");
	}

	private void handleClientKeyOutput(int seat)
	{
		if(seat!=1)
		{
			forward = dataManager.get(dataMarkerForward);
			backward = dataManager.get(dataMarkerBackward);
			turnLeft = dataManager.get(dataMarkerTurnLeft);
			turnRight = dataManager.get(dataMarkerTurnRight);
		}
		if(seat!=0)
		{
			gunPitchUp = dataManager.get(dataMarkerGunPitchUp);
			gunPitchDown = dataManager.get(dataMarkerGunPitchDown);
		}

		acceleration = dataManager.get(dataMarkerAcceleration);
		speed = dataManager.get(dataMarkerSpeed);
		shootingProgress = dataManager.get(dataMarkerShootingProgress);
		reloadProgress = dataManager.get(dataMarkerReloadProgress);
		gunPitch = dataManager.get(dataMarkerGunPitch);

		rightWheelDurability = dataManager.get(dataMarkerWheelRightDurability);
		leftWheelDurability = dataManager.get(dataMarkerWheelLeftDurability);
		mainDurability = dataManager.get(dataMarkerMainDurability);
		gunDurability = dataManager.get(dataMarkerGunDurability);
		shieldDurability = dataManager.get(dataMarkerShieldDurability);
	}

	private void handleServerKeyInput()
	{
		dataManager.set(dataMarkerForward, forward);
		dataManager.set(dataMarkerBackward, backward);
		dataManager.set(dataMarkerTurnLeft, turnLeft);
		dataManager.set(dataMarkerTurnRight, turnRight);
		dataManager.set(dataMarkerGunPitchUp, gunPitchUp);
		dataManager.set(dataMarkerGunPitchDown, gunPitchDown);

		dataManager.set(dataMarkerAcceleration, acceleration);
		dataManager.set(dataMarkerSpeed, speed);
		dataManager.set(dataMarkerShootingProgress, shootingProgress);
		dataManager.set(dataMarkerReloadProgress, reloadProgress);
		dataManager.set(dataMarkerGunPitch, gunPitch);

		dataManager.set(dataMarkerWheelRightDurability, rightWheelDurability);
		dataManager.set(dataMarkerWheelLeftDurability, leftWheelDurability);
		dataManager.set(dataMarkerMainDurability, mainDurability);
		dataManager.set(dataMarkerGunDurability, gunDurability);
		dataManager.set(dataMarkerShieldDurability, shieldDurability);
	}
}
