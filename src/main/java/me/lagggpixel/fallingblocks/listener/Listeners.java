package me.lagggpixel.fallingblocks.listener;

import me.lagggpixel.fallingblocks.FallingBlocks;
import me.lagggpixel.fallingblocks.controller.BlocksController;
import me.lagggpixel.fallingblocks.explosion.Explosion;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

public class Listeners implements Listener {
    private FallingBlocks plugin;

    public Listeners(FallingBlocks plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getPluginManager().registerEvents(this, (Plugin)this.plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        Location location = e.getLocation();
        Claim claim = this.plugin.getGriefPrevention().dataStore.getClaimAt(location, true, null);


        if (claim != null && claim.areExplosivesAllowed) {
            if (e.getEntity().getType().equals(EntityType.PRIMED_TNT) || e.getEntity().getType().equals(EntityType.MINECART_TNT)) {
                e.setCancelled(false);
                return;
            }
        }

        BlocksController controller = this.plugin.getBlocksController();
        if (!controller.doDrops())
            e.setYield(0.0F);
        controller.createExplosion(e.blockList(), e.getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {

        Block block = e.getBlock();
        Claim claim = this.plugin.getGriefPrevention().dataStore.getClaimAt(block.getLocation(), true, null);
        System.out.println(claim);
        if (claim != null && claim.areExplosivesAllowed) {
            e.setCancelled(false);
            return;
        }

        BlocksController controller = this.plugin.getBlocksController();
        if (!controller.doDrops())
            e.setYield(0.0F);
        controller.createExplosion(e.blockList(), e.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        Block block = e.getBlock();
        for (Explosion explosion : this.plugin.getBlocksController().getExplosions()) {
            if (explosion.getFallingBlocks().contains(e.getEntity())) {
                e.setCancelled(true);
                explosion.getFallingBlocks().remove(e.getEntity());
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, ((FallingBlock)e
                        .getEntity()).getBlockData().getMaterial());
            }
        }
    }
}
