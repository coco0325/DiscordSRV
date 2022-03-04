/*-
 * LICENSE
 * DiscordSRV
 * -------------
 * Copyright (C) 2016 - 2021 Austin "Scarsz" Shapiro
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package github.scarsz.discordsrv.listeners;

import github.scarsz.discordsrv.Debug;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.objects.MessageFormat;
import github.scarsz.discordsrv.objects.managers.GroupSynchronizationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinLeaveListener implements Listener {

    public PlayerJoinLeaveListener() {
        Bukkit.getPluginManager().registerEvents(this, DiscordSRV.getPlugin());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (DiscordSRV.getPlugin().isGroupRoleSynchronizationEnabled()) {
            // trigger a synchronization for the player
            Bukkit.getScheduler().runTaskAsynchronously(DiscordSRV.getPlugin(), () ->
                    DiscordSRV.getPlugin().getGroupSynchronizationManager().resync(
                            player,
                            GroupSynchronizationManager.SyncDirection.AUTHORITATIVE,
                            true,
                            GroupSynchronizationManager.SyncCause.PLAYER_JOIN
                    )
            );
        }

        MessageFormat messageFormat = event.getPlayer().hasPlayedBefore()
                ? DiscordSRV.getPlugin().getMessageFromConfiguration("MinecraftPlayerJoinMessage")
                : DiscordSRV.getPlugin().getMessageFromConfiguration("MinecraftPlayerFirstJoinMessage");

        // make sure join messages enabled
        if (messageFormat == null) return;


        // schedule command to run in a second to be able to capture display name
        Bukkit.getScheduler().runTaskLaterAsynchronously(DiscordSRV.getPlugin(), () ->
                DiscordSRV.getPlugin().sendJoinMessage(event.getPlayer(), event.getJoinMessage()), 20);
    }

}
