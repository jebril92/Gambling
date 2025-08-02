package fr.wivern.gambling.listener;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.PowerLossEvent;
import fr.wivern.gambling.Gambling;
import java.util.Objects;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FactionListener implements Listener {
    private final Gambling gambling;

    public FactionListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onLostPower(PowerLossEvent event) {
        Player player = event.getfPlayer().getPlayer();
        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            event.setCancelled(true);
            event.setMessage((String)null);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onDuelDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity attacker = event.getDamager();
        if (victim instanceof Player && attacker instanceof Player) {
            Player playerVictim = (Player)victim;
            Player playerAttacker = (Player)event.getDamager();
            if (!this.gambling.getMatchManager().getPlayerMatchMap().containsKey(playerAttacker)) {
                return;
            }

            Faction attackerFaction = FPlayers.getInstance().getByPlayer(playerAttacker).getFaction();
            Faction victimFaction = FPlayers.getInstance().getByPlayer(playerVictim).getFaction();
            if (Objects.equals(attackerFaction, victimFaction)) {
                event.setCancelled(false);
            }
        }

    }
}
