package exter.fluxheater.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;

public class FluxHeaterConfig
{
  static public int burn_time;
  static public int burn_cost;

  static public int capacity;
  static public int charge_rate;

  static public int burn_time_fluid;
  static public int burn_time_energy;

  static public Map<Class<? extends TileEntity>,Integer> burn_times = new HashMap<Class<? extends TileEntity>,Integer>();
  
  static public int getBurnTime(Class<? extends TileEntity> clazz)
  {
    Integer i = burn_times.get(clazz);
    if(i == null)
    {
      return 0;
    }
    return i;
  }
  
  static public void load(Configuration config)
  {
    burn_time = config.get("fluxheater", "burn_time", 25,"Default cycle burn time in ticks.").getInt(25); 
    burn_time_fluid = config.get("fluxheater", "burn_time.fuidtile", 22, "Default cycle burn time in ticks (for fluid TEs).").getInt(22); 
    burn_time_energy = config.get("fluxheater", "burn_time.energytile", 20, "Default cycle burn time in ticks (for RF TEs).").getInt(20);
    burn_cost = config.get("fluxheater", "burn_cost", 1000, "How much RF each cycle consumes.").getInt(1000); 
    capacity = config.get("fluxheater", "fluxpad.basic.capacity", 50000, "How much RF the Flux Heater can hold.").getInt(50000);    
    charge_rate = config.get("fluxheater", "charge_rate", 250, "Maximun RF/t rate in which the Flux Heater can charge.").getInt(250); 
  }
}
