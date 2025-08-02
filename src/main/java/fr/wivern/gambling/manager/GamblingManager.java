package fr.wivern.gambling.manager;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.data.PlayerData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GamblingManager {
    private Gambling gambling;
    private final HashMap<Player, PlayerData> playerDataHashMap;
    private final List<Player> playerMoneyWait;
    private final List<Player> playerItemWait;
    private final HashMap<Player, Integer> playerMoney;
    private final HashMap<Player, ItemStack> playerItem;

    public GamblingManager(Gambling gambling) {
        this.gambling = gambling;
        this.playerDataHashMap = new HashMap();
        this.playerMoneyWait = new ArrayList();
        this.playerItemWait = new ArrayList();
        this.playerMoney = new HashMap();
        this.playerItem = new HashMap();
    }

    public void putDataWithMoney(Player player, String kitName, double money) {
        this.playerDataHashMap.put(player, new PlayerData(player.getName(), kitName, money));
    }

    public void addPlayerMoney(Player player) {
        this.playerMoneyWait.add(player);
    }

    public void addPlayerItem(Player player) {
        this.playerItemWait.add(player);
    }

    public void removePlayerMoney(Player player) {
        this.playerMoneyWait.remove(player);
    }

    public void removePlayerItem(Player player) {
        this.playerItemWait.remove(player);
    }

    public void putDataWithItemStack(Player player, String kitName, ItemStack itemStack) {
        this.playerDataHashMap.put(player, new PlayerData(player.getName(), kitName, itemStack));
    }

    public void removeFromMap(Player player) {
        this.playerDataHashMap.remove(player);
    }

    public HashMap<Player, PlayerData> getPlayerDataHashMap() {
        return this.playerDataHashMap;
    }

    public List<Player> getPlayerMoneyWait() {
        return this.playerMoneyWait;
    }

    public List<Player> getPlayerItemWait() {
        return this.playerItemWait;
    }

    public HashMap<Player, Integer> getPlayerMoney() {
        return this.playerMoney;
    }

    public HashMap<Player, ItemStack> getPlayerItem() {
        return this.playerItem;
    }
}
