package org.battlecraft.starstone3;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StarstoneListener implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, Integer> miningProgress = new HashMap<>();
    private final Map<UUID, Block> miningBlock = new HashMap<>();

    public StarstoneListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        UUID playerId = player.getUniqueId();

        if (block.getType() == Material.DIAMOND_BLOCK && isStarstone(block)) {
            if (itemInHand.getType() == Material.DIAMOND_PICKAXE) {
                event.setCancelled(true);
                plugin.getLogger().info("BlockDamageEvent: Starstone damage event detected. Player: " + player.getName());

                if (!miningBlock.containsKey(playerId) || miningBlock.get(playerId) != block) {
                    miningProgress.put(playerId, 0);
                    miningBlock.put(playerId, block);
                    player.sendMessage("Started mining the Starstone...");
                    plugin.getLogger().info("Started mining the Starstone. Player: " + player.getName());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!miningBlock.containsKey(playerId)) {
                                plugin.getLogger().info("Stopped mining the Starstone because the player is not in the miningBlock map. Player: " + player.getName());
                                this.cancel();
                                return;
                            }

                            if (miningBlock.get(playerId) != block) {
                                plugin.getLogger().info("Stopped mining the Starstone because the mining block has changed. Player: " + player.getName());
                                this.cancel();
                                return;
                            }

                            if (!player.isOnline()) {
                                plugin.getLogger().info("Stopped mining the Starstone because the player is not online. Player: " + player.getName());
                                this.cancel();
                                return;
                            }

                            if (player.getInventory().getItemInMainHand().getType() != Material.DIAMOND_PICKAXE) {
                                plugin.getLogger().info("Stopped mining the Starstone because the player is not holding a diamond pickaxe. Player: " + player.getName());
                                this.cancel();
                                return;
                            }

                            Set<Material> transparent = null;
                            Block targetBlock = player.getTargetBlock(transparent, 5);
                            if (targetBlock == null || !targetBlock.equals(block)) {
                                plugin.getLogger().info("Stopped mining the Starstone because the player is not targeting the block. Player: " + player.getName());
                                this.cancel();
                                return;
                            }

                            int progress = miningProgress.get(playerId);
                            plugin.getLogger().info("Mining progress: " + progress + " ticks. Player: " + player.getName());
                            if (progress >= 100) { // 5 seconds (20 ticks per second * 5)
                                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.DIAMOND));
                                player.sendMessage("You have mined a Starstone and received a diamond!");
                                plugin.getLogger().info("Starstone mined successfully. Player: " + player.getName());
                                miningProgress.put(playerId, 0);
                            } else {
                                miningProgress.put(playerId, progress + 1);
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 1L);
                }
            } else {
                player.sendMessage("You need a diamond pickaxe to mine the Starstone.");
                plugin.getLogger().info("Player tried to mine with a non-diamond pickaxe. Player: " + player.getName());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.DIAMOND_BLOCK && isStarstone(block)) {
            event.setCancelled(true);
            plugin.getLogger().info("BlockBreakEvent: Starstone break event detected and cancelled.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        miningProgress.remove(playerId);
        miningBlock.remove(playerId);
        plugin.getLogger().info("PlayerQuitEvent: Player " + event.getPlayer().getName() + " quit. Removed from mining tracking.");
    }

    private boolean isStarstone(Block block) {
        for (MetadataValue value : block.getMetadata(Starstone3.STARSTONE_METADATA_KEY)) {
            if (value.asBoolean()) {
                return true;
            }
        }
        return false;
    }
}
