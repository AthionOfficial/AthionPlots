package net.athion.athionplots.Commands;

import java.util.UUID;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.athion.athionplots.Core.AthionSQL;
import net.milkbowl.vault.economy.EconomyResponse;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandComment {

    public CommandComment(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "AthionPlots.use.comment")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else if (args.length < 2) {
                AthionCommands.SendMsg(p, AthionCommands.C("WordUsage") + ": " + ChatColor.RED + "/ap " + AthionCommands.C("CommandComment") + " <" + AthionCommands.C("WordText") + ">");
            } else {
                final String id = AthionCore.getPlotID(p.getLocation());

                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNoPlotFound"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    final World w = p.getWorld();
                    final AthionMaps pmi = AthionCore.getMap(w);
                    final String playername = p.getName();
                    final UUID uuid = p.getUniqueId();

                    double price = 0;

                    if (AthionCore.isEconomyEnabled(w)) {
                        price = pmi.AddCommentPrice;
                        final double balance = AthionPlots.economy.getBalance(p);

                        if (balance >= price) {
                            final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                            if (!er.transactionSuccess()) {
                                AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                AthionCommands.warn(er.errorMessage);

                            }
                        } else {
                            AthionCommands.SendMsg(p,
                            ChatColor.RED + AthionCommands.C("MsgNotEnoughComment") + " " + AthionCommands.C("WordMissing") + " " + ChatColor.RESET + AthionCommands.f(price - balance, false));

                        }
                    }

                    final AthionPlot plot = AthionCore.getPlotById(p, id);

                    String text = StringUtils.join(args, " ");
                    text = text.substring(text.indexOf(" "));

                    final String[] comment = new String[3];
                    comment[0] = playername;
                    comment[1] = text;
                    comment[2] = uuid.toString();

                    plot.comments.add(comment);
                    AthionSQL.addPlotComment(comment, plot.comments.size(), AthionCore.getIdX(id), AthionCore.getIdZ(id), plot.world, uuid);

                    AthionCommands.SendMsg(p, AthionCommands.C("MsgCommentAdded") + " " + AthionCommands.f(-price));

                } else {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlot") + "(" + id + ") " + AthionCommands.C("MsgHasNoOwner"));
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
