package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandDone {

    public CommandDone(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "AthionPlots.use.done") || AthionPlots.cPerms(p, "AthionPlots.admin.done")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));

            } else {
                final String id = AthionCore.getPlotID(p.getLocation());

                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else {
                    if (!AthionCore.isPlotAvailable(id, p)) {
                        final AthionPlot plot = AthionCore.getPlotById(p, id);
                        final String name = p.getName();

                        if (plot.owner.equalsIgnoreCase(name) || AthionPlots.cPerms(p, "AthionPlots.admin.done")) {
                            if (plot.finished) {
                                plot.setUnfinished();
                                AthionCommands.SendMsg(p, AthionCommands.C("MsgUnmarkFinished"));
                            } else {
                                plot.setFinished();
                                AthionCommands.SendMsg(p, AthionCommands.C("MsgMarkFinished"));
                            }
                        }
                    } else {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgHasNoOwner"));
                    }
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
