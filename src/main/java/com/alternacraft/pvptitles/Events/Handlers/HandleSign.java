/*
 * Copyright (C) 2016 AlternaCraft
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.alternacraft.pvptitles.Events.Handlers;

import com.alternacraft.pvptitles.Files.LangsFile;
import com.alternacraft.pvptitles.Main.Manager;
import com.alternacraft.pvptitles.Main.PvpTitles;
import static com.alternacraft.pvptitles.Main.PvpTitles.getPluginName;
import com.alternacraft.pvptitles.Managers.BoardsAPI.BoardModel;
import com.alternacraft.pvptitles.Managers.BoardsAPI.ModelController;
import com.alternacraft.pvptitles.Managers.BoardsCustom.SignBoard;
import com.alternacraft.pvptitles.Managers.BoardsCustom.SignBoardData;
import static com.alternacraft.pvptitles.Managers.BoardsCustom.SignBoardData.EAST;
import static com.alternacraft.pvptitles.Managers.BoardsCustom.SignBoardData.NORTH;
import static com.alternacraft.pvptitles.Managers.BoardsCustom.SignBoardData.SOUTH;
import static com.alternacraft.pvptitles.Managers.BoardsCustom.SignBoardData.WEST;
import com.alternacraft.pvptitles.Misc.Localizer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class HandleSign implements Listener {

    private static final BlockFace[] AXIS = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private final org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(Material.WALL_SIGN);

    private PvpTitles pt = null;
    private Manager cm = null;

    public HandleSign(PvpTitles plugin) {
        this.pt = plugin;
        this.cm = plugin.getManager();
    }

    @EventHandler
    public void onCreateSign(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        String[] lineas = event.getLines();

        Player pl = event.getPlayer();

        // Otros datos
        String nombre = "default";
        String modelo = "";
        String orientacion = "";
        String server = "";
        short blockface = 0;
        //int refresh = 5;

        if (lineas[0].contains("[PvpTitles]") || lineas[0].contains("[pvptitles]")
                || lineas[0].compareToIgnoreCase("[pvptitles]") == 0) {

            // Compruebo permisos
            if (!event.getPlayer().hasPermission("pvptitles.manageboard")) {
                pl.sendMessage(getPluginName() + LangsFile.COMMAND_NO_PERMISSIONS.getText(Localizer.getLocale(pl)));
                return;
            }

            BoardModel sm = cm.searchModel(lineas[1]);
            
            if (sm != null) {
                modelo = sm.getNombre();
                Location locSign = sign.getLocation();

                // Nombre
                if (lineas[2].compareTo("") != 0) {
                    nombre = lineas[2];
                }

                if (lineas[3].compareTo("") != 0) {
                    server = lineas[3];
                }

                BlockFace bf = yawToFace(pl.getLocation().getYaw());

                if (bf.equals(BlockFace.NORTH) || bf.equals(BlockFace.SOUTH)) {
                    if (bf.equals(BlockFace.SOUTH)) {
                        orientacion = "X+";
                        blockface = SOUTH;
                    } else {
                        orientacion = "X-";
                        blockface = NORTH;
                    }
                } else if (bf.equals(BlockFace.WEST)) {
                    orientacion = "Z+";
                    blockface = WEST;
                } else {
                    orientacion = "Z-";
                    blockface = EAST;
                }

                matSign.setFacingDirection(bf);

                SignBoardData data = new SignBoardData(nombre, modelo, server, locSign);
                data.setBlockface(blockface);
                data.setOrientacion(orientacion);

                ModelController mc = new ModelController();
                mc.preprocessUnit(sm.getParams());
                
                SignBoard cs = new SignBoard(data, sm, mc);
                cs.setLineas(lineas);
                cs.setMatSign(matSign);

                if (!pt.getManager().getLbm().addBoard(cs, pl)) {                
                    event.setCancelled(true);
                }
            } else {
                pl.sendMessage(getPluginName() + LangsFile.BOARD_MODEL_NOT_EXISTS.getText(Localizer.getLocale(pl)));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeleteSign(BlockBreakEvent event) {
        Integer[] coords = new Integer[]{1,-1};
        
        Block b = event.getBlock();
        Location locblock = b.getLocation();
        World world = locblock.getWorld();

        // Caso especial en caso de que rompa el bloque que sostiene a un cartel
        if (world.getBlockAt(locblock).getType() != Material.WALL_SIGN) {
            for (Integer coord : coords) {
                Block b2 = new Location(b.getWorld(), b.getX() + coord, b.getY(), b.getZ()).getBlock();
                if (b2.getType() == Material.WALL_SIGN) {
                    pt.getManager().getLbm().deleteBoard(b2.getLocation(), event);
                }
            }
            
            for (Integer coord : coords) {
                Block b2 = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ() + coord).getBlock();
                if (b2.getType() == Material.WALL_SIGN) {
                    pt.getManager().getLbm().deleteBoard(b2.getLocation(), event);
                }
            }
        } else {
            pt.getManager().getLbm().deleteBoard(locblock, event);
        }
    }

    /**
     * Método que devuelve la cara del bloque horizontal según el ángulo
     *
     * @param yaw Ángulo
     * @return La cara del bloque
     */
    private BlockFace yawToFace(float yaw) {
        return AXIS[Math.round(yaw / 90f) & 0x3];
    }
}
