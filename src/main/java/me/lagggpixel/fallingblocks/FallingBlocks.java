package me.lagggpixel.fallingblocks;

import me.lagggpixel.fallingblocks.controller.BlocksController;
import me.lagggpixel.fallingblocks.listener.Listeners;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.plugin.java.JavaPlugin;

public class FallingBlocks extends JavaPlugin {

    private GriefPrevention griefPrevention;
    private BlocksController blocksController;

    private Listeners listeners;

    public void onEnable() {
        griefPrevention = GriefPrevention.instance;
        this.blocksController = new BlocksController(this);
        this.listeners = new Listeners(this);
        this.blocksController.start();
        this.listeners.start();
    }

    public BlocksController getBlocksController() {
        return this.blocksController;
    }

    public Listeners getListeners() {
        return this.listeners;
    }

    public GriefPrevention getGriefPrevention() {
        return griefPrevention;
    }
}
