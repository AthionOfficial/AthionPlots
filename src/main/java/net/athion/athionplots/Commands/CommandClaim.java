package net.athion.athionplots.Commands;

import java.util.UUID;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;
import net.athion.athionplots.Core.AthionPlot;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class CommandClaim {

    public CommandClaim(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "AthionPlots.use.claim") || AthionPlots.cPerms(p, "AthionPlots.admin.claim.other")) {
            if (!AthionCore.isPlotWorld(p)) {
                AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgNotPlotWorld"));
            } else {
                final String id = AthionCore.getPlotID(p.getLocation());

                if (id.equals("")) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgCannotClaimRoad"));
                } else if (!AthionCore.isPlotAvailable(id, p)) {
                    AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgThisPlotOwned"));
                } else {
                    final String playername = p.getName();
                    final UUID uuid = p.getUniqueId();

                    /*if (args.length == 2) {
                        if (AthionPlots.cPerms(p, "AthionPlots.admin.claim.other")) {
                    		playername = args[1];
                    		uuid = null;
                    	}
                    }*/

                    final int plotlimit = AthionPlots.getPlotLimit(p);

                    if (playername.equals(p.getName()) && (plotlimit != -1) && (AthionCore.getNbOwnedPlot(p) >= plotlimit)) {
                        AthionCommands.SendMsg(p, ChatColor.RED
                        + AthionCommands.C("MsgAlreadyReachedMaxPlots")
                        + " ("
                        + AthionCore.getNbOwnedPlot(p)
                        + "/"
                        + AthionPlots.getPlotLimit(p)
                        + "). "
                        + AthionCommands.C("WordUse")
                        + " "
                        + ChatColor.RED
                        + "/ap "
                        + AthionCommands.C("CommandHome")
                        + ChatColor.RESET
                        + " "
                        + AthionCommands.C("MsgToGetToIt"));
                    } else {
                        final World w = p.getWorld();
                        final AthionMaps pmi = AthionCore.getMap(w);

                        double price = 0;

                        if (AthionCore.isEconomyEnabled(w)) {
                            price = pmi.ClaimPrice;
                            final double balance = AthionPlots.economy.getBalance(p);

                            if (balance >= price) {
                                final EconomyResponse er = AthionPlots.economy.withdrawPlayer(p, price);

                                if (!er.transactionSuccess()) {
                                    AthionCommands.SendMsg(p, ChatColor.RED + er.errorMessage);
                                    AthionCommands.warn(er.errorMessage);
                                }
                            } else {
                                AthionCommands.SendMsg(p, ChatColor.RED
                                + AthionCommands.C("MsgNotEnoughBuy")
                                + " "
                                + AthionCommands.C("WordMissing")
                                + " "
                                + ChatColor.RESET
                                + (price - balance)
                                + ChatColor.RED
                                + " "
                                + AthionPlots.economy.currencyNamePlural());
                            }
                        }

                        final AthionPlot plot = AthionCore.claimPlot(w, id, playername, uuid);

                        //AthionManager.adjustLinkedPlots(id, w);

                        if (plot == null) {
                            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("ErrCreatingPlotAt") + " " + id);
                        } else {
                            firework(p);
                            if (playername.equalsIgnoreCase(p.getName())) {
                                AthionCommands.SendMsg(p, AthionCommands.C("MsgThisPlotYours")
                                + " "
                                + AthionCommands.C("WordUse")
                                + " "
                                + ChatColor.RED
                                + "/ap "
                                + AthionCommands.C("CommandHome")
                                + ChatColor.RESET
                                + " "
                                + AthionCommands.C("MsgToGetToIt")
                                + " "
                                + AthionCommands.f(-price));
                            } else {
                                AthionCommands.SendMsg(p, AthionCommands.C("MsgThisPlotIsNow")
                                + " "
                                + playername
                                + AthionCommands.C("WordPossessive")
                                + ". "
                                + AthionCommands.C("WordUse")
                                + " "
                                + ChatColor.RED
                                + "/ap "
                                + AthionCommands.C("CommandHome")
                                + ChatColor.RESET
                                + " "
                                + AthionCommands.C("MsgToGetToIt")
                                + " "
                                + AthionCommands.f(-price));
                            }

                        }
                    }
                }
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }
    }

    public void firework(final Player p) {
        final Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
        final FireworkMeta meta = fw.getFireworkMeta();
        final Builder effect = FireworkEffect.builder();
        effect.with(Type.BALL);
        effect.withTrail();
        effect.withColor(Color.RED);
        effect.withFlicker();
        meta.addEffect(effect.build());
        fw.setFireworkMeta(meta);
    }

}
