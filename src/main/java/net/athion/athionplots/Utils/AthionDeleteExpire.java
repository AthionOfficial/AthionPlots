package net.athion.athionplots.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionPlot;
import net.athion.athionplots.Core.AthionSQL;

import org.bukkit.ChatColor;
import org.bukkit.World;

public class AthionDeleteExpire implements Runnable {

    @Override
    public void run() {
        if (AthionPlots.worldcurrentlyprocessingexpired != null) {
            final World w = AthionPlots.worldcurrentlyprocessingexpired;
            final List<AthionPlot> expiredplots = new ArrayList<AthionPlot>();
            HashMap<String, AthionPlot> plots = AthionCore.getPlots(w);
            final String date = AthionPlots.getDate();
            AthionPlot expiredplot;

            for (final String id : plots.keySet()) {
                final AthionPlot plot = plots.get(id);

                if (!plot.protect && !plot.finished && (plot.expireddate != null) && (AthionPlots.getDate(plot.expireddate).compareTo(date.toString()) < 0)) {
                    expiredplots.add(plot);
                }

                if (expiredplots.size() == AthionPlots.nbperdeletionprocessingexpired) {
                    break;
                }
            }

            if (expiredplots.size() == 0) {
                AthionPlots.counterexpired = 0;
            } else {
                plots = null;

                //Collections.sort(expiredplots);

                String ids = "";

                for (int ictr = 0; (ictr < AthionPlots.nbperdeletionprocessingexpired) && (expiredplots.size() > 0); ictr++) {
                    expiredplot = expiredplots.get(0);

                    expiredplots.remove(0);

                    AthionCore.clear(w, expiredplot);

                    final String id = expiredplot.id;
                    ids += ChatColor.RED + id + ChatColor.RESET + ", ";

                    AthionCore.getPlots(w).remove(id);

                    AthionCore.removeOwnerSign(w, id);
                    AthionCore.removeSellSign(w, id);

                    AthionSQL.deletePlot(AthionCore.getIdX(id), AthionCore.getIdZ(id), w.getName().toLowerCase());

                    AthionPlots.counterexpired--;
                }

                if (ids.substring(ids.length() - 2).equals(", ")) {
                    ids = ids.substring(0, ids.length() - 2);
                }

                AthionPlots.cscurrentlyprocessingexpired.sendMessage("" + AthionPlots.PREFIX + AthionPlots.caption("MsgDeletedExpiredPlots") + " " + ids);
            }

            if (AthionPlots.counterexpired == 0) {
                AthionPlots.cscurrentlyprocessingexpired.sendMessage("" + AthionPlots.PREFIX + AthionPlots.caption("MsgDeleteSessionFinished"));
                AthionPlots.worldcurrentlyprocessingexpired = null;
                AthionPlots.cscurrentlyprocessingexpired = null;
            }
        }
    }
}
