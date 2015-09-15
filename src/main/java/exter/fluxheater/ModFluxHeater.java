package exter.fluxheater;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cofh.api.energy.IEnergyProvider;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import exter.fluxheater.config.FluxHeaterConfig;
import exter.fluxheater.item.ItemFluxHeater;
import exter.fluxheater.proxy.CommonFluxHeaterProxy;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(
    modid = "fluxheater",
    name = "FluxHeater",
    version = "1.0.0.0",
    dependencies =
         "required-after:Forge@[10.13.4.1448,);")
public class ModFluxHeater
{
  @Mod.Instance
  static public ModFluxHeater instance;

  public static Logger log = LogManager.getLogger("fluxheater");

  @SidedProxy(
      clientSide = "exter.fluxheater.proxy.ClientFluxHeaterProxy",
      serverSide = "exter.fluxheater.proxy.CommonFluxHeaterProxy")
  static public CommonFluxHeaterProxy proxy;

  private Configuration conf;

  static public ItemFluxHeater item_heater;

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event)
  {
    proxy.preInit();
    conf = new Configuration(event.getSuggestedConfigurationFile());
    FluxHeaterConfig.load(conf);
    item_heater = new ItemFluxHeater();
    GameRegistry.registerItem(item_heater, "heater");
    GameRegistry.registerFuelHandler(item_heater);
    conf.save();
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event)
  {
    proxy.init();
  }

  @Mod.EventHandler
  public void postInit(FMLPostInitializationEvent event)
  {
    
    @SuppressWarnings("unchecked")
    //Get the value of TileEntity.classToNameMap
    Map<Class<TileEntity>,String> tiles = (Map<Class<TileEntity>,String>)ObfuscationReflectionHelper.getPrivateValue(TileEntity.class, null, 2);

    // Assign default values depending on which interfaces TileEntity implements.
    for(Class<TileEntity> clazz:tiles.keySet())
    {
      String name = clazz.getName();
      int time = conf.get("tile_entity", name, -1).getInt(-1);
      if(time < 0)
      {
        if(IEnergyProvider.class.isAssignableFrom(clazz)) // RF generator tiles
        {
          time = FluxHeaterConfig.burn_time_energy;
        } else if(IFluidHandler.class.isAssignableFrom(clazz)) // Fluid handling tile (the ones that use fuel tend to be water->steam boilers)
        {
          time = FluxHeaterConfig.burn_time_fluid;
        } else
        {
          time = FluxHeaterConfig.burn_time;
        }
      }
      log.info("Automaticaly added '" + name + "' with burn time of " + time);

      conf.getCategory("tile_entity").put(name, new Property(name, String.valueOf(time),Property.Type.INTEGER));
      FluxHeaterConfig.burn_times.put(clazz, time);
    }
    conf.save();
    
    proxy.postInit();
    if(OreDictionary.getOres("ingotCupronickel").size() > 0)
    {
      GameRegistry.addRecipe(new ShapedOreRecipe(
          new ItemStack(item_heater),
          "CRC",
          "IBI",
          "IGI",
          'I', "ingotIron",
          'C', "ingotCuproNickel",
          'G', "ingotGold",
          'B', Blocks.redstone_block,
          'R', Items.redstone).setMirrored(true));
    } else if(OreDictionary.getOres("ingotCopper").size() > 0 && OreDictionary.getOres("ingotNickel").size() > 0)
    {
      GameRegistry.addRecipe(new ShapedOreRecipe(
          new ItemStack(item_heater),
          "NRC",
          "IBI",
          "IGI",
          'I', "ingotIron",
          'C', "ingotCopper",
          'N', "ingotNickel",
          'G', "ingotGold",
          'B', Blocks.redstone_block,
          'R', Items.redstone).setMirrored(true));
    } else
    {
      GameRegistry.addRecipe(new ShapedOreRecipe(
          new ItemStack(item_heater),
          "GGG",
          "IRI",
          "III",
          'I', "ingotIron",
          'G', "ingotGold",
          'R', Blocks.redstone_block));
    }
  }
}
