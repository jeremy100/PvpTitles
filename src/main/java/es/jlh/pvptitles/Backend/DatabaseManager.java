package es.jlh.pvptitles.Backend;

import es.jlh.pvptitles.Managers.BoardsCustom.SignBoardData;
import es.jlh.pvptitles.Managers.BoardsCustom.SignBoard;
import es.jlh.pvptitles.Objects.PlayerFame;
import es.jlh.pvptitles.Managers.Timer.TimedPlayer;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author AlternaCraft
 */
public interface DatabaseManager {

    /**
     * Método para gestionar los jugadores cuando entran y cuando salen del
     * server
     * <p>
     * Registra el jugador en la bd
     * </p>
     *
     * @param player Player
     *
     * @return False si no se pudo completar
     */
    public boolean playerConnection(Player player);

    /**
     * Método para guardar la fama obtenida por un jugador
     * <p>
     * Guarda los puntos del jugador
     * </p>
     *
     * @param playerUUID UUID
     * @param fame Entero con los puntos PvP
     * @param world En caso de MW activado, opción para establecer puntos en un
     * mundo específico
     *
     * @return False si no se pudo completar
     */
    public boolean savePlayerFame(UUID playerUUID, int fame, String world);

    /**
     * Método para cargar los puntos pvp de un jugador
     *
     * @param playerUUID
     * @param world En caso de MW activado, opción para ver puntos en un mundo
     * específico
     *
     * @return Entero con la fama del jugador
     */
    public int loadPlayerFame(UUID playerUUID, String world);

    /**
     * Método para crear o añadir el tiempo de juego de un jugador
     *
     * @param tPlayer TimedPlayer
     *
     * @return False si no se pudo completar
     */
    public boolean savePlayedTime(TimedPlayer tPlayer);

    /**
     * Método para recibir los dias que lleva el jugador en el servidor con el
     * plugin activado.
     *
     * @param playerUUID UUID
     *
     * @return Entero con los minutos transcurridos
     */
    public int loadPlayedTime(UUID playerUUID);

    /**
     * Método para recibir el top deseado de jugadores ordenado de mejor a peor
     *
     * @param cant Cantidad de jugadores a mostrarng w,
     * @param server String
     *
     * @return ArrayList con los jugadores
     */
    public ArrayList<PlayerFame> getTopPlayers(short cant, String server);

    /**
     * Método para registrar un cartel en la base de datos
     *
     * @param sb SignBoard
     *
     * @return False si no se pudo completar
     */
    public boolean registraBoard(SignBoard sb);

    /**
     * Método para modificar la id del server de un cartel
     *
     * @param l Location
     *
     * @return False si no se pudo completar
     */
    public boolean modificaBoard(Location l);

    /**
     * Método para borrar un cartel de la base de datos
     *
     * @param l Localicación del cartel base
     *
     * @return False si no se pudo completar
     */
    public boolean borraBoard(Location l);

    /**
     * Método para buscar las tablas de puntuaciones de la base de datos
     *
     * @return ArrayList con todas ellas
     */
    public ArrayList<SignBoardData> buscaBoards();

    /**
     * Método para recibir el nombre del servidor según su ID
     *
     * @param id int
     * 
     * @return String
     */
    public String getServerName(short id);

    /**
     * Método para borrar los datos de los jugadores inactivos
     *
     * @return Entero con la cantidad de ficheros borrados
     */
    public int purgeData();

    /**
     * Método para exportar todos los datos de la base de datos
     * 
     * @param filename String
     */
    public void DBExport(String filename);

    /**
     * Método para importar todos los datos desde un fichero
     *
     * @param filename String
     *
     * @return False si no se pudo importar
     */
    public boolean DBImport(String filename);

    /**
     * Método para recibir el nombre del fichero por defecto para importar
     *
     * @return String
     */
    public String getDefaultFImport();

    /**
     * Método para recibir el nombre del fichero por defecto para exportar
     *
     * @return String
     */
    public String getDefaultFExport();
}
