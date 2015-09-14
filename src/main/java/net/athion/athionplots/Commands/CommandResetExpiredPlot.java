package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Utils.AthionDeleteExpire;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class CommandResetExpiredPlot {

    private AthionPlots plugin;

    public CommandResetExpiredPlot(final CommandSender s, final String[] args) {
        if (AthionPlots.cPerms(s, "AthionPlots.admin.resetexpired")) {
            if (args.length <= 1) {
                AthionCommands.SendMsg(s, AthionCommands.C("WordUsage")
                + ": "
                + ChatColor.RED
                + "/ap "
                + AthionCommands.C("CommandResetExpired")
                + " <"
                + AthionCommands.C("WordWorld")
                + "> "
                + ChatColor.RESET
                + "Example: "
                + ChatColor.RED
                + "/ap "
                + AthionCommands.C("CommandResetExpired")
                + " plotworld ");
            } else if (AthionPlots.worldcurrentlyprocessingexpired != null) {
                AthionCommands.SendMsg(s, AthionPlots.cscurrentlyprocessingexpired.getName() + " " + AthionCommands.C("MsgAlreadyProcessingPlots"));
            } else {
                final World w = s.getServer().getWorld(args[1]);

                if (w == null) {
                    AthionCommands.SendMsg(s, ChatColor.RED + AthionCommands.C("WordWorld") + " '" + args[1] + "' " + AthionCommands.C("MsgDoesNotExistOrNotLoaded"));

                } else if (!AthionCore.isPlotWorld(w)) {
                    AthionCommands.SendMsg(s, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));

                } else {
                    AthionPlots.worldcurrentlyprocessingexpired = w;
                    AthionPlots.cscurrentlyprocessingexpired = s;
                    AthionPlots.counterexpired = 50;
                    AthionPlots.nbperdeletionprocessingexpired = 5;

                    plugin.scheduleTask(new AthionDeleteExpire(), 5, 50);
                }
            }
        } else {
            AthionCommands.SendMsg(s, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
