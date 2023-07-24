package me.lagggpixel.fallingblocks.controller;

import java.util.ArrayList;
import java.util.List;

import me.lagggpixel.fallingblocks.FallingBlocks;
import me.lagggpixel.fallingblocks.explosion.Explosion;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

public class BlocksController {
    private FallingBlocks plugin;

    private ArrayList<Explosion> explosions;

    private boolean particles;

    private int delay;

    private int interval;

    private int spawnChance;

    private boolean randomRespawn;

    private boolean drops;

    private boolean regenTerrain;

    private boolean spawnFallingBlocks;

    public BlocksController(FallingBlocks plugin) {
        this.plugin = plugin;
        this.explosions = new ArrayList<>();
    }

    public void start() {
        this.plugin.saveDefaultConfig();
        FileConfiguration config = this.plugin.getConfig();
        this.particles = config.getBoolean("particles", true);
        this.delay = config.getInt("delay", 5);
        this.interval = config.getInt("interval", 20);
        this.spawnChance = config.getInt("spawn chance", 30);
        this.randomRespawn = config.getBoolean("random respawn rate", true);
        this.drops = config.getBoolean("drops", true);
        this.regenTerrain = config.getBoolean("regen terrain", true);
        this.spawnFallingBlocks = config.getBoolean("falling blocks", true);
    }

    public void createExplosion(List<Block> blockList, Location center) {
        Explosion explosion = new Explosion(this, blockList, center);
        this.explosions.add(explosion);
        explosion.explode(this.particles, this.delay, this.interval, this.spawnChance, this.randomRespawn, this.regenTerrain, this.spawnFallingBlocks);
    }

    public FallingBlocks getPlugin() {
        return this.plugin;
    }

    public ArrayList<Explosion> getExplosions() {
        return this.explosions;
    }

    public boolean doDrops() {
        return this.drops;
    }
}
