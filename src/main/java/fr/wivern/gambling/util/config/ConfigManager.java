package fr.wivern.gambling.util.config;

import fr.wivern.gambling.Gambling;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private Gambling gambling;
    private final FileConfiguration config;

    public ConfigManager(Gambling gambling) {
        this.gambling = gambling;
        this.config = gambling.getConfig();
    }

    public String getString(String path) {
        return ChatColor.translateAlternateColorCodes('&', this.gambling.getConfig().getString(path));
    }

    public List<String> getStringList(String path) {
        List<String> stringList = this.gambling.getConfig().getStringList(path);
        ArrayList<String> toReturn = new ArrayList();
        stringList.forEach((line) -> {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        });
        return toReturn;
    }

    public void setDouble(String path, double value) {
        this.config.set(path, value);
    }

    public int getInt(String path) {
        return this.config.getInt(path);
    }

    public boolean getBoolean(String path) {
        return this.config.getBoolean(path);
    }

    private double getDouble(String path) {
        return this.config.getDouble(path);
    }

    public double getFloat(String path) {
        return this.config.getDouble(path);
    }

    public long getLong(String path) {
        return this.config.getLong(path);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void updateConfig() {
        this.gambling.saveConfig();
        this.gambling.reloadConfig();
    }
}
