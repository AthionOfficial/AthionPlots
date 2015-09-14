package net.athion.athionplots.Core;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.athion.athionplots.AthionPlots;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class AthionPlot {

    public String owner;
    public UUID ownerId;
    public String world;
    public Biome biome;
    public Date expireddate;
    public boolean finished;
    public List<String[]> comments;
    public String id;
    public double customprice;
    public boolean forsale;
    public String finisheddate;
    public boolean protect;
    public boolean auctionned;
    public String currentbidder;
    public UUID currentbidderId;
    public double currentbid;
    AthionPlayers allowed;
    AthionPlayers denied;

    public AthionPlot() {
        owner = "";
        ownerId = null;
        world = "";
        id = "";
        allowed = new AthionPlayers();
        denied = new AthionPlayers();
        biome = Biome.PLAINS;

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 7);
        final java.util.Date utlDate = cal.getTime();
        expireddate = new java.sql.Date(utlDate.getTime());

        comments = new ArrayList<>();
        customprice = 0;
        forsale = false;
        finisheddate = "";
        protect = false;
        auctionned = false;
        currentbidder = "";
        currentbid = 0;
        currentbidderId = null;
    }

    public AthionPlot(final String o, final Location t, final Location b, final String tid, final int days) {
        owner = o;
        ownerId = null;
        world = t.getWorld().getName();
        allowed = new AthionPlayers();
        denied = new AthionPlayers();
        biome = Biome.PLAINS;
        id = tid;

        if (days == 0) {
            expireddate = null;
        } else {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, days);
            final java.util.Date utlDate = cal.getTime();
            expireddate = new java.sql.Date(utlDate.getTime());
        }

        comments = new ArrayList<>();
        customprice = 0;
        forsale = false;
        finisheddate = "";
        protect = false;
        auctionned = false;
        currentbidder = "";
        currentbid = 0;
        currentbidderId = null;
    }

    public AthionPlot(final String o, final UUID uuid, final Location t, final Location b, final String tid, final int days) {
        owner = o;
        ownerId = uuid;
        world = t.getWorld().getName();
        allowed = new AthionPlayers();
        denied = new AthionPlayers();
        biome = Biome.PLAINS;
        id = tid;

        if (days == 0) {
            expireddate = null;
        } else {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, days);
            final java.util.Date utlDate = cal.getTime();
            expireddate = new java.sql.Date(utlDate.getTime());
        }

        comments = new ArrayList<>();
        customprice = 0;
        forsale = false;
        finisheddate = "";
        protect = false;
        auctionned = false;
        currentbidder = "";
        currentbid = 0;
        currentbidderId = null;
    }

    public AthionPlot(final UUID o, final Location t, final Location b, final String tid, final int days) {
        ownerId = o;
        final Player p = Bukkit.getPlayer(o);
        if (p != null) {
            owner = p.getName();
        } else {
            owner = "";
        }
        world = t.getWorld().getName();
        allowed = new AthionPlayers();
        denied = new AthionPlayers();
        biome = Biome.PLAINS;
        id = tid;

        if (days == 0) {
            expireddate = null;
        } else {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, days);
            final java.util.Date utlDate = cal.getTime();
            expireddate = new java.sql.Date(utlDate.getTime());
        }

        comments = new ArrayList<>();
        customprice = 0;
        forsale = false;
        finisheddate = "";
        protect = false;
        auctionned = false;
        currentbidder = "";
        currentbid = 0;
        currentbidderId = null;
    }

    public AthionPlot(final String o, final String w, final int tX, final int bX, final int tZ, final int bZ, final String bio, final Date exp, final boolean fini, final AthionPlayers al,
    final List<String[]> comm, final String tid, final double custprice, final boolean sale, final String finishdt, final boolean prot, final String bidder, final Double bid,
    final boolean isauctionned, final AthionPlayers den) {
        owner = o;
        ownerId = null;
        world = w;
        biome = Biome.valueOf(bio);
        expireddate = exp;
        finished = fini;
        allowed = al;
        comments = comm;
        id = tid;
        customprice = custprice;
        forsale = sale;
        finisheddate = finishdt;
        protect = prot;
        auctionned = isauctionned;
        currentbidder = bidder;
        currentbid = bid;
        denied = den;
    }

    AthionPlot(final String o, final UUID uuid, final String w, final int tX, final int bX, final int tZ, final int bZ, final String bio, final Date exp, final boolean fini, final AthionPlayers al,
    final List<String[]> comm, final String tid, final double custprice, final boolean sale, final String finishdt, final boolean prot, final String bidder, final UUID bidderId, final Double bid,
    final boolean isauctionned, final AthionPlayers den) {
        ownerId = uuid;
        owner = o;
        world = w;
        biome = Biome.valueOf(bio);
        expireddate = exp;
        finished = fini;
        allowed = al;
        comments = comm;
        id = tid;
        customprice = custprice;
        forsale = sale;
        finisheddate = finishdt;
        protect = prot;
        auctionned = isauctionned;
        currentbidder = bidder;
        currentbid = bid;
        denied = den;
        if (bidderId == null) {
            currentbidderId = null;
        } else {
            currentbidderId = bidderId;
        }
    }

    public void resetExpire(final int days) {
        if (days == 0) {
            if (expireddate != null) {
                expireddate = null;
                updateField("expireddate", expireddate);
            }
        } else {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, days);
            final java.util.Date utlDate = cal.getTime();
            final java.sql.Date temp = new java.sql.Date(utlDate.getTime());
            if ((expireddate == null) || !temp.toString().equalsIgnoreCase(expireddate.toString())) {
                expireddate = temp;
                updateField("expireddate", expireddate);
            }
        }
    }

    public String getExpire() {
        return DateFormat.getDateInstance().format(expireddate);
    }

    public void setExpire(final Date date) {
        if (!expireddate.equals(date)) {
            expireddate = date;
            updateField("expireddate", expireddate);
        }
    }

    public void setFinished() {
        finisheddate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
        finished = true;

        updateFinished(finisheddate, finished);
    }

    public void setUnfinished() {
        finisheddate = "";
        finished = false;

        updateFinished(finisheddate, finished);
    }

    public Biome getBiome() {
        return biome;
    }

    public String getOwner() {
        return owner;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getAllowed() {
        return allowed.getPlayerList();
    }

    public String getDenied() {
        return denied.getPlayerList();
    }

    public int getCommentsCount() {
        return comments.size();
    }

    public String[] getComments(final int i) {
        return comments.get(i);
    }

    public void addAllowed(final String name) {
        if (!isAllowedConsulting(name)) {
            allowed.put(name);
            AthionSQL.addPlotAllowed(name, null, AthionCore.getIdX(id), AthionCore.getIdZ(id), world);
        }
    }

    public void addAllowed(final UUID uuid) {
        if (!isAllowed(uuid)) {
            final String name = allowed.put(uuid);
            AthionSQL.addPlotAllowed(name, uuid, AthionCore.getIdX(id), AthionCore.getIdZ(id), world);
        }
    }

    public void addDenied(final String name) {
        if (!isDeniedConsulting(name)) {
            denied.put(name);
            AthionSQL.addPlotDenied(name, null, AthionCore.getIdX(id), AthionCore.getIdZ(id), world);
        }
    }

    public void addDenied(final UUID uuid) {
        if (!isDenied(uuid)) {
            final String name = denied.put(uuid);
            AthionSQL.addPlotDenied(name, uuid, AthionCore.getIdX(id), AthionCore.getIdZ(id), world);
        }
    }

    public void removeAllowed(final String name) {
        if (allowed.contains(name)) {
            final UUID uuid = allowed.remove(name);
            AthionSQL.deletePlotAllowed(AthionCore.getIdX(id), AthionCore.getIdZ(id), name, uuid, world);

            if (AthionPlots.worldeditplugin != null) {
                final Player p = Bukkit.getPlayer(uuid);

                if (p != null) {
                    if (AthionCore.isPlotWorld(p.getWorld())) {
                        if (!AthionPlots.isIgnoringWELimit(p)) {
                            AthionPlots.athionworldedit.setMask(p);
                        } else {
                            AthionPlots.athionworldedit.removeMask(p);
                        }
                    }
                }
            }
        }
    }

    public void removeAllowedGroup(final String name) {
        if (allowed.contains(name)) {
            allowed.remove(name);
            AthionSQL.deletePlotAllowed(AthionCore.getIdX(id), AthionCore.getIdZ(id), name, null, world);
        }
    }

    public void removeAllowed(final UUID uuid) {
        if (allowed.contains(uuid)) {
            final String name = allowed.remove(uuid);
            AthionSQL.deletePlotAllowed(AthionCore.getIdX(id), AthionCore.getIdZ(id), name, uuid, world);

            if (AthionPlots.worldeditplugin != null) {
                final Player p = Bukkit.getPlayer(uuid);

                if (p != null) {
                    if (AthionCore.isPlotWorld(p.getWorld())) {
                        if (!AthionPlots.isIgnoringWELimit(p)) {
                            AthionPlots.athionworldedit.setMask(p);
                        } else {
                            AthionPlots.athionworldedit.removeMask(p);
                        }
                    }
                }
            }
        }
    }

    public void removeDenied(final String name) {
        if (denied.contains(name)) {
            final UUID uuid = denied.remove(name);
            AthionSQL.deletePlotDenied(AthionCore.getIdX(id), AthionCore.getIdZ(id), name, uuid, world);
        }
    }

    public void removeDeniedGroup(final String name) {
        if (denied.contains(name)) {
            denied.remove(name);
            AthionSQL.deletePlotDenied(AthionCore.getIdX(id), AthionCore.getIdZ(id), name, null, world);
        }
    }

    public void removeDenied(final UUID uuid) {
        if (denied.contains(uuid)) {
            final String name = denied.remove(uuid);
            AthionSQL.deletePlotDenied(AthionCore.getIdX(id), AthionCore.getIdZ(id), name, uuid, world);
        }
    }

    public void removeAllAllowed() {
        final HashMap<String, UUID> list = allowed.getAllPlayers();
        for (final String n : list.keySet()) {
            final UUID uuid = list.get(n);
            AthionSQL.deletePlotAllowed(AthionCore.getIdX(id), AthionCore.getIdZ(id), n, uuid, world);
        }
        allowed.clear();
    }

    public void removeAllDenied() {
        final HashMap<String, UUID> list = denied.getAllPlayers();
        for (final String n : list.keySet()) {
            final UUID uuid = list.get(n);
            AthionSQL.deletePlotDenied(AthionCore.getIdX(id), AthionCore.getIdZ(id), n, uuid, world);
        }
        denied.clear();
    }

    @Deprecated
    public boolean isAllowed(final String name) {
        final Player p = Bukkit.getServer().getPlayerExact(name);
        if (p == null) {
            return false;
        } else {
            return isAllowedInternal(p.getName(), p.getUniqueId(), true, true);
        }
    }

    public boolean isAllowedConsulting(final String name) {
        @SuppressWarnings("deprecation")
        final Player p = Bukkit.getServer().getPlayerExact(name);
        if (p != null) {
            return isAllowedInternal(name, p.getUniqueId(), true, true);
        } else {
            return isAllowedInternal(name, null, true, true);
        }
    }

    public boolean isGroupAllowed(final String name) {
        return isAllowedInternal(name, null, true, true);
    }

    public boolean isAllowed(final String name, final UUID uuid) {
        return isAllowedInternal(name, uuid, true, true);
    }

    public boolean isAllowed(final UUID uuid) {
        return isAllowedInternal("", uuid, true, true);
    }

    @Deprecated
    public boolean isAllowed(final String name, final boolean IncludeStar, final boolean IncludeGroup) {
        final Player p = Bukkit.getServer().getPlayerExact(name);
        if (p == null) {
            return false;
        } else {
            return isAllowedInternal(p.getName(), p.getUniqueId(), IncludeStar, IncludeGroup);
        }
    }

    private boolean isAllowedInternal(final String name, final UUID uuid, final boolean IncludeStar, final boolean IncludeGroup) {

        if (IncludeStar && owner.equals("*")) {
            return true;
        }

        Player p = null;

        if (uuid != null) {
            p = Bukkit.getServer().getPlayer(uuid);
        }

        if ((uuid != null) && (ownerId != null) && ownerId.equals(uuid)) {
            return true;
        } else if ((uuid == null) && owner.equalsIgnoreCase(name)) {
            return true;
        }

        if (IncludeGroup && owner.toLowerCase().startsWith("group:") && (p != null)) {
            if (p.hasPermission("plotme.group." + owner.replace("Group:", ""))) {
                return true;
            }
        }

        final HashMap<String, UUID> list = allowed.getAllPlayers();
        for (final String str : list.keySet()) {
            if (IncludeStar && str.equals("*")) {
                return true;
            }

            final UUID u = list.get(str);
            if ((u != null) && (uuid != null) && u.equals(uuid)) {
                return true;
            } else if ((uuid == null) && str.equalsIgnoreCase(name)) {
                return true;
            }

            if (IncludeGroup && str.toLowerCase().startsWith("group:") && (p != null)) {
                if (p.hasPermission("plotme.group." + str.replace("Group:", ""))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Deprecated
    public boolean isDenied(final String name) {
        final Player p = Bukkit.getServer().getPlayerExact(name);
        if (p == null) {
            return false;
        } else {
            return isDeniedInternal(name, null, true, true);
        }
    }

    public boolean isDeniedConsulting(final String name) {
        @SuppressWarnings("deprecation")
        final Player p = Bukkit.getServer().getPlayerExact(name);
        if (p != null) {
            return isDeniedInternal(name, p.getUniqueId(), true, true);
        } else {
            return isDeniedInternal(name, null, true, true);
        }
    }

    public boolean isGroupDenied(final String name) {
        return isDeniedInternal(name, null, true, true);
    }

    public boolean isDenied(final UUID uuid) {
        return isDeniedInternal("", uuid, true, true);
    }

    private boolean isDeniedInternal(final String name, final UUID uuid, final boolean IncludeStar, final boolean IncludeGroup) {
        Player p = null;

        if (isAllowedInternal(name, uuid, false, false)) {
            return false;
        }

        if (uuid != null) {
            p = Bukkit.getServer().getPlayer(uuid);
        }

        final HashMap<String, UUID> list = denied.getAllPlayers();
        for (final String str : list.keySet()) {
            if (str.equals("*")) {
                return true;
            }

            final UUID u = list.get(str);
            if ((u != null) && (uuid != null) && u.equals(uuid)) {
                return true;
            } else if ((uuid == null) && str.equalsIgnoreCase(name)) {
                return true;
            }

            if (IncludeGroup && str.toLowerCase().startsWith("group:") && (p != null)) {
                if (p.hasPermission("plotme.group." + str.replace("Group:", ""))) {
                    return true;
                }
            }
        }

        return false;
    }

    public Set<String> allowed() {
        return allowed.getPlayers();
    }

    public Set<String> denied() {
        return denied.getPlayers();
    }

    public int allowedcount() {
        return allowed.size();
    }

    public int deniedcount() {
        return denied.size();
    }

    public int compareTo(final AthionPlot plot) {
        if (expireddate.compareTo(plot.expireddate) == 0) {
            return owner.compareTo(plot.owner);
        } else {
            return expireddate.compareTo(plot.expireddate);
        }
    }

    private void updateFinished(final String finishtime, final boolean isfinished) {
        updateField("finisheddate", finishtime);
        updateField("finished", isfinished);
    }

    public void updateField(final String field, final Object value) {
        AthionSQL.updatePlot(AthionCore.getIdX(id), AthionCore.getIdZ(id), world, field, value);
    }

}
