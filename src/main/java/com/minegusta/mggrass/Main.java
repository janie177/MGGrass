package com.minegusta.mggrass;

import com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin implements CommandExecutor{

    public static Plugin plugin;

    @Override
    public void onEnable()
    {
        plugin = this;
        getCommand("grassify").setExecutor(this);

    }

    @Override
    public void onDisable()
    {

    }

    public Plugin getPlugin()
    {
        return plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args)
    {
        if(s instanceof ConsoleCommandSender || !s.isOp()) return false;

        Player p = (Player) s;
        grassify(p);
        p.sendMessage(ChatColor.GREEN + "Let there be grass!");

        return true;
    }

    private void grassify(Player p)
    {
        Location start = p.getLocation();
        double x = start.getX();
        double z = start.getZ();
        Random rand = new Random();
        int i = 0;

        while (i < 20)
        {
            int newX = (int) x + (rand.nextInt(160) - 80);
            int newZ = (int) z + (rand.nextInt(160) - 80);
            int y = p.getWorld().getHighestBlockYAt(newX, newZ) - 1;

            Block b = p.getWorld().getBlockAt(newX, y, newZ);

            if(b.getType() == Material.LEAVES)
            {
                y = getGrassY(p.getWorld(), newX, y, newZ);
                if(y == 0)
                {
                    i++;
                    continue;
                }
            }
            else if(b.getType() != Material.GRASS)
            {
                i++;
                continue;
            }

            Location location = new Location(p.getWorld(), newX, y, newZ);

            makeGrass(location);

            i++;
        }
    }

    private int getGrassY(World w, int x, int y, int z)
    {
        int count = 1;
        Block start = w.getBlockAt(x, y, z);


        while (count < 21)
        {
            if(start.getRelative(0, -count, 0).getType() == Material.GRASS) return y - count;
            count++;
        }
        return 0;
    }

    private void makeGrass(Location start)
    {
        Random rand = new Random();
        List<Block> blocks = Lists.newArrayList();

        int radius = 6;

        for(int x = -radius; x < radius; x++)
        {
            for(int y = -1; y < 3; y++)
            {
                for(int z = -radius; z < radius; z++)
                {
                    Block newBlock = start.getBlock().getRelative(x, y, z);
                    if(newBlock.getLocation().distance(start) > 5) continue;
                    if(newBlock.getType() == Material.AIR && newBlock.getRelative(0, -1, 0).getType() == Material.GRASS && rand.nextBoolean())
                    {
                        blocks.add(newBlock);
                    }
                }
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), ()-> blocks.stream().forEach(b ->
        {
            b.setType(Material.LONG_GRASS);
            b.setData((byte)1);
        }), 5);
    }
}
