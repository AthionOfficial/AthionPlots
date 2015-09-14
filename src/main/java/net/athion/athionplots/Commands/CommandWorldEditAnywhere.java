package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandWorldEditAnywhere {

    public CommandWorldEditAnywhere(final Player p, final String[] args) {
        if (AthionPlots.cPerms(p, "plotme.admin.weanywhere")) {
            p.getName();

            if ((AthionPlots.isIgnoringWELimit(p) && !AthionPlots.defaultWEAnywhere) || (!AthionPlots.isIgnoringWELimit(p) && AthionPlots.defaultWEAnywhere)) {
                AthionPlots.removeIgnoreWELimit(p);
            } else {
                AthionPlots.addIgnoreWELimit(p);
            }

            if (AthionPlots.isIgnoringWELimit(p)) {
                AthionCommands.SendMsg(p, AthionCommands.C("MsgWorldEditAnywhere"));
            } else {
                AthionCommands.SendMsg(p, AthionCommands.C("MsgWorldEditInYourPlots"));
            }
        } else {
            AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
