package pl.pabilo8.immersiveintelligence.common.items.tools;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.tool.ITool;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.EnergyHelper.IIEEnergyItem;
import blusunrize.immersiveengineering.common.util.RotationUtil;
import blusunrize.immersiveengineering.common.util.inventory.IEItemStackHandler;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import pl.pabilo8.immersiveintelligence.Config.IIConfig.Tools;
import pl.pabilo8.immersiveintelligence.api.utils.IWrench;
import pl.pabilo8.immersiveintelligence.common.CommonProxy;
import pl.pabilo8.immersiveintelligence.common.items.ItemIIBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created by Pabilo8 on 2019-05-30.
 */
public class ItemIIElectricWrench extends ItemIIBase implements ITool, IIEEnergyItem, IWrench
{
	public ItemIIElectricWrench()
	{
		super("electric_wrench", 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag)
	{
		String stored = this.getEnergyStored(stack)+"/"+this.getMaxEnergyStored(stack);
		list.add(I18n.format(Lib.DESC+"info.energyStored", stored));
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		if(!stack.isEmpty())
			return new IEItemStackHandler(stack)
			{
				final EnergyHelper.ItemEnergyStorage energyStorage = new EnergyHelper.ItemEnergyStorage(stack);

				@Override
				public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing)
				{
					return capability==CapabilityEnergy.ENERGY||
							super.hasCapability(capability, facing);
				}

				@Override
				public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing)
				{
					if(capability==CapabilityEnergy.ENERGY)
						return (T)energyStorage;
					if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
						return (T)this;
					return null;
				}
			};
		return null;
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{

		return EnumActionResult.PASS;
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		return EnumActionResult.PASS;
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		return true;
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState)
	{
		if(getToolClasses(stack).contains(toolClass)&&hasEnoughEnergy(stack))
			return 4;
		else
			return -1;
	}

	@Override
	public boolean isDamaged(ItemStack stack)
	{
		return false;
	}

	@Override
	public boolean isTool(ItemStack item)
	{
		return true;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand)
	{
		return !player.world.isRemote&&RotationUtil.rotateEntity(entity, player);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return this.getEnergyStored(stack)/this.getMaxEnergyStored(stack);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state)
	{
		if(hasEnoughEnergy(stack))
		{
			for(String type : this.getToolClasses(stack))
				if(state.getBlock().isToolEffective(type, state))
					return 16;
		}
		return super.getDestroySpeed(stack, state);
	}

	@Override
	public int getMaxEnergyStored(ItemStack container)
	{
		return Tools.electric_wrench_capacity;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
	{
		stack.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(Tools.electric_wirecutter_energy_per_use, false);
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}

	@Override
	public Set<String> getToolClasses(ItemStack stack)
	{
		return ImmutableSet.of(CommonProxy.TOOL_WRENCH, CommonProxy.TOOL_ADVANCED_WRENCH);
	}

	@Override
	public boolean canHarvestBlock(@Nonnull IBlockState state, ItemStack stack)
	{
		if(hasEnoughEnergy(stack))
		{
			if(state.getBlock().isToolEffective(CommonProxy.TOOL_WRENCH, state))
				return true;
			else if(state.getBlock().isToolEffective(CommonProxy.TOOL_ADVANCED_WRENCH, state))
				return true;
		}
		return false;
	}

	public boolean hasEnoughEnergy(ItemStack stack)
	{
		return stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() >= Tools.electric_wirecutter_energy_per_use;
	}

	@Override
	public boolean canBeUsed(ItemStack stack)
	{
		return hasEnoughEnergy(stack);
	}

	@Override
	public void damageWrench(ItemStack stack, EntityPlayer player)
	{
		extractEnergy(stack, Tools.electric_wrench_energy_per_use, false);
	}
}
