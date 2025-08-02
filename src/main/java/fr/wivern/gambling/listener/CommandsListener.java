package fr.wivern.gambling.listener;

import fr.wivern.gambling.Gambling;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandsListener implements Listener {
    private Gambling gambling;

    public CommandsListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void processCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            this.gambling.getAllowedManager().getCommands().stream().filter((s) -> {
                return !event.getMessage().startsWith(s);
            }).forEach((s) -> {
                event.setCancelled(true);
                player.sendMessage(this.gambling.getConfigManager().getString("COMMAND-DISABLED"));
            });
        }

    }
}
