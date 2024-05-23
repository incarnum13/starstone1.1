package org.battlecraft.starstone3;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class Starstone3 extends JavaPlugin {

    public static final String STARSTONE_METADATA_KEY = "starstone";

    @Override
    public void onEnable() {
        getLogger().info("Starstone3 has been enabled.");
        getServer().getPluginManager().registerEvents(new StarstoneListener(this), this);
        getServer().getPluginManager().registerEvents(new ChunkPopulateListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Starstone3 has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("summonstarstone")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Block block = player.getLocation().getBlock();
                block.setType(Material.DIAMOND_BLOCK); // Using Diamond Block as Starstone
                block.setMetadata(STARSTONE_METADATA_KEY, new FixedMetadataValue(this, true));
                player.sendMessage("A Starstone has been summoned at your location!");
                getLogger().info("Starstone summoned at " + block.getLocation());
                return true;
            }
        }
        return false;
    }
}
