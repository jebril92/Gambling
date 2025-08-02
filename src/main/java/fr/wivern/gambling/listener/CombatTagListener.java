package fr.wivern.gambling.listener;

import fr.wivern.gambling.Gambling;
import net.minelink.ctplus.event.PlayerCombatTagEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CombatTagListener implements Listener {
    private final Gambling gambling;

    public CombatTagListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onCombat(PlayerCombatTagEvent event) {
        Player player = event.getPlayer();
        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            event.setCancelled(true);
            event.setTagDuration(0);
        }

    }
}
