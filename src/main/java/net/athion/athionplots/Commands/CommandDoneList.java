package net.athion.athionplots.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionComparator;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.athion.athionplots.Utils.MFWC;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandDoneList {

    public CommandDoneList(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "AthionPlots.admin.done")) {
            final AthionMaps pmi = AthionCore.getMap(p);

            if (pmi == null) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));

            } else {

                final HashMap<String, AthionPlot> plots = pmi.plots;
                final List<AthionPlot> finishedplots = new ArrayList<>();
                int nbfinished = 0;
                int maxpage;
                final int pagesize = 8;
                int page = 1;

                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (final NumberFormatException ignored) {}
                }

                for (final String id : plots.keySet()) {
                    final AthionPlot plot = plots.get(id);

                    if (plot.finished) {
                        finishedplots.add(plot);
                        nbfinished++;
                    }
                }

                Collections.sort(finishedplots, new AthionComparator());

                maxpage = (int) Math.ceil(((double) nbfinished / (double) pagesize));

                if (finishedplots.size() == 0) {
                    AthionCommands.SendMsg(p, AthionCommands.C("MsgNoPlotsFinished"));
                } else {
                    AthionCommands.SendMsg(p, AthionCommands.C("MsgFinishedPlotsPage") + " " + page + "/" + maxpage);

                    for (int i = (page - 1) * pagesize; (i < finishedplots.size()) && (i < (page * pagesize)); i++) {
                        final AthionPlot plot = finishedplots.get(i);

                        final String starttext = "  " + ChatColor.BLUE + plot.id + ChatColor.RESET + " -> " + plot.owner;

                        final int textLength = MFWC.getStringWidth(starttext);

                        final String line = starttext + AthionCommands.whitespace(550 - textLength) + "@" + plot.finisheddate;

                        p.sendMessage(line);
                    }
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
