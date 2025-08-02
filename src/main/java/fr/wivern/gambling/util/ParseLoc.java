package fr.wivern.gambling.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ParseLoc {
    public static Location getParseLoc(String world, String spawnLocation) {
        String[] spawns = spawnLocation.split(",");
        double x = Double.parseDouble(spawns[0]);
        double y = Double.parseDouble(spawns[1]);
        double z = Double.parseDouble(spawns[2]);
        float yaw = 0.0F;
        float pitch = 0.0F;
        if (spawns.length >= 5) {
            yaw = Float.parseFloat(spawns[3]);
            pitch = Float.parseFloat(spawns[4]);
        }

        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
