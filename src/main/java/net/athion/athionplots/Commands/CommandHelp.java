package net.athion.athionplots.Commands;

import java.util.ArrayList;
import java.util.List;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;
import net.athion.athionplots.Core.AthionMaps;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandHelp {

    public CommandHelp(final Player p, int page) {
        final int max = 9;
        int maxpage = 0;
        final boolean ecoon = AthionCore.isEconomyEnabled(p);

        final List<String> allowed_commands = new ArrayList<>();

        allowed_commands.add("limit");
        if (AthionPlots.cPerms(p, "AthionPlots.use.claim")) {
            allowed_commands.add("claim");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.claim.other")) {
            allowed_commands.add("claim.other");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.auto")) {
            allowed_commands.add("auto");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.list")) {
            allowed_commands.add("list");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.home")) {
            allowed_commands.add("home");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.home.other")) {
            allowed_commands.add("home.other");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.info")) {
            allowed_commands.add("info");
            allowed_commands.add("biomeinfo");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.comment")) {
            allowed_commands.add("comment");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.comments")) {
            allowed_commands.add("comments");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.biome")) {
            allowed_commands.add("biome");
            allowed_commands.add("biomelist");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.clear") || AthionPlots.cPerms(p, "AthionPlots.admin.clear")) {
            allowed_commands.add("clear");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.done") || AthionPlots.cPerms(p, "AthionPlots.admin.done")) {
            allowed_commands.add("done");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.done")) {
            allowed_commands.add("donelist");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.tp")) {
            allowed_commands.add("tp");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.id")) {
            allowed_commands.add("id");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.dispose") || AthionPlots.cPerms(p, "AthionPlots.use.dispose")) {
            allowed_commands.add("dispose");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.reset")) {
            allowed_commands.add("reset");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.add") || AthionPlots.cPerms(p, "AthionPlots.admin.add")) {
            allowed_commands.add("add");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.remove") || AthionPlots.cPerms(p, "AthionPlots.admin.remove")) {
            allowed_commands.add("remove");
        }
        if (AthionPlots.allowToBlock) {
            if (AthionPlots.cPerms(p, "AthionPlots.use.block") || AthionPlots.cPerms(p, "AthionPlots.admin.block")) {
                allowed_commands.add("deny");
            }
            if (AthionPlots.cPerms(p, "AthionPlots.use.unblock") || AthionPlots.cPerms(p, "AthionPlots.admin.unblock")) {
                allowed_commands.add("undeny");
            }
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.setowner")) {
            allowed_commands.add("setowner");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.move")) {
            allowed_commands.add("move");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.weanywhere")) {
            allowed_commands.add("weanywhere");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.reload")) {
            allowed_commands.add("reload");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.list")) {
            allowed_commands.add("listother");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.expired")) {
            allowed_commands.add("expired");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.addtime")) {
            allowed_commands.add("addtime");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.admin.resetexpired")) {
            allowed_commands.add("resetexpired");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.merge")) {
            allowed_commands.add("merge");
        }
        if (AthionPlots.cPerms(p, "AthionPlots.use.schematic")) {
            allowed_commands.add("schematic");
        }

        final AthionMaps pmi = AthionCore.getMap(p);

        if (AthionCore.isPlotWorld(p) && ecoon) {
            if (AthionPlots.cPerms(p, "AthionPlots.use.buy")) {
                allowed_commands.add("buy");
            }
            if (AthionPlots.cPerms(p, "AthionPlots.use.sell")) {
                allowed_commands.add("sell");
                if (pmi.CanSellToBank) {
                    allowed_commands.add("sellbank");
                }
            }
            if (AthionPlots.cPerms(p, "AthionPlots.use.auction")) {
                allowed_commands.add("auction");
            }
            if (AthionPlots.cPerms(p, "AthionPlots.use.bid")) {
                allowed_commands.add("bid");
            }
        }

        maxpage = (int) Math.ceil((double) allowed_commands.size() / max);

        if (page > maxpage) {
            page = 1;
        }

        p.sendMessage(ChatColor.GOLD
        + "====[ "
        + ChatColor.AQUA
        + ChatColor.BOLD
        + AthionCommands.C("HelpTitle")
        + " Help "
        + ChatColor.GOLD
        + "]===="
        + ChatColor.RESET
        + "\n Page "
        + page
        + " of "
        + maxpage);

        for (int ctr = (page - 1) * max; (ctr < (page * max)) && (ctr < allowed_commands.size()); ctr++) {
            final String allowedcmd = allowed_commands.get(ctr);

            if (allowedcmd.equalsIgnoreCase("claim")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandClaim"));

            } else if (allowedcmd.equalsIgnoreCase("claim.other")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandClaim") + " <" + AthionCommands.C("WordPlayer") + ">");

            } else if (allowedcmd.equalsIgnoreCase("auto")) {
                if (AthionPlots.allowWorldTeleport) {
                    p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandAuto") + " [" + AthionCommands.C("WordWorld") + "]");
                } else {
                    p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandAuto"));
                }
            } else if (allowedcmd.equalsIgnoreCase("home")) {
                if (AthionPlots.allowWorldTeleport) {
                    p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandHome") + "[:#] [" + AthionCommands.C("WordWorld") + "]");
                } else {
                    p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandHome") + "[:#]");
                }
            } else if (allowedcmd.equalsIgnoreCase("home.other")) {
                if (AthionPlots.allowWorldTeleport) {
                    p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandHome") + "[:#] <" + AthionCommands.C("WordPlayer") + "> [" + AthionCommands.C("WordWorld") + "]");
                } else {
                    p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandHome") + "[:#] <" + AthionCommands.C("WordPlayer") + ">");
                }
            } else if (allowedcmd.equalsIgnoreCase("info")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandInfo"));
            } else if (allowedcmd.equalsIgnoreCase("comment")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandComment") + " <" + AthionCommands.C("WordComment") + ">");
            } else if (allowedcmd.equalsIgnoreCase("comments")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandComments"));
            } else if (allowedcmd.equalsIgnoreCase("list")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandList"));
            } else if (allowedcmd.equalsIgnoreCase("listother")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandList") + " <" + AthionCommands.C("WordPlayer") + ">");
            } else if (allowedcmd.equalsIgnoreCase("biomeinfo")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandBiome"));
            } else if (allowedcmd.equalsIgnoreCase("biome")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandBiome") + " <" + AthionCommands.C("WordBiome") + ">");
            } else if (allowedcmd.equalsIgnoreCase("biomelist")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandBiomelist"));
            } else if (allowedcmd.equalsIgnoreCase("done")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandDone"));
            } else if (allowedcmd.equalsIgnoreCase("tp")) {
                if (AthionPlots.allowWorldTeleport) {
                    p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandTp") + " <" + AthionCommands.C("WordId") + "> [" + AthionCommands.C("WordWorld") + "]");
                } else {
                    p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandTp") + " <" + AthionCommands.C("WordId") + ">");
                }
            } else if (allowedcmd.equalsIgnoreCase("id")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandId"));
            } else if (allowedcmd.equalsIgnoreCase("clear")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandClear"));
            } else if (allowedcmd.equalsIgnoreCase("reset")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandReset"));
            } else if (allowedcmd.equalsIgnoreCase("add")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandAdd") + " <" + AthionCommands.C("WordPlayer") + ">");
            } else if (allowedcmd.equalsIgnoreCase("deny")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandDeny") + " <" + AthionCommands.C("WordPlayer") + ">");
            } else if (allowedcmd.equalsIgnoreCase("remove")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandRemove") + " <" + AthionCommands.C("WordPlayer") + ">");
            } else if (allowedcmd.equalsIgnoreCase("undeny")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandUndeny") + " <" + AthionCommands.C("WordPlayer") + ">");
            } else if (allowedcmd.equalsIgnoreCase("setowner")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandSetowner") + " <" + AthionCommands.C("WordPlayer") + ">");
            } else if (allowedcmd.equalsIgnoreCase("move")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandMove") + " <" + AthionCommands.C("WordIdFrom") + "> <" + AthionCommands.C("WordIdTo") + ">");
            } else if (allowedcmd.equalsIgnoreCase("weanywhere")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandWEAnywhere"));
            } else if (allowedcmd.equalsIgnoreCase("expired")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandExpired") + " [page]");
            } else if (allowedcmd.equalsIgnoreCase("donelist")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandDoneList") + " [page]");
            } else if (allowedcmd.equalsIgnoreCase("addtime")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandAddtime"));
            } else if (allowedcmd.equalsIgnoreCase("reload")) {
                p.sendMessage(ChatColor.GRAY + "/ap reload");
            } else if (allowedcmd.equalsIgnoreCase("dispose")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandDispose"));
            } else if (allowedcmd.equalsIgnoreCase("buy")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandBuy"));
            } else if (allowedcmd.equalsIgnoreCase("sell")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandSell") + " [" + AthionCommands.C("WordAmount") + "]");
            } else if (allowedcmd.equalsIgnoreCase("sellbank")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandSellBank"));
            } else if (allowedcmd.equalsIgnoreCase("auction")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandAuction") + " [" + AthionCommands.C("WordAmount") + "]");
            } else if (allowedcmd.equalsIgnoreCase("resetexpired")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandResetExpired") + " <" + AthionCommands.C("WordWorld") + ">");
            } else if (allowedcmd.equalsIgnoreCase("bid")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + AthionCommands.C("CommandBid") + " <" + AthionCommands.C("WordAmount") + ">");
            } else if (allowedcmd.equalsIgnoreCase("merge")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + "merge");
            } else if (allowedcmd.equalsIgnoreCase("schematic")) {
                p.sendMessage(ChatColor.GRAY + "/ap " + "schematic" + " load" + " [schematic]");
            }
        }
    }

}
