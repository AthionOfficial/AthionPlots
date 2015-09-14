package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandInfo {

    public CommandInfo(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "AthionPlots.use.info")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final String id = AthionCore.getPlotID(p.getLocation());

                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    p.sendMessage(ChatColor.GOLD + "====[" + ChatColor.AQUA + " AthionPlots Plot Info " + ChatColor.GOLD + "]====");
                    p.sendMessage("Plot ID: " + ChatColor.GREEN + id);
                    p.sendMessage("Plot Owner: " + ChatColor.GREEN + plot.owner);
                    p.sendMessage("Plot Expiration Date: " + ChatColor.GREEN + ((plot.expireddate == null) ? AthionCommands.C("WordNever") : plot.expireddate.toString()));
                    p.sendMessage(ChatColor.DARK_GRAY
                    + "Biome: "
                    + ChatColor.GRAY
                    + AthionCommands.FormatBiome(plot.biome.name())
                    + ChatColor.DARK_GRAY
                    + " Finished: "
                    + ChatColor.GRAY
                    + ((plot.finished) ? AthionCommands.C("WordYes") : AthionCommands.C("WordNo"))
                    + ChatColor.DARK_GRAY
                    + " Protected: "
                    + ChatColor.GRAY
                    + ((plot.protect) ? AthionCommands.C("WordYes") : AthionCommands.C("WordNo")));

                    if (plot.allowedcount() > 0) {
                        p.sendMessage(ChatColor.GREEN + AthionCommands.C("InfoHelpers") + ": " + ChatColor.AQUA + plot.getAllowed());
                    }

                    if (AthionPlots.allowToBlock && (plot.deniedcount() > 0)) {
                        p.sendMessage(ChatColor.GREEN + AthionCommands.C("InfoDenied") + ": " + ChatColor.AQUA + plot.getDenied());
                    }

                    if (AthionCore.isEconomyEnabled(p)) {
                        if ((plot.currentbidder == null) || plot.currentbidder.equalsIgnoreCase("")) {
                            p.sendMessage(ChatColor.GREEN
                            + AthionCommands.C("InfoAuctionned")
                            + ": "
                            + ChatColor.AQUA
                            + ((plot.auctionned) ? AthionCommands.C("WordYes")
                            + ChatColor.GREEN
                            + " "
                            + AthionCommands.C("InfoMinimumBid")
                            + ": "
                            + ChatColor.AQUA
                            + AthionCommands.round(plot.currentbid) : AthionCommands.C("WordNo"))
                            + ChatColor.GREEN
                            + " "
                            + AthionCommands.C("InfoForSale")
                            + ": "
                            + ChatColor.AQUA
                            + ((plot.forsale) ? ChatColor.AQUA + AthionCommands.round(plot.customprice) : AthionCommands.C("WordNo")));
                        } else {
                            p.sendMessage(ChatColor.GREEN
                            + AthionCommands.C("InfoAuctionned")
                            + ": "
                            + ChatColor.AQUA
                            + ((plot.auctionned) ? AthionCommands.C("WordYes")
                            + ChatColor.GREEN
                            + " "
                            + AthionCommands.C("InfoBidder")
                            + ": "
                            + ChatColor.AQUA
                            + plot.currentbidder
                            + ChatColor.GREEN
                            + " "
                            + AthionCommands.C("InfoBid")
                            + ": "
                            + ChatColor.AQUA
                            + AthionCommands.round(plot.currentbid) : AthionCommands.C("WordNo"))
                            + ChatColor.GREEN
                            + " "
                            + AthionCommands.C("InfoForSale")
                            + ": "
                            + ChatColor.AQUA
                            + ((plot.forsale) ? ChatColor.AQUA + AthionCommands.round(plot.customprice) : AthionCommands.C("WordNo")));
                        }
                    }
                } else {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgHasNoOwner"));
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
