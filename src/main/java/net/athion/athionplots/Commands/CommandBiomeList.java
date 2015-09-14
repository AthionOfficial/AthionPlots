package net.athion.athionplots.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.athion.athionplots.AthionPlots;
import net.athion.athionplots.Core.AthionCommands;
import net.athion.athionplots.Utils.MFWC;

import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class CommandBiomeList {

    public CommandBiomeList(final Player player, final String[] args) {
        if (AthionPlots.cPerms(player, "plotme.use.biome")) {
            AthionCommands.SendMsg(player, AthionCommands.C("WordBiomes") + " : ");

            //int i = 0;
            StringBuilder line = new StringBuilder();
            final List<String> biomes = new ArrayList<>();

            for (final Biome b : Biome.values()) {
                biomes.add(b.name());
            }

            Collections.sort(biomes);

            final List<String> column1 = new ArrayList<>();
            final List<String> column2 = new ArrayList<>();
            final List<String> column3 = new ArrayList<>();

            for (int ctr = 0; ctr < biomes.size(); ctr++) {
                if (ctr < (biomes.size() / 3)) {
                    column1.add(biomes.get(ctr));
                } else if (ctr < ((biomes.size() * 2) / 3)) {
                    column2.add(biomes.get(ctr));
                } else {
                    column3.add(biomes.get(ctr));
                }
            }

            for (int ctr = 0; ctr < column1.size(); ctr++) {
                String b;
                int nameLength;

                b = AthionCommands.FormatBiome(column1.get(ctr));
                nameLength = MFWC.getStringWidth(b);
                line.append(b).append(AthionCommands.whitespace(432 - nameLength));

                if (ctr < column2.size()) {
                    b = AthionCommands.FormatBiome(column2.get(ctr));
                    nameLength = MFWC.getStringWidth(b);
                    line.append(b).append(AthionCommands.whitespace(432 - nameLength));
                }

                if (ctr < column3.size()) {
                    b = AthionCommands.FormatBiome(column3.get(ctr));
                    line.append(b);
                }

                player.sendMessage("" + ChatColor.BLUE + line);
                //i = 0;
                line = new StringBuilder();

                /*int nameLength = MinecraftFontWidthCalculator.getStringWidth(b);

                i += 1;
                if(i == 3)
                {
                	line.append(b);
                	player.sendMessage("" + BLUE + line);
                	i = 0;
                	line = new StringBuilder();
                }
                else
                {
                	line.append(b).append(whitespace(318 - nameLength));
                }*/
            }
        } else {
            AthionCommands.SendMsg(player, ChatColor.RED + AthionCommands.C("MsgPermissionDenied"));
        }

    }

}
