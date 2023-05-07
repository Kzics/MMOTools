package fr.tools.listeners.commands;

import fr.tools.ToolsMain;
import fr.tools.listeners.enums.ItemsEnum;
import fr.tools.listeners.manager.ItemsManager;
import fr.tools.listeners.utils.ColorsUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemsCommand implements CommandExecutor {

    private final ItemsManager itemsManager;
    public ItemsCommand(final ToolsMain main){
        this.itemsManager = main.getItemsManager();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length != 3){
            sender.sendMessage(ColorsUtil.translate.apply("Arguments don't matches ! usage : /specialitems (type) (player)"));
            return false;
        }

        final String itemType = args[0];
        final Player player = Bukkit.getPlayer(args[1]);


        if(!containsItem(itemType)){
            sender.sendMessage(ColorsUtil.translate.apply("Please, specifiy a correct item"));

            return false;
        }

        if(player == null){
            sender.sendMessage(ColorsUtil.translate.apply("Player unable to receive the item"));
            return false;
        }

        switch (itemType) {
            case "axe" -> player.getInventory().addItem(itemsManager.getAxe());
            case "rod" -> player.getInventory().addItem(itemsManager.getRod());
            case "pickaxe" -> player.getInventory().addItem(itemsManager.getPick());
        }

        return true;
    }

    private boolean containsItem(final String itemType){
        for (ItemsEnum type : ItemsEnum.values()){
            if(type.getId().equals(itemType)){
                return true;
            }
        }
        return false;
    }
}
