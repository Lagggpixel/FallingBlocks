package me.lagggpixel.fallingblocks.explosion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import me.lagggpixel.fallingblocks.controller.BlocksController;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Explosion {
    private final BlocksController blockController;

    private final ArrayList<BlockState> explodedBlocks;

    private final ArrayList<BlockState> dropTypeBlocks;

    private final ArrayList<FallingBlock> fallingBlocks;

    private final Location center;

    private double radius = 1.0D;

    public Explosion(BlocksController blockController, List<Block> blockList, Location center) {
        this.blockController = blockController;
        this.explodedBlocks = new ArrayList<>();
        this.dropTypeBlocks = new ArrayList<>();
        this.fallingBlocks = new ArrayList<>();
        this.center = center;
        setupExplosion(blockList);
    }

    private void setupExplosion(List<Block> blockList) {
        double maxRadius = 1.0D;
        for (int i = 0; i < blockList.size(); i++) {
            Block block = blockList.get(i);
            BlockState state = block.getState();
            double distanceSquared = state.getLocation().distanceSquared(this.center);
            if (distanceSquared > maxRadius)
                maxRadius = distanceSquared;
            this.radius = Math.sqrt(maxRadius);
            block.setType(Material.AIR, false);
            this.explodedBlocks.add(state);
        }
        this.explodedBlocks.sort(Comparator.comparingInt(BlockState::getY));
    }

    public void explode(boolean particles, int delay, int interval, int spawnChance, boolean randomRespawn, boolean regenTerrain, boolean spawnFallingBlocks) {
        if (spawnFallingBlocks)
            createFallingBlocks(spawnChance);
        if (regenTerrain)
            startBlockRegen(particles, delay, interval, randomRespawn);
    }

    private void createFallingBlocks(int spawnChance) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (BlockState blockState : this.explodedBlocks) {
            if (blockState.getType().isBlock() && random.nextInt(1, 101) <= spawnChance) {
                Location fallingBlockLoc = blockState.getLocation();
                FallingBlock fallingBlock = fallingBlockLoc.getWorld().spawnFallingBlock(fallingBlockLoc, blockState.getData());
                fallingBlock.setVelocity(getNewFallingBlockVector(fallingBlockLoc));
                fallingBlock.setDropItem(false);
                fallingBlock.setHurtEntities(false);
                fallingBlock.setInvulnerable(true);
                fallingBlock.setSilent(true);
                this.fallingBlocks.add(fallingBlock);
            }
        }
    }

    private void startBlockRegen(boolean particles, int delay, int interval, final boolean randomSpawn) {
        (new BukkitRunnable() {
            int index = 0;

            ThreadLocalRandom random = ThreadLocalRandom.current();

            public void run() {
                if (this.index >= Explosion.this.explodedBlocks.size()) {
                    for (BlockState state : Explosion.this.dropTypeBlocks)
                        state.update(true, false);
                    Explosion.this.fallingBlocks.clear();
                    Explosion.this.removeExplosion();
                    cancel();
                    return;
                }
                if (randomSpawn) {
                    if (this.random.nextInt(1, 11) < 7)
                        Explosion.this.regenBlock(Explosion.this.explodedBlocks.get(this.index++));
                } else {
                    Explosion.this.regenBlock(Explosion.this.explodedBlocks.get(this.index++));
                }
            }
        }).runTaskTimer((Plugin)this.blockController.getPlugin(), (delay * 20), interval);
    }

    private Vector getNewFallingBlockVector(Location blockLocation) {
        Vector blockDirection = blockLocation.toVector().subtract(this.center.toVector());
        double divide = this.radius / blockDirection.lengthSquared();
        blockDirection.divide(new Vector(divide, divide, divide));
        blockDirection.setY(Math.abs(blockDirection.getY()));
        return blockDirection.normalize();
    }

    private void regenBlock(BlockState blockState) {
        if (!blockState.getType().isBlock()) {
            this.dropTypeBlocks.add(blockState);
        } else {
            Location stateLocation = blockState.getLocation();
            blockState.update(true, false);
            stateLocation.getWorld().playEffect(stateLocation, Effect.STEP_SOUND, blockState.getType());
        }
    }

    private void removeExplosion() {
        this.blockController.getExplosions().remove(this);
    }

    public ArrayList<FallingBlock> getFallingBlocks() {
        return this.fallingBlocks;
    }
}
