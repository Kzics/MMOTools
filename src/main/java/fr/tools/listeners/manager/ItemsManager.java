package fr.tools.listeners.manager;

import fr.tools.ToolsMain;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.inventory.ItemStack;

public class ItemsManager {

    private final ToolsMain main;
    public ItemsManager(final ToolsMain main){
        this.main = main;
    }

    public ItemStack getAxe(){
        return main.getMmoItems().getItem(Type.TOOL,"SPECIAL_AXE");
    }
    public ItemStack getRod(){
        return main.getMmoItems().getItem(Type.TOOL,"SPECIAL_ROD");
    }
    public ItemStack getPick(){
        return main.getMmoItems().getItem(Type.TOOL,"SPECIAL_PICK");
    }
}
