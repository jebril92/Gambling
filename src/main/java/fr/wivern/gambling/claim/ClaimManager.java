package fr.wivern.gambling.claim;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.data.PlayerData;
import fr.wivern.gambling.util.config.Base64Save;
import fr.wivern.gambling.util.config.Config;
import java.io.File;
import java.io.IOException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClaimManager {
    private final Gambling gambling;
    private Config config;

    public ClaimManager(Gambling gambling) {
        this.gambling = gambling;
    }

    public void claimWinItems(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(this.gambling.getConfigManager().getString("INVENTORY-FULL"));
        } else {
            File fileStuff = this.gambling.getWinItem();
            File fileCheckStuff = new File(fileStuff, player.getName() + "-win.yml");
            if (fileCheckStuff.exists()) {
                this.config = new Config(this.gambling, player.getName() + "-win", "win");

                try {
                    ItemStack[] stuff = Base64Save.itemStackArrayFromBase64(this.config.getString("item"));
                    if (stuff.length > player.getInventory().getContents().length) {
                        player.sendMessage(this.gambling.getConfigManager().getString("NOT-ENOUGHT-PLACE"));
                        player.closeInventory();
                        return;
                    }

                    for(int i = 0; i < this.gambling.getConfig().getInt("MULTIPLICATOR"); ++i) {
                        player.getInventory().addItem(stuff);
                    }

                    player.closeInventory();
                } catch (IOException var6) {
                    var6.printStackTrace();
                }

                this.config.deleteFile(player.getName() + "-win", "win");
            } else {
                player.sendMessage(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.WIN.MESSAGE"));
                player.closeInventory();
            }

        }
    }

    public boolean fileWinExist(Player player) {
        File fileStuff = this.gambling.getWinItem();
        File fileCheckStuff = new File(fileStuff, player.getName() + "-win.yml");
        return fileCheckStuff.exists();
    }

    public boolean fileWaitExist(Player player) {
        File fileStuff = this.gambling.getSaveCrash();
        File fileCheckStuff = new File(fileStuff, player.getName() + "-wait.yml");
        return fileCheckStuff.exists();
    }

    public boolean fileMatchExist(Player player) {
        File fileStuff = this.gambling.getSaveCrash();
        File fileCheckStuff = new File(fileStuff, player.getName() + "-match.yml");
        return fileCheckStuff.exists();
    }

    public boolean fileLeaveExist(Player player) {
        File fileStuff = this.gambling.getStoreItem();
        File fileCheckStuff = new File(fileStuff, player.getName() + "-leave.yml");
        return fileCheckStuff.exists();
    }

    public void saveWinItem(Player player) {
        this.config = new Config(this.gambling, player.getName() + "-win", "win");
        if (this.gambling.getGamblingManager().getPlayerDataHashMap().containsKey(player)) {
            ItemStack itemStack = ((PlayerData)this.gambling.getGamblingManager().getPlayerDataHashMap().get(player)).getItemStack();
            this.config.set("item", Base64Save.itemStackArrayToBase64(new ItemStack[]{itemStack}));
            this.config.save("win");
        }

        player.sendMessage(this.gambling.getConfigManager().getString("MESSAGE-CLAIM"));
    }

    public void saveLeaveItem(Player player) {
        this.config = new Config(this.gambling, player.getName() + "-leave", "items");
        if (this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
            ItemStack itemStack = ((PlayerData)this.gambling.getMatchManager().getPlayerWaitMap().get(player)).getItemStack();
            this.config.set("item", Base64Save.itemStackArrayToBase64(new ItemStack[]{itemStack}));
            this.config.save("items");
        }

        player.sendMessage(this.gambling.getConfigManager().getString("MESSAGE-CLAIM"));
    }

    public void saveWaitCrashItem(Player player, ItemStack[] itemStack) {
        this.config = new Config(this.gambling, player.getName() + "-wait", "saves");
        if (this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
            this.config.set("item", Base64Save.itemStackArrayToBase64(itemStack));
            this.config.save("saves");
        }

        player.sendMessage(this.gambling.getConfigManager().getString("MESSAGE-CLAIM"));
    }

    public void saveMatchCrashItem(Player player, ItemStack[] itemStack) {
        this.config = new Config(this.gambling, player.getName() + "-match", "saves");
        if (this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
            this.config.set("item", Base64Save.itemStackArrayToBase64(itemStack));
            this.config.save("saves");
        }

        player.sendMessage(this.gambling.getConfigManager().getString("MESSAGE-CLAIM"));
    }

    public void claimLeaveItems(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(this.gambling.getConfigManager().getString("INVENTORY-FULL"));
        } else {
            File fileStuff = this.gambling.getStoreItem();
            File fileCheckStuff = new File(fileStuff, player.getName() + "-leave.yml");
            if (fileCheckStuff.exists()) {
                this.config = new Config(this.gambling, player.getName() + "-leave", "items");

                try {
                    ItemStack[] stuff = Base64Save.itemStackArrayFromBase64(this.config.getString("item"));
                    if (stuff.length > player.getInventory().getContents().length) {
                        player.sendMessage(this.gambling.getConfigManager().getString("NOT-ENOUGHT-PLACE"));
                        player.closeInventory();
                        return;
                    }

                    player.getInventory().addItem(stuff);
                    player.closeInventory();
                } catch (IOException var5) {
                    var5.printStackTrace();
                }

                this.config.deleteFile(player.getName() + "-leave", "items");
            } else {
                player.sendMessage(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.LEAVE.MESSAGE"));
                player.closeInventory();
            }

        }
    }

    public void claimBetItems(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(this.gambling.getConfigManager().getString("INVENTORY-FULL"));
        } else {
            File fileStuff = this.gambling.getStoreItem();
            File fileCheckStuff = new File(fileStuff, player.getName() + ".yml");
            if (fileCheckStuff.exists()) {
                this.config = new Config(this.gambling, player.getName(), "items");

                try {
                    ItemStack[] stuff = Base64Save.itemStackArrayFromBase64(this.config.getString("bet"));
                    if (stuff.length > player.getInventory().getContents().length) {
                        player.sendMessage(this.gambling.getConfigManager().getString("NOT-ENOUGHT-PLACE"));
                        player.closeInventory();
                        return;
                    }

                    player.getInventory().addItem(stuff);
                    player.closeInventory();
                } catch (IOException var5) {
                    var5.printStackTrace();
                }

                this.config.deleteFile(player.getName() + "-leave", "items");
            } else {
                player.sendMessage(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.BET.MESSAGE"));
                player.closeInventory();
            }

        }
    }

    public void claimCrashItems(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(this.gambling.getConfigManager().getString("INVENTORY-FULL"));
        } else {
            File fileStuff = this.gambling.getSaveCrash();
            File fileCheckStuffWait = new File(fileStuff, player.getName() + "-wait.yml");
            File fileCheckStuffMatch = new File(fileStuff, player.getName() + "-match.yml");
            ItemStack[] stuff;
            if (fileCheckStuffWait.exists()) {
                this.config = new Config(this.gambling, player.getName() + "-wait", "saves");

                try {
                    stuff = Base64Save.itemStackArrayFromBase64(this.config.getString("item"));
                    if (stuff.length > player.getInventory().getContents().length) {
                        player.sendMessage(this.gambling.getConfigManager().getString("NOT-ENOUGHT-PLACE"));
                        player.closeInventory();
                        return;
                    }

                    player.getInventory().addItem(stuff);
                    player.closeInventory();
                } catch (IOException var7) {
                    var7.printStackTrace();
                }

                this.config.deleteFile(player.getName() + "-wait", "saves");
            } else if (fileCheckStuffMatch.exists()) {
                this.config = new Config(this.gambling, player.getName() + "-match", "saves");

                try {
                    stuff = Base64Save.itemStackArrayFromBase64(this.config.getString("item"));
                    if (stuff.length > player.getInventory().getContents().length) {
                        player.sendMessage(this.gambling.getConfigManager().getString("NOT-ENOUGHT-PLACE"));
                        player.closeInventory();
                        return;
                    }

                    player.getInventory().addItem(stuff);
                    player.closeInventory();
                } catch (IOException var6) {
                    var6.printStackTrace();
                }

                this.config.deleteFile(player.getName() + "-match", "saves");
            } else {
                player.sendMessage(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.CRASH.MESSAGE"));
                player.closeInventory();
            }

        }
    }
}
