package fr.wivern.gambling.save;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.data.PlayerData;
import fr.wivern.gambling.util.config.Base64Save;
import fr.wivern.gambling.util.config.Config;
import fr.wivern.gambling.util.save.PlayerInv;
import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SaveManager {
    private final Gambling gambling;
    private File folder;
    private Config saveConfig;

    public SaveManager(Gambling gambling) {
        this.gambling = gambling;
        this.folder = this.gambling.getSaveCrash();
    }

    public void savePlayerFromWait(Player player) {
        if (this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
            this.gambling.getMatchManager().getPlayerWaitMap().forEach((players, playerData) -> {
                this.saveConfig = new Config(this.gambling, players.getName() + "-wait", "saves");
                if (playerData.isUseMoney()) {
                    this.saveConfig.set("money", (int)playerData.getMoney());
                } else {
                    ItemStack[] itemStacks = new ItemStack[]{((PlayerData)this.gambling.getMatchManager().getPlayerWaitMap().get(players)).getItemStack()};
                    this.saveConfig.set("item", Base64Save.itemStackArrayToBase64(itemStacks));
                }

                this.saveConfig.save("saves");
            });
        }

    }

    public void savePlayerFromMatch(Player player) {
        if (this.gambling.getMatchManager().getPlayerMatchMap().containsKey(player)) {
            this.gambling.getMatchManager().getPlayerMatchMap().forEach((players, player2) -> {
                this.saveConfig = new Config(this.gambling, players.getName() + "-match", "saves");
                if (((PlayerData)this.gambling.getGamblingManager().getPlayerDataHashMap().get(players)).isUseMoney()) {
                    PlayerData playerData = (PlayerData)this.gambling.getGamblingManager().getPlayerDataHashMap().get(players);
                    this.saveConfig.set("money", playerData.getMoney());
                } else {
                    ItemStack[] itemStacks = new ItemStack[]{((PlayerData)this.gambling.getGamblingManager().getPlayerDataHashMap().get(players)).getItemStack()};
                    this.saveConfig.set("item", Base64Save.itemStackArrayToBase64(itemStacks));
                }

                this.saveConfig.set("stuff", Base64Save.itemStackArrayToBase64(((PlayerInv)this.gambling.getMatchManager().getPlayerInventory().get(players.getUniqueId())).getContents()));
                this.saveConfig.set("armor", Base64Save.itemStackArrayToBase64(((PlayerInv)this.gambling.getMatchManager().getPlayerInventory().get(players.getUniqueId())).getArmorContents()));
                this.saveConfig.save("saves");
            });
        }

    }

    public void loadStuffWait(Player player) {
        File file = new File(this.folder, player.getName() + "-wait.yml");
        if (file.exists()) {
            Config config = new Config(this.gambling, player.getName() + "-wait", "saves");
            if (config.get("item") != null) {
                try {
                    ItemStack[] item = Base64Save.itemStackArrayFromBase64(config.getString("item"));
                    if (player.getInventory().firstEmpty() == -1) {
                        this.gambling.getClaimManager().saveWaitCrashItem(player, item);
                        return;
                    }

                    player.getInventory().addItem(item);
                    player.updateInventory();
                } catch (IOException var5) {
                    var5.printStackTrace();
                }
            } else if (config.get("money") != null) {
                this.gambling.getEconomy().depositPlayer(player, (double)config.getInt("money"));
            }

            config.deleteFile(player.getName() + "-wait", "saves");
        }

    }

    public void loadStuffMatch(Player player) {
        File file = new File(this.folder, player.getName() + "-match.yml");
        Location location = player.getLocation().getWorld().getSpawnLocation();
        if (file.exists()) {
            Config config = new Config(this.gambling, player.getName() + "-match", "saves");
            if (config.get("item") != null) {
                this.gambling.getServer().getScheduler().runTaskLaterAsynchronously(this.gambling, () -> {
                    this.endConnexion(player, config);
                    player.teleport(location);

                    try {
                        ItemStack[] item = Base64Save.itemStackArrayFromBase64(config.getString("item"));
                        if (player.getInventory().firstEmpty() == -1) {
                            this.gambling.getClaimManager().saveMatchCrashItem(player, item);
                            return;
                        }

                        player.getInventory().addItem(item);
                        player.updateInventory();
                    } catch (IOException var5) {
                        var5.printStackTrace();
                    }

                }, 20L);
            } else if (config.get("money") != null) {
                this.gambling.getEconomy().depositPlayer(player, config.getDouble("money"));
                this.gambling.getServer().getScheduler().runTaskLaterAsynchronously(this.gambling, () -> {
                    this.endConnexion(player, config);
                    player.teleport(location);
                }, 20L);
            }

            config.deleteFile(player.getName() + "-match", "saves");
        }

    }

    public void endConnexion(Player player, Config config) {
        this.gambling.getMatchManager().loadStuffFromConfig(player, config);
        player.getActivePotionEffects().forEach((potionEffect) -> {
            player.removePotionEffect(potionEffect.getType());
        });
    }
}
