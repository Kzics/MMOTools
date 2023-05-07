package fr.tools.listeners;

import fr.tools.ToolsMain;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RodListeners implements Listener {

    private static final int PROGRESS_BAR_WIDTH = 10;
    private final ToolsMain instance;
    public RodListeners(final ToolsMain toolsMain){
        this.instance = toolsMain;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
            Entity clickedEntity = event.getRightClicked();
            if (clickedEntity instanceof ArmorStand) {
                if(clickedEntity.hasMetadata("fishing")) {
                    if(!instance.getFishingAreaManager().isFisheable(clickedEntity.getLocation())) return;

                    startFishing(player, clickedEntity);
                    }
            }
        }
    }

    private void startFishing(Player player, Entity armorStand) {
        Location progressBarLocation = armorStand.getLocation().add(0.5, 0.5, 0.5);
        ArmorStand progressBar = progressBarLocation.getWorld().spawn(progressBarLocation, ArmorStand.class);
        progressBar.setGravity(false);
        progressBar.setVisible(false);
        progressBar.setInvulnerable(true);
        progressBar.setMarker(true);
        progressBar.setCustomNameVisible(true);
        progressBar.setCustomName(getProgressBar(0));

        List<Location> fishLocations = new ArrayList<>();

        int maxProgress = 10;
        final int[] progress = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || player.getInventory().getItemInMainHand().getType() != Material.FISHING_ROD ||
                 player.getLocation().distance(armorStand.getLocation()) > 5) {
                    progressBar.remove();
                    cancel();
                    return;
                }
                progress[0]++;
                progressBar.setCustomName(getProgressBar(progress[0]));

                if (progress[0] >= maxProgress) {
                    List<Entity> entities = armorStand.getNearbyEntities(5, 5, 5);
                    for (Entity entity : entities) {
                        if (entity instanceof Fish) {
                            fishLocations.add(entity.getLocation());
                            entity.remove();
                        }
                    }

                    new BukkitRunnable(){

                        @Override
                        public void run() {
                            fishLocations.forEach(l->l.getWorld().spawnEntity(l,EntityType.TROPICAL_FISH));
                            instance.getFishingAreaManager().setFisheableState(armorStand.getLocation().add(0,-1,0),true);

                            System.out.println("autre");
                            System.out.println(armorStand.getLocation());
                            instance.getFishingAreaManager().getStateStand(armorStand.getLocation().add(0,-1,0))
                                    .setCustomName("Can Fish");

                        }
                    }.runTaskLater(instance,100L);

                    progressBar.remove();
                    cancel();

                    instance.getFishingAreaManager().setFisheableState(armorStand.getLocation().add(0,-1,0),false);

                    ToolsMain.giveExperience(player,fishLocations.size());

                    instance.getFishingAreaManager().getStateStand(armorStand.getLocation().add(0,-1,0))
                            .setCustomName("Can't Fish");
                }
                ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0));

            }
        }.runTaskTimer(instance, 0, 20);
    }


    private String getProgressBar(int progress) {
        StringBuilder sb = new StringBuilder();
        int filledWidth = progress * PROGRESS_BAR_WIDTH / 10;
        int emptyWidth = PROGRESS_BAR_WIDTH - filledWidth;
        for (int i = 0; i < filledWidth; i++) {
            sb.append("§a█");
        }
        for (int i = 0; i < emptyWidth; i++) {
            sb.append("§8█");
        }
        return sb.toString();
    }
}
