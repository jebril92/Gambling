package fr.wivern.gambling.match;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.data.PlayerData;
import fr.wivern.gambling.util.ParseLoc;
import fr.wivern.gambling.util.config.Base64Save;
import fr.wivern.gambling.util.config.Config;
import fr.wivern.gambling.util.save.InventoryUtils;
import fr.wivern.gambling.util.save.PlayerInv;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MatchManager {
    private final Gambling gambling;
    private final ConcurrentHashMap<Player, Player> playerMatchMap;
    private final ConcurrentHashMap<Player, PlayerData> playerWaitMap;
    private final Map<UUID, PlayerInv> playerInventory;
    private final int maxGambling;
    private final HashMap<String, Location> playerLocation;
    private final List<Player> hidePlayer;

    public MatchManager(Gambling gambling) {
        this.gambling = gambling;
        this.playerMatchMap = new ConcurrentHashMap();
        this.playerWaitMap = new ConcurrentHashMap();
        this.playerInventory = new HashMap();
        this.maxGambling = this.gambling.getConfigManager().getInt("GAMBLING-MAX-PLAYERS");
        this.playerLocation = new HashMap();
        this.hidePlayer = new ArrayList();
    }

    public void messageStartDuel(Player player) {
        player.sendMessage(this.gambling.getConfigManager().getString("GAMBLING-START").replace("<target>", ((Player)this.playerMatchMap.get(player)).getName()));
    }

    public void messageEndDuel(Player winner, Player loser) {
        List<String> message = this.gambling.getConfigManager().getStringList("GAMBLING-END");
        Iterator var4 = message.iterator();

        while(var4.hasNext()) {
            String mess = (String)var4.next();
            winner.sendMessage(ChatColor.translateAlternateColorCodes('&', mess.replace("<winner>", winner.getName()).replace("<loser>", loser.getName())));
            loser.sendMessage(ChatColor.translateAlternateColorCodes('&', mess.replace("<winner>", winner.getName()).replace("<loser>", loser.getName())));
        }

    }

    public void startMatch(Player player, Player target) {
        this.putInMatch(player, target);
        this.preparePlayerToFight(player);
        this.preparePlayerToFight(target);
        this.playerLocation.put(player.getName(), player.getLocation());
        this.playerLocation.put(target.getName(), target.getLocation());
        Location pos1 = ParseLoc.getParseLoc(this.gambling.getArenaConfig().getString("ARENA.WORLD"), this.gambling.getArenaConfig().getString("ARENA.POS1"));
        Location pos2 = ParseLoc.getParseLoc(this.gambling.getArenaConfig().getString("ARENA.WORLD"), this.gambling.getArenaConfig().getString("ARENA.POS2"));
        Iterator var5 = Bukkit.getOnlinePlayers().iterator();

        while(var5.hasNext()) {
            Player players = (Player)var5.next();
            if (players != this.getPlayerMatchMap().get(player)) {
                player.hidePlayer(players);
                this.hidePlayer.add(players);
            }

            if (players != this.getPlayerMatchMap().get(target)) {
                target.hidePlayer(players);
                this.hidePlayer.add(players);
            }
        }

        player.teleport(pos1);
        target.teleport(pos2);
    }

    public void endMatch(Player winner, Player loser) {
        this.removeFromMatch(winner);
        this.removeFromMatch(loser);
        this.messageEndDuel(winner, loser);
        this.clearInventory(winner);
        if (this.hasPreviousInventory(winner)) {
            this.loadInventory(winner);
        }

        this.clearInventory(loser);
        if (this.hasPreviousInventory(loser)) {
            this.loadInventory(loser);
        }

        Location location;
        if (this.playerLocation.containsKey(winner.getName())) {
            winner.teleport((Location)this.playerLocation.get(winner.getName()));
            this.playerLocation.remove(winner.getName());
        } else {
            location = winner.getLocation().getWorld().getSpawnLocation();
            winner.teleport(location.add(0.0D, 0.0D, 5.0D));
        }

        if (this.playerLocation.containsKey(loser.getName())) {
            winner.teleport((Location)this.playerLocation.get(loser.getName()));
            this.playerLocation.remove(loser.getName());
        } else {
            location = winner.getLocation().getWorld().getSpawnLocation();
            loser.teleport(location.add(0.0D, 0.0D, 5.0D));
        }

        if (((PlayerData)this.gambling.getGamblingManager().getPlayerDataHashMap().get(winner)).isUseMoney()) {
            double money = ((PlayerData)this.gambling.getGamblingManager().getPlayerDataHashMap().get(winner)).getMoney();
            this.gambling.getEconomy().depositPlayer(winner, money * (double)this.gambling.getConfig().getInt("MULTIPLICATOR"));
        } else if (winner.getInventory().firstEmpty() == -1) {
            this.gambling.getClaimManager().saveWinItem(winner);
        } else {
            ItemStack itemStack = ((PlayerData)this.gambling.getGamblingManager().getPlayerDataHashMap().get(winner)).getItemStack();

            for(int i = 0; i < this.gambling.getConfig().getInt("MULTIPLICATOR"); ++i) {
                winner.getInventory().setItem(i, itemStack);
            }
        }

        this.gambling.getGamblingManager().removeFromMap(winner);
        this.gambling.getGamblingManager().removeFromMap(loser);
        winner.getActivePotionEffects().forEach((potionEffect) -> {
            winner.removePotionEffect(potionEffect.getType());
        });
        loser.getActivePotionEffects().forEach((potionEffect) -> {
            loser.removePotionEffect(potionEffect.getType());
        });
        winner.setHealth(20.0D);
        loser.setHealth(20.0D);
        Iterator var7 = Bukkit.getOnlinePlayers().iterator();

        while(var7.hasNext()) {
            Player players = (Player)var7.next();
            if (players != loser) {
                winner.showPlayer(players);
                this.hidePlayer.remove(players);
            }

            if (players != winner) {
                loser.showPlayer(players);
                this.hidePlayer.remove(players);
            }
        }

    }

    public void endConnexion(Player player, Config config) {
        this.clearInventory(player);
        this.loadStuffFromConfig(player, config);
        player.getActivePotionEffects().forEach((potionEffect) -> {
            player.removePotionEffect(potionEffect.getType());
        });
    }

    public void putInMatch(Player player, Player target) {
        this.playerMatchMap.put(player, target);
        this.playerMatchMap.put(target, player);
    }

    public void removeFromMatch(Player player) {
        this.playerMatchMap.remove(player);
    }

    public void putInWaitWithMoney(Player player, String kitName, Double money) {
        this.playerWaitMap.put(player, new PlayerData(player.getName(), kitName, money));
    }

    public void putInWaitWithItemStack(Player player, String kitName, ItemStack itemStack) {
        this.playerWaitMap.put(player, new PlayerData(player.getName(), kitName, itemStack));
    }

    public void saveInventory(Player player) {
        this.playerInventory.put(player.getUniqueId(), InventoryUtils.playerInventoryFromPlayer2(player));
    }

    public void loadInventory(Player player) {
        player.getInventory().setContents(((PlayerInv)this.playerInventory.get(player.getUniqueId())).getContents());
        player.getInventory().setArmorContents(((PlayerInv)this.playerInventory.get(player.getUniqueId())).getArmorContents());
        player.updateInventory();
        this.playerInventory.remove(player.getUniqueId());
    }

    public void loadStuffFromConfig(Player player, Config config) {
        try {
            ItemStack[] armor = Base64Save.itemStackArrayFromBase64(config.getString("armor"));
            ItemStack[] stuff = Base64Save.itemStackArrayFromBase64(config.getString("stuff"));
            player.getInventory().setArmorContents(armor);
            player.getInventory().setContents(stuff);
            player.updateInventory();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public boolean hasPreviousInventory(Player player) {
        return this.playerInventory.containsKey(player.getUniqueId());
    }

    public void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
    }

    public void preparePlayerToFight(Player player) {
        this.messageStartDuel(player);
        this.saveInventory(player);
        this.clearInventory(player);
        player.closeInventory();
        player.getActivePotionEffects().forEach((potionEffect) -> {
            player.removePotionEffect(potionEffect.getType());
        });
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
    }

    public ConcurrentHashMap<Player, Player> getPlayerMatchMap() {
        return this.playerMatchMap;
    }

    public ConcurrentHashMap<Player, PlayerData> getPlayerWaitMap() {
        return this.playerWaitMap;
    }

    public int getMaxGambling() {
        return this.maxGambling;
    }

    public Map<UUID, PlayerInv> getPlayerInventory() {
        return this.playerInventory;
    }

    public List<Player> getHidePlayer() {
        return this.hidePlayer;
    }
}
