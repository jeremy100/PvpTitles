package es.jlh.pvptitles.Commands;

import es.jlh.pvptitles.Events.FameAddEvent;
import es.jlh.pvptitles.Events.FameSetEvent;
import es.jlh.pvptitles.Files.LangFile;
import es.jlh.pvptitles.Files.LangFile.LangType;
import es.jlh.pvptitles.Main.Manager;
import es.jlh.pvptitles.Main.PvpTitles;
import static es.jlh.pvptitles.Main.PvpTitles.PLUGIN;
import es.jlh.pvptitles.Misc.Localizer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author AlternaCraft
 */
public class FameCommand implements CommandExecutor {

    private PvpTitles pt = null;
    private Manager dm = null;

    public FameCommand(PvpTitles pt) {
        this.pt = pt;
        this.dm = pt.manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        LangType messages = (sender instanceof Player) ? Localizer.getLocale((Player) sender) : Manager.messages;

        if (args.length == 0 || args.length > 5) {
            sender.sendMessage(PLUGIN + LangFile.COMMAND_ARGUMENTS.getText(messages));
            return false;
        }

        try {
            return this.modRank(sender, args, messages);
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Método para aniadir/ver/modificar los puntos de fama de un jugador
     *
     * @param sender Jugador que ejecuta el comando
     * @param args Parametros del comando
     * @return Booleano con el resultado de la ejecucion
     */
    private boolean modRank(CommandSender sender, String[] args, LangType messages)
            throws NumberFormatException {
        OfflinePlayer opl = null;

        if (args.length < 1) {
            return false;
        } else if (args.length > 1) {
            opl = pt.getServer().getOfflinePlayer(args[1]);

            // Evitar NullPointerException
            if (opl == null) {
                sender.sendMessage(PLUGIN + ChatColor.RED + args[1] + " doesn't "
                        + "exist");
                return true;
            }
        }

        switch (args[0]) {
            case "add":
                // <editor-fold defaultstate="collapsed" desc="ADD">
                if (args.length >= 3) {
                    // Evitar nullpointerexception
                    int fameIncr = 0;
                    String world = null;

                    if (dm.params.isMw_enabled()) {
                        if (args.length < 4) {
                            sender.sendMessage(PLUGIN + ChatColor.RED + "Syntax: 'pvpfame add <player> <world_name> <famepoints>'");
                            return true;
                        }

                        world = args[2];
                        if (pt.getServer().getWorld(world) == null) {
                            sender.sendMessage(PLUGIN + ChatColor.RED + "World \"" + world + "\" does not exist");
                            return true;
                        }
                        fameIncr = Integer.parseInt(args[3]);
                    } else {
                        fameIncr = Integer.parseInt(args[2]);
                    }
                    //

                    int fameA = this.dm.dbh.getDm().loadPlayerFame(opl.getUniqueId(), world);

                    FameAddEvent event = new FameAddEvent(opl, fameA, fameIncr);
                    if (dm.params.isMw_enabled()) {
                        event.setWorldname(world);
                        if (args.length >= 5 && args[4].contains("-s")) {
                            event.setSilent(true);
                        }
                    } else if (args.length >= 4 && args[3].contains("-s")) {
                        event.setSilent(true);
                    }

                    pt.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        if (!this.dm.dbh.getDm().savePlayerFame(opl.getUniqueId(), event.getFameTotal(), world)) {
                            PvpTitles.logError("Error saving player fame to " + opl.getName(), null);
                            event.setCancelled(true);
                        } else {
                            sender.sendMessage(PLUGIN + LangFile.FAME_ADD.getText(messages).
                                    replace("%tag%", this.dm.params.getTag())
                            );
                        }
                    } else {
                        sender.sendMessage(PLUGIN + LangFile.FAME_MODIFY_ERROR.getText(messages).
                                replace("%player%", args[1]).
                                replace("%tag%", this.dm.params.getTag())
                        );
                    }
                } else {
                    sender.sendMessage(PLUGIN + ChatColor.RED + "Syntax: 'pvpfame add <player> [<world_name>] <famepoints>'");
                }
                return true;
            // </editor-fold>
            case "see":
                // <editor-fold defaultstate="collapsed" desc="SEE">
                if (args.length == 2 || args.length == 3) {
                    int fameTotal = 0;

                    if (dm.params.isMw_enabled()) {
                        if (args.length == 2) {
                            String world = pt.getServer().getWorlds().get(0).getName();

                            if (sender instanceof Player) {
                                Player pl = (Player) sender;
                                world = pl.getWorld().getName();
                            }

                            fameTotal = this.dm.dbh.getDm().loadPlayerFame(opl.getUniqueId(), world);
                        } else {
                            fameTotal = this.dm.dbh.getDm().loadPlayerFame(opl.getUniqueId(), args[2]);
                        }
                    } else {
                        fameTotal = this.dm.dbh.getDm().loadPlayerFame(opl.getUniqueId(), null);
                    }

                    sender.sendMessage(PLUGIN + LangFile.FAME_SEE.getText(messages).
                            replace("%player%", args[1]).
                            replace("%fame%", String.valueOf(fameTotal)).
                            replace("%tag%", this.dm.params.getTag())
                    );
                } else {
                    sender.sendMessage(PLUGIN + ChatColor.RED + "Syntax: 'pvpfame see <player> [<world_name>]'");
                }
                return true;
            // </editor-fold>
            case "set":
                // <editor-fold defaultstate="collapsed" desc="SET">
                if (args.length >= 3) {
                    // Evitar nullpointerexception
                    int fameTotal = 0;
                    String world = null;

                    if (dm.params.isMw_enabled()) {
                        if (args.length < 4) {
                            sender.sendMessage(PLUGIN + ChatColor.RED + "Syntax: 'pvpfame set <player> <world_name> <famepoints>'");
                            return true;
                        }

                        world = args[2];
                        if (pt.getServer().getWorld(world) == null) {
                            sender.sendMessage(PLUGIN + ChatColor.RED + "World \"" + world + "\" does not exist");
                            return true;
                        }
                        fameTotal = Integer.valueOf(args[3]);
                    } else {
                        fameTotal = Integer.valueOf(args[2]);
                    }
                    //

                    int fame = this.dm.dbh.getDm().loadPlayerFame(opl.getUniqueId(), world);

                    fameTotal = (fameTotal < 0) ? 0 : fameTotal;

                    FameSetEvent event = new FameSetEvent(opl, fame, fameTotal);                    
                    if (dm.params.isMw_enabled()) {
                        event.setWorldname(world);
                        if (args.length >= 5 && args[4].contains("-s")) {
                            event.setSilent(true);
                        }
                    } else if (args.length >= 4 && args[3].contains("-s")) {
                        event.setSilent(true);
                    }

                    pt.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        if (!this.dm.dbh.getDm().savePlayerFame(opl.getUniqueId(), event.getFameTotal(), world)) {
                            PvpTitles.logError("Error saving player fame to " + opl.getName(), null);
                            event.setCancelled(true);
                        } else {
                            sender.sendMessage(PLUGIN + LangFile.FAME_SET.getText(messages).
                                    replace("%tag%", this.dm.params.getTag()));
                        }
                    } else {
                        sender.sendMessage(PLUGIN + LangFile.FAME_MODIFY_ERROR.getText(messages).
                                replace("%player%", args[1]).
                                replace("%tag%", this.dm.params.getTag())
                        );
                    }
                } else {
                    sender.sendMessage(PLUGIN + ChatColor.RED + "Syntax: 'pvpfame set <player> [<world_name>] <famepoints>'");
                }
                return true;
            // </editor-fold>
            default:
                break;
        }

        return false;
    }
}
