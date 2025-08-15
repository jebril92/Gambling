package fr.wivern.gambling.listener;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.data.PlayerData;
import fr.wivern.gambling.util.config.Base64Save;
import fr.wivern.gambling.util.config.Config;
import fr.wivern.gambling.util.save.PlayerInv;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class GamblingListener implements Listener {
    private Gambling gambling;
    private Config config;

    public GamblingListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onDie(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer;
        if (victim.getKiller() != null) {
            killer = victim.getKiller();
            if (!this.gambling.getMatchManager().getPlayerMatchMap().containsKey(victim) || !this.gambling.getMatchManager().getPlayerMatchMap().containsKey(killer)) {
                return;
            }

            int stat = victim.getStatistic(Statistic.DEATHS);
            int statKills = victim.getStatistic(Statistic.PLAYER_KILLS);
            victim.setStatistic(Statistic.DEATHS, Math.max(stat - 1, 0));
            killer.setStatistic(Statistic.PLAYER_KILLS, Math.max(statKills - 1, 0));
            event.getDrops().clear();
            event.setDeathMessage((String)null);
            this.gambling.getServer().getScheduler().runTaskLater(this.gambling, () -> {
                victim.spigot().respawn();
            }, 5L);
            this.gambling.getServer().getScheduler().runTaskLater(this.gambling, () -> {
                this.gambling.getMatchManager().endMatch(killer, victim);
            }, 20L);
        } else {
            if (!this.gambling.getMatchManager().getPlayerMatchMap().containsKey(victim)) {
                return;
            }

            killer = (Player)this.gambling.getMatchManager().getPlayerMatchMap().get(victim);
            event.getDrops().clear();
            this.gambling.getServer().getScheduler().runTaskLater(this.gambling, () -> {
                victim.spigot().respawn();
            }, 5L);
            this.gambling.getServer().getScheduler().runTaskLater(this.gambling, () -> {
                this.gambling.getMatchManager().endMatch(killer, victim);
            }, 20L);
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Iterator var3 = this.gambling.getMatchManager().getHidePlayer().iterator();

        while(var3.hasNext()) {
            Player players = (Player)var3.next();
            if (players != player) {
                players.hidePlayer(player);
            }
        }

    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
            // Seulement pour l'argent maintenant
            this.gambling.getEconomy().depositPlayer(player, ((PlayerData)this.gambling.getMatchManager().getPlayerWaitMap().get(player)).getMoney());
            this.gambling.getMatchManager().getPlayerWaitMap().remove(player);
        }

        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            Player target = (Player)this.gambling.getMatchManager().getPlayerMatchMap().get(player);
            this.config = new Config(this.gambling, player.getName(), "players");
            this.config.set("armor", Base64Save.itemStackArrayToBase64(((PlayerInv)this.gambling.getMatchManager().getPlayerInventory().get(player.getUniqueId())).getArmorContents()));
            this.config.set("stuff", Base64Save.itemStackArrayToBase64(((PlayerInv)this.gambling.getMatchManager().getPlayerInventory().get(player.getUniqueId())).getContents()));
            this.config.save("players");
            this.gambling.getMatchManager().endMatch(target, player);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            event.setCancelled(true);
            player.sendMessage(this.gambling.getConfigManager().getString("CANNOT-LAUNCH-ITEM"));
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.gambling.getSaveManager().loadStuffWait(player);
        this.gambling.getSaveManager().loadStuffMatch(player);
        Location location = player.getLocation().getWorld().getSpawnLocation();
        location.getChunk().unload();
        location.getChunk().load();
        File fileplayer = this.gambling.getFilePlayersStorage();
        File fileCheckPlayer = new File(fileplayer, player.getName() + ".yml");
        if (fileCheckPlayer.exists()) {
            this.config = new Config(this.gambling, player.getName(), "players");
            this.gambling.getServer().getScheduler().runTaskLaterAsynchronously(this.gambling, () -> {
                this.gambling.getMatchManager().endConnexion(player, this.config);
                player.teleport(location);
            }, 20L);
            this.config.deleteFile(player.getName(), "players");
        }

        File fileStuff = this.gambling.getStoreItem();
        File fileCheckStuff = new File(fileStuff, player.getName() + ".yml");
        if (fileCheckStuff.exists()) {
            this.config = new Config(this.gambling, player.getName(), "items");

            try {
                ItemStack[] stuff = Base64Save.itemStackArrayFromBase64(this.config.getString("bet"));
                player.getInventory().setContents(stuff);
                player.updateInventory();
            } catch (IOException var9) {
                var9.printStackTrace();
            }

            this.config.deleteFile(player.getName(), "items");
        }

    }

    @EventHandler
    public void onLostFeed(FoodLevelChangeEvent event) {
        HumanEntity humanEntity = event.getEntity();
        if (humanEntity instanceof Player) {
            Player player = (Player)humanEntity;
            if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
                event.setCancelled(true);
            }
        }

    }
}
