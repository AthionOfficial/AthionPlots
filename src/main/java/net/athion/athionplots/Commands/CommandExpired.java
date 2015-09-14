package net.athion.athionplots.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;
import net.athion.athionplots.Utils.MFWC;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CommandExpired {

    public CommandExpired(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.admin.expired")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));

            } else {
                final int pagesize = 8;
                int page = 1;
                int maxpage;
                int nbexpiredplots = 0;
                final World w = p.getWorld();
                final List<AthionPlot> expiredplots = new ArrayList<>();
                final HashMap<String, AthionPlot> plots = AthionCore.getPlots(w);
                final String date = AthionPlots.getDate();

                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (final NumberFormatException ignored) {}
                }

                for (final String id : plots.keySet()) {
                    final AthionPlot plot = plots.get(id);

                    if (!plot.protect && (plot.expireddate != null) && (AthionPlots.getDate(plot.expireddate).compareTo(date) < 0)) {
                        nbexpiredplots++;
                        expiredplots.add(plot);
                    }
                }

                //Collections.sort(expiredplots);

                maxpage = (int) Math.ceil(((double) nbexpiredplots / (double) pagesize));

                if (expiredplots.size() == 0) {
                    AthionCommands.SendMsg(p, AthionCommands.C("MsgNoPlotExpired"));
                } else {
                    AthionCommands.SendMsg(p, AthionCommands.C("MsgExpiredPlotsPage") + " " + page + "/" + maxpage);

                    for (int i = (page - 1) * pagesize; (i < expiredplots.size()) && (i < (page * pagesize)); i++) {
                        final AthionPlot plot = expiredplots.get(i);

                        final String starttext = "  " + ChatColor.BLUE + plot.id + ChatColor.RESET + " -> " + plot.owner;

                        final int textLength = MFWC.getStringWidth(starttext);

                        final String line = starttext + AthionCommands.whitespace(550 - textLength) + "@" + plot.expireddate.toString();

                        p.sendMessage(line);
                    }
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
