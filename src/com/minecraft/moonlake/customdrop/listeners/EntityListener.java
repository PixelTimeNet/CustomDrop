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
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityListener implements MoonLakeListener {

    private final CustomDropPlugin main;

    public EntityListener(CustomDropPlugin main) {
        this.main = main;
    }

    public CustomDropPlugin getMain() {
        return main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        // 处理实体的死亡事件
        getMain().getCustomDropManager().handlerEntityDead(entity, event);
    }
}
