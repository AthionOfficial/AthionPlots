package net.athion.athionplots.Commands;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Core.AthionCore;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandPlugin {

    public CommandPlugin(final Player p, final String[] args) {
        AthionCommands.SendMsg(p, ChatColor.RED + AthionCommands.C("MsgCredit"));
    }

}
