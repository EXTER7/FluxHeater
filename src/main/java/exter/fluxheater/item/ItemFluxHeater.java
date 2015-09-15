package exter.fluxheater.item;

import java.util.List;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.ItemEnergyContainer;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import exter.fluxheater.config.FluxHeaterConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;

public class ItemFluxHeater extends ItemEnergyContainer implements IEnergyContainerItem, IFuelHandler
{
  public ItemFluxHeater()
  {
    super(FluxHeaterConfig.capacity, FluxHeaterConfig.charge_rate, FluxHeaterConfig.capacity);
    setUnlocalizedName("fluxHeater");
    setTextureName("fluxheater:flux_heater");
    setMaxStackSize(1);
  }

  @Override
  public int getBurnTime(ItemStack fuel)
  {
    if(fuel.getItem() instanceof ItemFluxHeater)
    {
      for(StackTraceElement element : Thread.currentThread().getStackTrace())
      {
        Class<?> clazz;
        try
        {
          clazz = Class.forName(element.getClassName());
        } catch(ClassNotFoundException e)
        {
          throw new RuntimeException(e);
        }
        if(TileEntity.class.isAssignableFrom(clazz))
        {
          int used = super.extractEnergy(fuel, FluxHeaterConfig.burn_cost, true);
          int time = FluxHeaterConfig.getBurnTime(clazz.asSubclass(TileEntity.class)) * used / FluxHeaterConfig.burn_cost;
          return time;
        }
      }
    }
    return 0;
  }

  @Override
  public ItemStack getContainerItem(ItemStack item)
  {
    item = item.copy();
    super.extractEnergy(item, FluxHeaterConfig.burn_cost, false);
    item.stackSize = 1;
    return item;
  }

  @Override
  public boolean hasContainerItem(ItemStack stack)
  {
    return true;
  }

  @Override
  public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack)
  {
    return false;
  }

  @Override
  public int extractEnergy(ItemStack container, int maxExtract, boolean simulate)
  {
    return 0;
  }

  @SuppressWarnings("unchecked")
  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, EntityPlayer player, @SuppressWarnings("rawtypes") List list, boolean par4)
  {
    list.add(EnumChatFormatting.BLUE + String.valueOf(getEnergyStored(stack)) + " / " + String.valueOf(getMaxEnergyStored(stack)) + " RF");
  }
}
