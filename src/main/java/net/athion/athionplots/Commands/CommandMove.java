package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandMove {

    public CommandMove(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.admin.move")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else if ((args.length < 3) || args[1].equalsIgnoreCase("") || args[2].equalsIgnoreCase("")) {
                AthionCommands.SendMsg(p, AthionCommands.C("WordUsage")
                + ": "
                + ChatColor.RED
                + "/ap "
                + AthionCommands.C("CommandMove")
                + " <"
                + AthionCommands.C("WordIdFrom")
                + "> <"
                + AthionCommands.C("WordIdTo")
                + "> "
                + ChatColor.RESET
                + AthionCommands.C("WordExample")
                + ": "
                + ChatColor.RED
                + "/ap "
                + AthionCommands.C("CommandMove")
                + " 0;1 2;-1");
            } else {
                final String plot1 = args[1];
                final String plot2 = args[2];

                if (!AthionCore.isValidId(plot1) || !AthionCore.isValidId(plot2)) {
                    AthionCommands.SendMsg(p, AthionCommands.C("WordUsage")
                    + ": "
                    + ChatColor.RED
                    + "/ap "
                    + AthionCommands.C("CommandMove")
                    + " <"
                    + AthionCommands.C("WordIdFrom")
                    + "> <"
                    + AthionCommands.C("WordIdTo")
                    + "> "
                    + ChatColor.RESET
                    + AthionCommands.C("WordExample")
                    + ": "
                    + ChatColor.RED
                    + "/ap "
                    + AthionCommands.C("CommandMove")
                    + " 0;1 2;-1");

                } else if (AthionCore.mP(p.getWorld(), plot1, plot2)) {
                    AthionCommands.SendMsg(p, AthionCommands.C("MsgPlotMovedSuccess"));
                } else {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("ErrMovingPlot"));
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
