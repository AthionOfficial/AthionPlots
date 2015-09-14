package net.athion.athionplots.Core;

import java.util.Comparator;

public class AthionComparator implements Comparator<AthionPlot> {

    @Override
    public int compare(final AthionPlot plot1, final AthionPlot plot2) {
        if (plot1.finisheddate.compareTo(plot2.finisheddate) == 0) {
            return plot1.owner.compareTo(plot2.owner);
        } else {
            return plot1.finisheddate.compareTo(plot2.finisheddate);
        }
    }

}
