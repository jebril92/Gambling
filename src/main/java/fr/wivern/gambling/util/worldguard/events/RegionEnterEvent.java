package fr.wivern.gambling.util.worldguard.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.wivern.gambling.util.worldguard.movement.MovementWay;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;

public class RegionEnterEvent extends RegionEvent implements Cancellable {
    private boolean cancelled = false;
    private boolean cancellable = true;

    public RegionEnterEvent(ProtectedRegion region, Player player, MovementWay movement, PlayerEvent parent) {
        super(region, player, movement, parent);
        if (movement == MovementWay.SPAWN || movement == MovementWay.DISCONNECT) {
            this.cancellable = false;
        }

    }

    public void setCancelled(boolean cancelled) {
        if (this.cancellable) {
            this.cancelled = cancelled;
        }
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isCancellable() {
        return this.cancellable;
    }

    protected void setCancellable(boolean cancellable) {
        if (!(this.cancellable = cancellable)) {
            this.cancelled = false;
        }

    }
}
