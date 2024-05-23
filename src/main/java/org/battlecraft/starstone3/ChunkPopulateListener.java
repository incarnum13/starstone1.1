package org.battlecraft.starstone3;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class ChunkPopulateListener implements Listener {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    public ChunkPopulateListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        if (random.nextDouble() < 0.05) { // 5% chance
            int x = random.nextInt(16);
            int z = random.nextInt(16);
            int y = event.getChunk().getWorld().getHighestBlockYAt(x, z) - 1; // Adjust to place on the ground
            Block block = event.getChunk().getBlock(x, y, z);
            block.setType(Material.DIAMOND_BLOCK);
            block.setMetadata(Starstone3.STARSTONE_METADATA_KEY, new FixedMetadataValue(plugin, true));
        }
    }
}
