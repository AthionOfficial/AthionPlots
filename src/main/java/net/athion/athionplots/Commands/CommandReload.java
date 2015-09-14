package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReload {

    public CommandReload(final CommandSender s, final String[] args) {
        if (!(s instanceof Player) || AthionPlots.cPerms(s, "plotme.admin.reload")) {
            AthionPlots.initialize();
            AthionCommands.SendMsg(s, AthionCommands.C("MsgReloadedSuccess"));
        } else {
            AthionCommands.SendMsg(s, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
