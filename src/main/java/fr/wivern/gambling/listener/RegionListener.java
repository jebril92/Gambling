package fr.wivern.gambling.listener;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.util.worldguard.events.RegionEnterEvent;
import fr.wivern.gambling.util.worldguard.events.RegionLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegionListener implements Listener {
    private Gambling gambling;

    public RegionListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onRegionEntry(RegionEnterEvent event) {
        if (event.getRegion().getId().equalsIgnoreCase(this.gambling.getArenaConfig().getString("ARENA-NAME")) && event.isCancellable() && !event.getPlayer().hasPermission(this.gambling.getArenaConfig().getString("PERMISSION-BYPASS")) && !this.gambling.getMatchManager().getPlayerMatchMap().containsKey(event.getPlayer())) {
            event.getPlayer().sendMessage(this.gambling.getConfigManager().getString("ARENA-ENTER-DENY"));
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onRegionEntry(RegionLeaveEvent event) {
        if (event.getRegion().getId().equalsIgnoreCase(this.gambling.getConfigManager().getString("LEAVE-REGION-REMOVE-PLAYER")) && event.isCancellable() && this.gambling.getMatchManager().getPlayerWaitMap().containsKey(event.getPlayer())) {
            this.gambling.getMatchManager().getPlayerWaitMap().remove(event.getPlayer());
            event.getPlayer().sendMessage(this.gambling.getConfigManager().getString("LEAVE-GAMBLING"));
            event.setCancelled(true);
        }

        if (event.getRegion().getId().equalsIgnoreCase(this.gambling.getArenaConfig().getString("ARENA-NAME")) && event.isCancellable() && this.gambling.getMatchManager().getPlayerMatchMap().containsKey(event.getPlayer())) {
            event.getPlayer().sendMessage(this.gambling.getConfigManager().getString("ARENA-ENTER-DENY"));
            event.setCancelled(true);
        }

    }
}
