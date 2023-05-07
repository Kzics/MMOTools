package fr.tools.listeners;

import fr.tools.ToolsMain;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PickListeners implements Listener {

    private static final int PROGRESS_BAR_WIDTH = 10;

    private final ToolsMain instance;

    public PickListeners(final ToolsMain instance){
        this.instance = instance;

    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final ItemStack item = e.getItem();
        final Block block = e.getClickedBlock();

        if (block != null && block.getType().equals(Material.DIAMOND_ORE) && item.getType().name().endsWith("PICKAXE")) {
            if (instance.getBreakers().contains(player.getUniqueId())) return;

            instance.getBreakers().add(player.getUniqueId());

            Location progressBarLocation = e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
            ArmorStand progressBar = progressBarLocation.getWorld().spawn(progressBarLocation, ArmorStand.class);
            progressBar.setGravity(false);
            progressBar.setVisible(false);
            progressBar.setInvulnerable(true);
            progressBar.setMarker(true);
            progressBar.setCustomNameVisible(true);
            progressBar.setCustomName(getProgressBar(0));
            new BukkitRunnable() {
                int progress = 0;
                final List<Block> blocksToRestore = new ArrayList<>();
                final Block clickedBlock = player.getTargetBlockExact(5, FluidCollisionMode.ALWAYS);

                @Override
                public void run() {

                    final Block targetBlock = player.getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
                    if(targetBlock == null){
                        progressBar.remove();
                        cancel();
                        return;
                    }
                    if (player.getGameMode() == GameMode.SURVIVAL && player.isOnline() && clickedBlock.getLocation().equals(targetBlock.getLocation())
                            && player.getInventory().getItemInMainHand().getType().name().endsWith("PICKAXE")) {
                        // increment the progress
                        progress++;

                        ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0));

                        // update the progress bar
                        progressBar.setCustomName(getProgressBar(progress));

                        // check if the progress is full
                        if (progress == 10) {
                            // add the block to the list of blocks to restore

                            blocksToRestore.add(clickedBlock);

                            // break the block
                            clickedBlock.setType(Material.AIR);
                            cancel();

                            // reset the progress and progress bar
                            progress = 0;
                            progressBar.setCustomName(getProgressBar(progress));
                            progressBar.remove();
                            instance.getBreakers().remove(player.getUniqueId());

                            ToolsMain.giveExperience(player,blocksToRestore.size());


                        }
                    } else {
                        // cancel the task
                        cancel();
                        progressBar.remove();
                        instance.getBreakers().remove(player.getUniqueId());



                        // restore the blocks after 30 seconds
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Block blockData : blocksToRestore) {
                                    Block block = clickedBlock.getWorld().getBlockAt(clickedBlock.getLocation());
                                    block.setType(blockData.getType());
                                }
                            }
                        }.runTaskLater(instance, 100); // 600 ticks = 30 seconds
                    }
                }
            }.runTaskTimer(instance, 0, 20); // run the task every 2 ticks (0.1 seconds)


        }
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
