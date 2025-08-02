package fr.wivern.gambling.util.worldguard.listener;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.util.worldguard.events.RegionEnterEvent;
import fr.wivern.gambling.util.worldguard.events.RegionEnteredEvent;
import fr.wivern.gambling.util.worldguard.events.RegionLeaveEvent;
import fr.wivern.gambling.util.worldguard.events.RegionLeftEvent;
import fr.wivern.gambling.util.worldguard.movement.MovementWay;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class WGRegionEventsListener implements Listener {
    private WorldGuardPlugin wgPlugin;
    private Gambling plugin;
    private Map<Player, Set<ProtectedRegion>> playerRegions;

    public WGRegionEventsListener(Gambling plugin, WorldGuardPlugin wgPlugin) {
        this.plugin = plugin;
        this.wgPlugin = wgPlugin;
        this.playerRegions = new HashMap();
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Set<ProtectedRegion> regions = (Set)this.playerRegions.remove(e.getPlayer());
        if (regions != null) {
            Iterator var3 = regions.iterator();

            while(var3.hasNext()) {
                ProtectedRegion region = (ProtectedRegion)var3.next();
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                this.plugin.getServer().getPluginManager().callEvent(leaveEvent);
                this.plugin.getServer().getPluginManager().callEvent(leftEvent);
            }
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Set<ProtectedRegion> regions = (Set)this.playerRegions.remove(e.getPlayer());
        if (regions != null) {
            Iterator var3 = regions.iterator();

            while(var3.hasNext()) {
                ProtectedRegion region = (ProtectedRegion)var3.next();
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                this.plugin.getServer().getPluginManager().callEvent(leaveEvent);
                this.plugin.getServer().getPluginManager().callEvent(leftEvent);
            }
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        e.setCancelled(this.updateRegions(e.getPlayer(), MovementWay.MOVE, e.getTo(), e));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getPlayer().getLocation(), e);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        this.updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getRespawnLocation(), e);
    }

    private synchronized boolean updateRegions(Player player, MovementWay movement, Location to, PlayerEvent event) {
        HashSet regions;
        if (this.playerRegions.get(player) == null) {
            regions = new HashSet();
        } else {
            regions = new HashSet((Collection)this.playerRegions.get(player));
        }

        Set<ProtectedRegion> oldRegions = new HashSet(regions);
        RegionManager rm = this.wgPlugin.getRegionManager(to.getWorld());
        if (rm == null) {
            return false;
        } else {
            HashSet<ProtectedRegion> appRegions = new HashSet(rm.getApplicableRegions(to).getRegions());
            ProtectedRegion globalRegion = rm.getRegion("__global__");
            if (globalRegion != null) {
                appRegions.add(globalRegion);
            }

            Iterator itr = appRegions.iterator();

            ProtectedRegion region;
            while(itr.hasNext()) {
                region = (ProtectedRegion)itr.next();
                if (!regions.contains(region)) {
                    RegionEnterEvent e = new RegionEnterEvent(region, player, movement, event);
                    this.plugin.getServer().getPluginManager().callEvent(e);
                    if (e.isCancelled()) {
                        regions.clear();
                        regions.addAll(oldRegions);
                        return true;
                    }

                    final ProtectedRegion finalRegion = region;
                    final Player finalPlayer = player;
                    final MovementWay finalMovement = movement;
                    final PlayerEvent finalEvent = event;

                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        RegionEnteredEvent e1 = new RegionEnteredEvent(finalRegion, finalPlayer, finalMovement, finalEvent);
                        this.plugin.getServer().getPluginManager().callEvent(e1);
                    }, 1L);
                    regions.add(region);
                }
            }

            itr = regions.iterator();

            while(itr.hasNext()) {
                region = (ProtectedRegion)itr.next();
                if (!appRegions.contains(region)) {
                    if (rm.getRegion(region.getId()) != region) {
                        itr.remove();
                    } else {
                        RegionLeaveEvent e2 = new RegionLeaveEvent(region, player, movement, event);
                        this.plugin.getServer().getPluginManager().callEvent(e2);
                        if (e2.isCancelled()) {
                            regions.clear();
                            regions.addAll(oldRegions);
                            return true;
                        }

                        final ProtectedRegion finalRegion = region;
                        final Player finalPlayer = player;
                        final MovementWay finalMovement = movement;
                        final PlayerEvent finalEvent = event;

                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                            RegionLeftEvent e = new RegionLeftEvent(finalRegion, finalPlayer, finalMovement, finalEvent);
                            this.plugin.getServer().getPluginManager().callEvent(e);
                        }, 1L);
                        itr.remove();
                    }
                }
            }

            this.playerRegions.put(player, regions);
            return false;
        }
    }
}
