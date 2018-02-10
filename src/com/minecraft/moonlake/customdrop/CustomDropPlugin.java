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


package com.minecraft.moonlake.customdrop;

import com.minecraft.moonlake.MoonLakeAPI;
import com.minecraft.moonlake.MoonLakePlugin;
import com.minecraft.moonlake.customdrop.commands.CommandCustomDrop;
import com.minecraft.moonlake.customdrop.listeners.EntityListener;
import com.minecraft.moonlake.customdrop.listeners.PlayerListener;
import com.minecraft.moonlake.customdrop.manager.CustomDropManager;
import com.minecraft.moonlake.util.StringUtil;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class CustomDropPlugin extends JavaPlugin {

    private String prefix;
    private boolean vaultEconomyHook;
    private CustomDropManager customDropManager;

    public CustomDropPlugin() {
    }

    @Override
    public void onEnable() {
        if(!setupMoonLake()) {
            this.getLogger().log(Level.SEVERE, "前置月色之湖核心API插件加载失败.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.initFolder();
        this.hookVaultEconomy();
        this.customDropManager = new CustomDropManager(this);
        this.customDropManager.reload();

        MoonLakeAPI.registerEvent(new PlayerListener(this), this);
        MoonLakeAPI.registerEvent(new EntityListener(this), this);
        MoonLakeAPI.getPluginAnnotation().getCommand().registerCommand(this, new CommandCustomDrop(this));

        this.getLogger().info("自定义掉落 CustomDrop 插件 v" + getDescription().getVersion() + " 成功加载.");
    }

    @Override
    public void onDisable() {
    }

    private void initFolder() {
        if(!getDataFolder().exists())
            getDataFolder().mkdirs();
        File config = new File(getDataFolder(), "config.yml");
        if(!config.exists())
            saveDefaultConfig();
        reloadPrefix();
    }

    public void reloadPrefix() {
        this.prefix = StringUtil.toColor(getConfig().getString("Prefix", "&f[&a自定义掉落&f] "));
    }

    public String getMessage(String key, Object... args) {
        return StringUtil.toColor(prefix + String.format(getConfig().getString("Messages." + key, ""), args));
    }

    public CustomDropManager getCustomDropManager() {
        return customDropManager;
    }

    public boolean isVaultEconomyHook() {
        return vaultEconomyHook;
    }

    private boolean setupMoonLake() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("MoonLake");
        return plugin != null && plugin instanceof MoonLakePlugin;
    }

    private void hookVaultEconomy() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Vault");
        vaultEconomyHook = plugin != null && plugin.isEnabled();
    }
}
