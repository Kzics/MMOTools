package fr.tools.listeners;

import fr.tools.ToolsMain;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class AxeListeners implements Listener {

    private static final int PROGRESS_BAR_WIDTH = 10;

    private final ToolsMain instance;

    public AxeListeners(final ToolsMain instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType().equals(Material.OAK_LOG) && item.getType().name().endsWith("AXE")) {
                if(instance.getBreakers().contains(player.getUniqueId())) return;

                instance.getBreakers().add(player.getUniqueId());
                // create and spawn the progress bar armor stand
                Location progressBarLocation = block.getLocation().add(0.5, 0.5, 0.5);
                ArmorStand progressBar = progressBarLocation.getWorld().spawn(progressBarLocation, ArmorStand.class);
                progressBar.setGravity(false);
                progressBar.setVisible(false);
                progressBar.setInvulnerable(true);
                progressBar.setMarker(true);
                progressBar.setCustomNameVisible(true);
                progressBar.setCustomName(getProgressBar(0));

                // create and start the breaking task
                new BukkitRunnable() {
                    int progress = 0;
                    final List<Block> blocksToBreak = new ArrayList<>();
                    final Map<Location, Material> blocks = new HashMap<>();

                    @Override
                    public void run() {
                        final Block targetBlock = player.getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
                        // check if the player is still breaking the same block
                        if(targetBlock == null){
                            progressBar.remove();
                            cancel();
                            return;
                        }
                        if (player.getGameMode() == GameMode.SURVIVAL && player.isOnline() && block.getLocation().equals(targetBlock.getLocation())
                        && player.getInventory().getItemInMainHand().getType().name().endsWith("AXE")) {
                            // increment the progress
                            progress++;
                            // update the progress bar
                            progressBar.setCustomName(getProgressBar(progress));
                            // check if the progress is full
                            if (progress == 10) {
                                // get all the blocks to break
                                Queue<Block> queue = new LinkedList<>();
                                queue.add(block);

                                while (!queue.isEmpty()) {
                                    Block currentBlock = queue.remove();
                                    if (currentBlock.getType() == Material.OAK_LOG || currentBlock.getType() == Material.OAK_LEAVES) {
                                        if (currentBlock.getY() >= block.getY()) { // only add blocks above the clicked block
                                             blocksToBreak.add(currentBlock);
                                             blocks.put(currentBlock.getLocation(),currentBlock.getType());

                                        for (int dx = -1; dx <= 1; dx++) {
                                            for (int dy = -1; dy <= 1; dy++) {
                                                for (int dz = -1; dz <= 1; dz++) {
                                                    if (dx == 0 && dy == 0 && dz == 0) continue;
                                                    Block nearbyBlock = currentBlock.getRelative(dx, dy, dz);
                                                    if (nearbyBlock.getType() == Material.OAK_LOG || nearbyBlock.getType() == Material.OAK_LEAVES) {
                                                        if (!blocksToBreak.contains(nearbyBlock) && !queue.contains(nearbyBlock)) {
                                                            queue.add(nearbyBlock);
                                                        }
                                                        System.out.println(currentBlock.getY());
                                                    }
                                                }
                                            }
                                        }
                                        }
                                    }
                                }

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        for (Map.Entry<Location,Material> entry : blocks.entrySet()) {
                                            block.getWorld().getBlockAt(entry.getKey())
                                                    .setType(entry.getValue());
                                        }
                                    }
                                }.runTaskLater(instance, 100L);

                                // break all the blocks
                                for (Block blockToBreak : blocksToBreak) {
                                    blockToBreak.setType(Material.AIR);
                                }

                                // send attack animation packet to the player
                                // remove the progress bar
                                progressBar.remove();
                                ToolsMain.giveExperience(player,blocksToBreak.size());

                                // cancel the task
                                cancel();
                                instance.getBreakers().remove(player.getUniqueId());

                                Location successArmorStandLocation = player.getLocation().add(new Vector(1,0,0));
                                ArmorStand successArmorStand = successArmorStandLocation.getWorld().spawn(successArmorStandLocation, ArmorStand.class);
                                successArmorStand.setGravity(false);
                                successArmorStand.setVisible(false);
                                successArmorStand.setInvulnerable(true);
                                successArmorStand.setCustomNameVisible(true);
                                successArmorStand.setCustomName(ChatColor.GOLD +" " + blocksToBreak.size() + " Experience");
                                // schedule the success armor stand to be removed after 3 seconds
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        successArmorStand.remove();
                                    }
                                }.runTaskLater(instance, 60L);

                            }

                            ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0));
                        } else {
                            // remove the progress bar
                            progressBar.remove();
                            instance.getBreakers().remove(player.getUniqueId());

                            // cancel the task
                            cancel();
                        }                        // send attack animation packet to the player every second
                    }
                }.runTaskTimer(instance, 0L, 20L);
            }
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
