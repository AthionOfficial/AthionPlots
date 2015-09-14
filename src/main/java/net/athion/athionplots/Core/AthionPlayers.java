package net.athion.athionplots.Core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;

public class AthionPlayers {
    private final HashMap<String, UUID> playerlist;

    public AthionPlayers() {
        playerlist = new HashMap<>();
    }

    public AthionPlayers(final HashMap<String, UUID> players) {
        playerlist = players;
    }

    public void put(final String name) {
        put(name, null);
    }

    public void put(final String name, final UUID uuid) {
        playerlist.put(name, uuid);
    }

    public String put(final UUID uuid) {
        final String name = Bukkit.getOfflinePlayer(uuid).getName();
        playerlist.put(name, uuid);
        return name;
    }

    public UUID remove(final String name) {
        String found = "";
        UUID uuid = null;
        for (final String key : playerlist.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                found = key;
                continue;
            }
        }
        if (!found.equals("")) {
            uuid = playerlist.get(found);
            playerlist.remove(found);
        }
        return uuid;
    }

    public String remove(final UUID uuid) {
        for (final String name : playerlist.keySet()) {
            if (playerlist.get(name).equals(uuid)) {
                playerlist.remove(name);
                return name;
            }
        }
        return "";
    }

    public Set<String> getPlayers() {
        return playerlist.keySet();
    }

    public String getPlayerList() {
        StringBuilder list = new StringBuilder();

        for (final String s : playerlist.keySet()) {
            list = list.append(s + ", ");
        }
        if (list.length() > 1) {
            list = list.delete(list.length() - 2, list.length());
        }
        if (list.toString() == null) {
            return "";
        } else {
            return list.toString();
        }
    }

    public boolean contains(final String name) {
        for (final String key : playerlist.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(final UUID uuid) {
        return playerlist.values().contains(uuid);
    }

    public HashMap<String, UUID> getAllPlayers() {
        return playerlist;
    }

    public void clear() {
        playerlist.clear();
    }

    public int size() {
        return playerlist.size();
    }

    public void replace(final UUID uuid, final String newname) {
        if ((uuid != null) && (playerlist != null)) {
            if (this.contains(uuid)) {
                final Iterator<String> it = playerlist.keySet().iterator();
                while (it.hasNext()) {
                    final String name = it.next();

                    if ((playerlist.get(name) != null) && playerlist.get(name).equals(uuid)) {
                        playerlist.remove(name);
                        playerlist.put(newname, uuid);
                        return;
                    }
                }
            }
        }
    }

    public void replace(final String name, final UUID newuuid) {
        if ((newuuid != null) && (playerlist != null)) {
            if (this.contains(name)) {
                final Iterator<String> it = playerlist.keySet().iterator();
                while (it.hasNext()) {
                    final String key = it.next();

                    if (key.equalsIgnoreCase(name)) {
                        playerlist.remove(key);
                        playerlist.put(name, newuuid);
                        return;
                    }
                }
            }
        }
    }
}
