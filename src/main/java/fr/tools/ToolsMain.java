package fr.tools;

import fr.tools.listeners.AxeListeners;
import fr.tools.listeners.commands.FishAreaCommand;
import fr.tools.listeners.PickListeners;
import fr.tools.listeners.RodListeners;
import fr.tools.listeners.commands.ItemsCommand;
import fr.tools.listeners.manager.FishingAreaManager;
import fr.tools.listeners.manager.ItemsManager;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.comp.mmocore.MMOCoreHook;
import net.Indyuce.mmoitems.comp.rpg.SkillsHook;
import net.Indyuce.mmoitems.comp.rpg.SkillsProHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ToolsMain extends JavaPlugin {


    private List<UUID> breakers;
    private FishingAreaManager fishingAreaManager;
    private MMOItems mmoItems;
    private ItemsManager itemsManager;

    @Override
    public void onEnable() {

        try {
            mmoItems = (MMOItems) Bukkit.getPluginManager().getPlugin("MMOItems");
        }catch (Exception e){
            this.getLogger().severe("MMOCORE Plugin is missing ! Please add it then restart the server !");

        }
        mmoItems = (MMOItems) Bukkit.getPluginManager().getPlugin("MMOItems");
        itemsManager = new ItemsManager(this);


        this.getServer().getPluginManager().registerEvents(new AxeListeners(this),this);
        this.getServer().getPluginManager().registerEvents(new PickListeners(this),this);
        this.getServer().getPluginManager().registerEvents(new RodListeners(this),this);

        this.getCommand("fisharea").setExecutor(new FishAreaCommand(this));
        this.getCommand("specialitems").setExecutor(new ItemsCommand(this));

        fishingAreaManager = new FishingAreaManager();
        breakers = new ArrayList<>();

    }


    public List<UUID> getBreakers() {
        return breakers;
    }

    public FishingAreaManager getFishingAreaManager() {
        return fishingAreaManager;
    }

    public MMOItems getMmoItems() {
        return mmoItems;
    }

    public ItemsManager getItemsManager() {
        return itemsManager;
    }

    public static void giveExperience(final Player player,final int amount){
        PlayerData playerData = PlayerData.get(player.getUniqueId());
        double exp = playerData.getCollectionSkills().getExperience(MMOCore.plugin.professionManager.get("farmer"));

        playerData.getCollectionSkills().setExperience(MMOCore.plugin.professionManager.get("farmer"),exp + amount);

    }
}
