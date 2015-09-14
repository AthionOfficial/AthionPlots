package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandID {

    public CommandID(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.admin.id")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final String plotid = AthionCore.getPlotID(p.getLocation());

                if (plotid.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else {
                    p.sendMessage(ChatColor.BLUE + AthionCommands.C("WordPlot") + " " + AthionCommands.C("WordId") + ": " + ChatColor.RESET + plotid);

                    final Location bottom = AthionCore.getPlotBottomLoc(p.getWorld(), plotid);
                    final Location top = AthionCore.getPlotTopLoc(p.getWorld(), plotid);

                    p.sendMessage(ChatColor.BLUE + AthionCommands.C("WordBottom") + ": " + ChatColor.RESET + bottom.getBlockX() + ChatColor.BLUE + "," + ChatColor.RESET + bottom.getBlockZ());
                    p.sendMessage(ChatColor.BLUE + AthionCommands.C("WordTop") + ": " + ChatColor.RESET + top.getBlockX() + ChatColor.BLUE + "," + ChatColor.RESET + top.getBlockZ());
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
