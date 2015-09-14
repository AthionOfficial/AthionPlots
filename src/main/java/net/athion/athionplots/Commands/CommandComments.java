package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandComments {

    public CommandComments(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.use.comments")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else if (args.length < 2) {
                final String id = AthionCore.getPlotID(p.getLocation());

                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    if (plot.ownerId.equals(p.getUniqueId()) || plot.isAllowed(p.getUniqueId()) || AthionPlots.cPerms(p, "plotme.admin")) {
                        if (plot.comments.size() == 0) {
                            AthionCommands.SendMsg(p, AthionCommands.C("MsgNoComments"));
                        } else {
                            AthionCommands.SendMsg(p, AthionCommands.C("MsgYouHave") + " " + ChatColor.BLUE + plot.comments.size() + ChatColor.RESET + " " + AthionCommands.C("MsgComments"));

                            for (final String[] comment : plot.comments) {
                                p.sendMessage(ChatColor.BLUE + AthionCommands.C("WordFrom") + " : " + ChatColor.RED + comment[0]);
                                p.sendMessage("" + ChatColor.RESET + ChatColor.ITALIC + comment[1]);
                            }

                        }
                    } else {
                        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgNotYoursNotAllowedViewComments"));
                    }
                } else {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgHasNoOwner"));
                }
            }
        } else {
            p.sendMessage(ChatColor.BLUE + AthionCommands.SYSTEM_PREFIX + ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
