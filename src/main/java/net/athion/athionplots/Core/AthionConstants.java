package net.athion.athionplots.Core;

import org.bukkit.ChatColor;

public interface AthionConstants {

    /*
     * Constants are defined for the purpose of current and future use by the Engine. This will include pre-defined messages, errors and
     * dynamic response for the Engine usage.
     */

    /* DEFAULT Variables AND Settings */
    public static final String PluginTag = ChatColor.GOLD + "[" + ChatColor.GREEN + "AthionPlots" + ChatColor.GOLD + "] ";
    // Codes x200 (Engine Errors)
    public static final String x0200 = ChatColor.RED + "Error (x200): Invalid Argument Input or Amount"; /* Invalid Input OR Argument */
    public static final String x0201 = ChatColor.RED + "Error (x201): The engine could not perform this action."; /* An internal Engine error has occurred. Please look at the console for the specific error, or debug the system. */

    /* Response Codes */
    // Codes x250 (User Errors)
    public static final String x250 = PluginTag + ChatColor.RED + "You lack the permission to perform this Command.";
    public static final String x251 = PluginTag + ChatColor.RED + "You lack the permission to perform this Action.";
    public static final String x252 = PluginTag + ChatColor.RED + "You lack the permission to Build or Destroy in this plot.";
    public static final String x253 = PluginTag + ChatColor.RED + "You lack the permission to Interact OR Action-Use in this plot.";
    public static final String x254 = PluginTag + ChatColor.RED + "This is not your plot. Please type /ap myplot [plot #]";
    // Codes x350 (Command Usage)
    public static final String x350 = PluginTag + ChatColor.YELLOW + "To Claim a plot, please type /ap claim";
    public static final String x351 = PluginTag + ChatColor.YELLOW + "To Auto-Claim a plot, please type /ap auto";

    // Codes x300 (Connection Errors)
    public static final String x352 = PluginTag + ChatColor.YELLOW + "To go to your plot, please type /ap myplot [plot #]";
    public static final String x353 = PluginTag + ChatColor.YELLOW + "To view all your plots, type /ap myplots";
    public static final String x354 = PluginTag + ChatColor.YELLOW + "To view information about the plot you're in, type /ap info";
    public static final String x355 = PluginTag + ChatColor.YELLOW + "To leave a comment on the plot you're in, please type /ap comment <text>";
    public static final String x356 = PluginTag + ChatColor.YELLOW + "To view comments on the plot you're in, please type /ap comments";
    public static final String x357 = PluginTag + ChatColor.YELLOW + "To view this plots Biome, please type /ap biome";
    public static final String x358 = PluginTag + ChatColor.YELLOW + "To load a Biome onto your plot, please type /ap biome <biome_type>";
    public static final String x359 = PluginTag + ChatColor.YELLOW + "To view all possible biomes, please type /ap biomelist";
    public static final String x360 = PluginTag + ChatColor.YELLOW + "To clear your plot of all Blocks, Items and Entities to its original state, please type /ap clear";
    public static final String x361 = PluginTag + ChatColor.YELLOW + "To give a player a permission, please type /ap perm give <player> <permission> [plot #]";
    public static final String x362 = PluginTag + ChatColor.YELLOW + "To remove a players permission, please type /ap perm remove <player> <permission> [plot #]";
    public static final String x363 = PluginTag + ChatColor.YELLOW + "To clear a players permissions entirely, please type /ap perm clear <player> [plot #]";
    public static final String x364 = PluginTag + ChatColor.YELLOW + "To protect your plot you're standing on, please type /ap protect";
    public static final String x365 = PluginTag + ChatColor.YELLOW + "To delete or dispose of your plot your standing on, please type /ap dispose";
    public static final String x366 = PluginTag + ChatColor.YELLOW + "To flag your plot as done, please stand in that plot and type /ap done";
    public static final String x367 = PluginTag + ChatColor.YELLOW + "To block a player from building, interacting and entering your plot, please type /ap block <player>";
    public static final String x368 = PluginTag + ChatColor.YELLOW + "To unblock a player, please type /ap unblock <player>";

    /*
     * Permissions
     * Defined permissions to use globally throughout the engine. This storage method is for the purpose of organization and representation
     * of all permissions used, this will avoid confusion for future changes, etc.
     */
    public enum Permissions {
        user_command_claim,
        user_command_auto,
        user_command_home,
        user_command_list,
        user_command_info,
        user_command_comment,
        user_command_comments,
        user_command_biome,
        user_command_biome_set,
        user_command_biome_list,
        user_command_clear,
        user_command_perm_give,
        user_command_perm_remove,
        user_command_protect,
        user_command_dispose,
        user_command_done,
        user_command_block,
        user_command_unblock,
        user_action_pvp,
        user_action_build,
        user_action_destroy,
        user_action_use,
        user_action_switch,
        admin_command_claim,
        admin_command_home,
        admin_command_tp,
        admin_command_id,
        admin_command_reset,
        admin_command_buy,
        admin_command_sell,
        admin_command_auction,
        admin_command_dispose,
        admin_command_done,
        admin_command_setowner,
        admin_command_move,
        admin_command_we,
        admin_command_reload,
        admin_command_vs
    }
    /*
     * Commands
     * Defined commands the user and administrator may use through this plugin. If the command is not defined herein, it is possible for it to cause
     * unknown affects. The command usage structure is designed to utilize multiple commands in a single environment efficiently.
     */
    public enum Commands {
        user_claim,
        user_auto,
        user_home,
        user_list,
        user_info,
        user_comment,
        user_comments,
        user_biome,
        user_biome_set,
        user_biome_list,
        user_clear,
        user_perm_give,
        user_perm_remove,
        user_protect,
        user_dispose,
        user_done,
        user_block,
        user_unblock,
        admin_claim,
        admin_home,
        admin_tp,
        admin_id,
        admin_reset,
        admin_buy,
        admin_sell,
        admin_auction,
        admin_dispose,
        admin_done,
        admin_setowner,
        admin_move,
        admin_we,
        admin_reload,
        admin_vs,
    }

    /* Messages */

    /* Dynamic Responses */

}
