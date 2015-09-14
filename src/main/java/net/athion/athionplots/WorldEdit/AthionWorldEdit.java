package net.athion.athionplots.WorldEdit;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface AthionWorldEdit {

    public void setMask(final Player p);

    public void setMask(final Player p, final Location l);

    public void removeMask(final Player p);

}
