package fr.tools.listeners.manager;

import fr.tools.listeners.utils.Pair;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.HashMap;
import java.util.Map;

public class FishingAreaManager {



    private Map<Location, Pair<ArmorStand,Boolean>> fishingAreas;
    public FishingAreaManager(){
        this.fishingAreas = new HashMap<>();
    }


    public boolean isFisheable(final Location location){
        if(!fishingAreas.containsKey(location)) return false;

        return fishingAreas.get(location).getValue();
    }

    public void addArea(final Location location,final ArmorStand stand){
        if(fishingAreas.containsKey(location)) return;

        fishingAreas.put(location,new Pair<>(stand,true));
    }

    public void removeArea(final Location location){
        if(!fishingAreas.containsKey(location)) return;

        fishingAreas.remove(location);

    }

    public void setFisheableState(Location location,final boolean state){
        if(!fishingAreas.containsKey(location)) return;

        final Pair<ArmorStand, Boolean> pair = this.fishingAreas.get(location);
        final ArmorStand stand = pair.getKey();

        this.fishingAreas.put(location,new Pair<>(stand,state));
    }

    public ArmorStand getStateStand(final Location location){
        System.out.println(this.fishingAreas.keySet());
        return this.fishingAreas.get(location).getKey();
    }



}
