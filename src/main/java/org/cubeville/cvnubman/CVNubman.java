package org.cubeville.cvnubman;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.*;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CVNubman extends JavaPlugin implements Listener {

    final UUID games1 = UUID.fromString("73d28f31-d639-4994-8860-7606c3fe7597");
    World nubmanWorld;
    
    UUID nubman;
    String nubmanName;
    int killMode;

    int particleTaskId;

    final double py = 73.8;
    
    final double p1x = 2040.5;
    final double p1z = 19.5;
    boolean p1active = true;
    
    final double p2x = 2040.5;
    final double p2z = -5.5;
    boolean p2active = true;

    final double p3x = 2018.5;
    final double p3z = 19.5;
    boolean p3active = true;

    final double p4x = 2018.5;
    final double p4z = -5.5;
    boolean p4active = true;

    double xmin = 2013;
    double ymin = 71;
    double zmin = -7;
    double xmax = 2045;
    double ymax = 76;
    double zmax = 21;

    double rxmin = 2029.2;
    double rxmax = 2031.8;
    double rzmin = 4.2;
    double rzmax = 9.8;

    int currentLevel;
    int pillCount[] = { 298, 304, 312, 326, 310 };
    int pillsRemaining;

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        nubmanWorld = Bukkit.getWorld(games1);

        particleTaskId = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                public void run() {
                    if(p1active) nubmanWorld.spawnParticle(Particle.REDSTONE, p1x, py, p1z, 2, .2, .2, .2, new Particle.DustOptions(Color.RED, 1));
                    if(p2active) nubmanWorld.spawnParticle(Particle.REDSTONE, p2x, py, p2z, 2, .2, .2, .2, new Particle.DustOptions(Color.RED, 1));
                    if(p3active) nubmanWorld.spawnParticle(Particle.REDSTONE, p3x, py, p3z, 2, .2, .2, .2, new Particle.DustOptions(Color.RED, 1));
                    if(p4active) nubmanWorld.spawnParticle(Particle.REDSTONE, p4x, py, p4z, 2, .2, .2, .2, new Particle.DustOptions(Color.RED, 1));
                    if(killMode > 0) {
                        killMode--;
                        if(killMode == 0) {
                            int levelName = currentLevel + 1;
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "loadout apply player:" + nubmanName + " nubman team:nubman_level_" + levelName);
                            for(Player p: getGhosts()) {
                                showTitle(p.getDisplayName(), "Hunt mode ends", "green", "Go get him!", "white");
                            }
                            showTitle(nubmanName, "Hunt mode ends", "red", "Run for your life!", "white");                         }
                    }
                    if(killMode > 0 && killMode % 4 == 0) {
                        nubmanWorld.playSound(Bukkit.getServer().getPlayer(nubman).getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 15.0f, 1.0f);
                    }
                }
            }, 20, 5).getTaskId();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("nubman")) {
            if(args.length < 1) return true;
            if(args[0].equals("setnubman")) {
                String playerName = args[1];
                Player player = Bukkit.getServer().getPlayerExact(playerName);
                nubman = player.getUniqueId();
                nubmanName = player.getName();
                initializeLevel(0);
            }
            return true;
        }
        return false;
    }

    private List<Player> getPlayers() {
        List<Player> ret = new ArrayList<>();
        for(Player player: nubmanWorld.getPlayers()) {
                if(player.getLocation().getX() < xmin || player.getLocation().getX() > xmax ||
                   player.getLocation().getY() < ymin || player.getLocation().getY() > ymax ||
                   player.getLocation().getZ() < zmin || player.getLocation().getZ() > zmax) continue;
                ret.add(player);
        }
        return ret;
    }

    private List<Player> getGhosts() {
        List<Player> ret = new ArrayList<>();
        for(Player p: getPlayers()) {
            if(!p.getUniqueId().equals(nubman)) ret.add(p);
        }
        return ret;
    }
    
    private void initializeLevel(int level) {
        killMode = 0;
        int levelName = level + 1;
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "loadout apply player:" + nubmanName + " nubman team:nubman_level_" + levelName);
        pillsRemaining = pillCount[level];
        currentLevel = level;
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "cvblocks copytoregion games1 nubman_level" + levelName + " games1 nubman_top");
        p1active = true;
        p2active = true;
        p3active = true;
        p4active = true;
        if(level > 0) {
            int ghostCnt = 0;
            for(Player player: getPlayers()) {
                if(player.getUniqueId().equals(nubman)) {
                    player.teleport(new Location(nubmanWorld, 2021.5, 74.0, 7.0, 270, 1));
                }
                else {
                    player.removePotionEffect(PotionEffectType.GLOWING);
                    if(ghostCnt == 0) {
                        player.teleport(new Location(nubmanWorld, 2031.1, 74.0, 4.9, 80, 0));
                    }
                    else if(ghostCnt == 1) {
                        player.teleport(new Location(nubmanWorld, 2029.8, 74.0, 4.9, 76, 0));
                    }
                    else if(ghostCnt == 2) {
                        player.teleport(new Location(nubmanWorld, 2031.1, 74.0, 9.1, 100, 3));
                    }
                    else if(ghostCnt >= 3) {
                        player.teleport(new Location(nubmanWorld, 2029.9, 74.0, 9.1, 101, 3));
                    }
                    ghostCnt++;
                }
                showTitle(player.getName(), "Level " + (level + 1), "white", "", "white");
            }
        }
    }

    private void showTitle(String player, String title, String titleColor, String subtitle, String subtitleColor) {
        if(subtitle != null && (!subtitle.equals("")))
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "title " + player + " subtitle {\"text\":\"" + subtitle + "\",\"color\":\"" + subtitleColor + "\"}");
        String titleCommand = "title " + player + " title {\"text\":\"" + title + "\",\"color\":\"" + titleColor + "\"}";
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), titleCommand);
    }

    private void sendBroadcastMessage(String message) {
        double x = (xmin + xmax) / 2;
        double y = (ymin + ymax) / 2;
        double z = (zmin + zmax) / 2;
        Location center = new Location(nubmanWorld, x, y, z);
        for(Player p: nubmanWorld.getPlayers()) {
            if(center.distance(p.getLocation()) < 150) {
                p.sendMessage(message);
            }
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if(!player.getWorld().getUID().equals(games1)) return;

        double x = event.getTo().getX();
        double y = event.getTo().getY();
        double z = event.getTo().getZ();
        if(x >= xmin && x <= xmax && y >= ymin && y <= ymax && z >= zmin && z <= zmax) {
            // Teleport
            if(z > zmax - 1 || z < zmin + 1) {
                Location l = event.getTo();
                l.setZ(l.getZ() + 26.0 * ((z > zmax -1) ? -1 : 1));
                player.teleport(l);
            }
            // Collisions with other players and/or hotspots
            for(Player p: player.getWorld().getPlayers()) {
                if(!p.getUniqueId().equals(player.getUniqueId())) { // don't check for collision with self
                    if(p.getLocation().distance(player.getLocation()) < 0.8) {
                        if(killMode == 0 && p.getUniqueId().equals(nubman)) { // Ghost touching nubman, and nubman is not in killing mode
                            if(player.getPotionEffect(PotionEffectType.GLOWING) == null) { // If the ghost is glowing he can't kill nubman
                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "cvportal trigger nubman_end");
                                sendBroadcastMessage("§aGhost §6" + player.getDisplayName() + " §acaught Nubman §6" + nubmanName + "§a!");
                            }
                        }
                        else if(killMode > 0 && player.getUniqueId().equals(nubman)) { // Nubman running into ghost
                            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10000, 1, false, false));
                            nubmanWorld.playSound(p.getLocation(), Sound.ENTITY_GHAST_SCREAM, 15.0f, 1.0f);
                            showTitle(p.getName(), "Caught!", "red", "Run to the center!", "white");
                        }
                    }
                }
            }

            if(player.getPotionEffect(PotionEffectType.GLOWING) != null &&
               player.getLocation().getX() >= rxmin && player.getLocation().getX() <= rxmax &&
               player.getLocation().getZ() >= rzmin && player.getLocation().getZ() <= rzmax) {
                player.removePotionEffect(PotionEffectType.GLOWING);
                showTitle(player.getName(), "Recovered!", "green", "The hunt goes on!", "white");
            }
        }

        if(player.getUniqueId().equals(nubman)) {
            if(event.getTo().getBlockY() == 73 && (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())) {
                Block block = player.getLocation().getWorld().getBlockAt(event.getTo().getBlockX(), 71, event.getTo().getBlockZ());
                if(block.getType() == Material.REDSTONE_BLOCK) {
                    block.setType(Material.BRICKS);
                    event.getTo().getWorld().playSound(event.getTo(), Sound.ENTITY_GENERIC_EAT, 15.0f, 1.0f);
                    pillsRemaining -= 1;
                    if(pillsRemaining <= 0) {
                        currentLevel++;
                        if(currentLevel == 5) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "cvportal trigger nubman_success");
                            sendBroadcastMessage("§aNubman §6" + player.getDisplayName() + " §awon!");
                        }
                        else {
                            initializeLevel(currentLevel);
                        }
                    }
                }
            }

            if(event.getTo().getY() >= 72.5 && event.getTo().getY() <= 74.5) {
                if(p1active && event.getTo().distance(new Location(event.getTo().getWorld(), p1x, event.getTo().getY(), p1z)) < .5) {
                    p1active = false;
                    activateKillMode();
                }
                else if(p2active && event.getTo().distance(new Location(event.getTo().getWorld(), p2x, event.getTo().getY(), p2z)) < .5) {
                    p2active = false;
                    activateKillMode();
                }
                else if(p3active && event.getTo().distance(new Location(event.getTo().getWorld(), p3x, event.getTo().getY(), p3z)) < .5) {
                    p3active = false;
                    activateKillMode();
                }
                else if(p4active && event.getTo().distance(new Location(event.getTo().getWorld(), p4x, event.getTo().getY(), p4z)) < .5) {
                    p4active = false;
                    activateKillMode();
                }
            }
        }
    }

    private void activateKillMode() {
        if(killMode == 0) {
            for(Player p: getGhosts()) {
                showTitle(p.getDisplayName(), "Nubman hunts!", "red", "Run for your lives!", "white");
            }
            showTitle(nubmanName, "Hunt mode!", "green", "Get them!", "white");
        }
        int levelName = currentLevel + 1;
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "loadout apply player:" + nubmanName + " nubman team:nubman_mad_level_" + levelName);
        killMode = 40;
    }

}
