/*
 * Copyright (C) 2016 The MoonLake Authors
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


package com.minecraft.moonlake.customdrop.listeners;

import com.minecraft.moonlake.api.event.MoonLakeListener;
import com.minecraft.moonlake.customdrop.CustomDropPlugin;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerListener implements MoonLakeListener {

    private final CustomDropPlugin main;

    public PlayerListener(CustomDropPlugin main) {
        this.main = main;
    }

    public CustomDropPlugin getMain() {
        return main;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        // 处理玩家破坏方块事件
        if(player.getGameMode() == GameMode.CREATIVE && player.getGameMode() != GameMode.ADVENTURE)
            return;
        // 不为创造模式并且不为冒险模式则处理
        getMain().getCustomDropManager().handlerBlockBreak(player, block, event);
    }
}
