package fr.tools.listeners.commands;


import fr.tools.ToolsMain;
import fr.tools.listeners.utils.ColorsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.metadata.FixedMetadataValue;

public class FishAreaCommand implements CommandExecutor {

    private final ToolsMain main;
    public FishAreaCommand(final ToolsMain main){
        this.main = main;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length != 4){
            sender.sendMessage(ColorsUtil.translate.apply("Correct usage : /fisharea (world) (x) (y) (z)"));
            return false;
        }

        final World world = Bukkit.getWorld(args[0]);
        if(world == null){
            sender.sendMessage(ColorsUtil.translate.apply("Incorrect world"));
            return false;
        }

        final double xValue;
        final double yValue;
        final double zValue;
        try{

            xValue = Double.parseDouble(args[1]);
            yValue = Double.parseDouble(args[2]);
            zValue = Double.parseDouble(args[3]);
        }catch (Exception e){
            sender.sendMessage(ColorsUtil.translate.apply("Incorrect coordinates"));
            return false;
        }

        if(spawnArmorStand(new Location(world,xValue,yValue,zValue),"Fishing area",0)){
            sender.sendMessage("Success");
        }else {
            sender.sendMessage(ColorsUtil.translate.apply("Water is missing to spawn fish..."));
        }

        return false;
    }

    private boolean spawnArmorStand(Location loc,final String name,int index) {
        final Block block = loc.getWorld().getHighestBlockAt(loc);

        if(!block.getType().equals(Material.WATER)) return false;
        ArmorStand armorStand = loc.getWorld().spawn(loc, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setCustomName(name);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);


        if(index == 0){
            final Location newLoc = loc.add(0,-1,0);

            final boolean canFish = main.getFishingAreaManager().isFisheable(newLoc);
            armorStand.setMetadata("fishing", new FixedMetadataValue(main, ""));
            spawnArmorStand(newLoc,canFish ? "Can Fish" : "Can't Fish",index+1);
        }else{

            main.getFishingAreaManager().addArea(loc,armorStand);

            spawnFish(block.getLocation(),3);
        }
        return true;
    }

    private void spawnFish(Location fishCenter,int radius){
        int fishCount = 10;
        for (int i = 0; i < fishCount; i++) {
            Location fishLocation = fishCenter.clone().add(Math.random() * radius * 2 - radius, 0, Math.random() * radius * 2 - radius);
            Fish fish = (Fish) fishLocation.getWorld().spawnEntity(fishLocation, EntityType.TROPICAL_FISH);
            fish.setInvulnerable(true);
            fish.setPersistent(false);
            fish.setAI(false);
        }
    }

}
