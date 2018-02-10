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


package com.minecraft.moonlake.customdrop.commands;

import com.minecraft.moonlake.api.annotation.plugin.command.Command;
import com.minecraft.moonlake.api.annotation.plugin.command.CommandArgumentOptional;
import com.minecraft.moonlake.api.annotation.plugin.command.MoonLakeCommand;
import com.minecraft.moonlake.api.annotation.plugin.command.exception.CommandPermissionException;
import com.minecraft.moonlake.customdrop.CustomDropPlugin;
import com.minecraft.moonlake.util.StringUtil;
import org.bukkit.command.CommandSender;

public class CommandCustomDrop implements MoonLakeCommand {

    private final CustomDropPlugin main;

    public CommandCustomDrop(CustomDropPlugin main) {
        this.main = main;
    }

    public CustomDropPlugin getMain() {
        return main;
    }

    @Command(name = "customdrop", usage = "help", max = 1)
    public void onCommand(CommandSender sender, @CommandArgumentOptional String arg) {
        if(arg == null)
            arg = "help";

        if(arg.equalsIgnoreCase("help")) {
            printHelp(sender);
        } else if(arg.equalsIgnoreCase("reload")) {
            // 重新载入配置文件数据
            if(!sender.hasPermission("moonlake.customdrop"))
                throw new CommandPermissionException("moonlake.customdrop");
            // 拥有权限则重载
            getMain().reloadConfig();
            getMain().reloadPrefix();
            getMain().getCustomDropManager().reload();
            sender.sendMessage(getMain().getMessage("CustomDropReload"));
        } else {
            sender.sendMessage(getMain().getMessage("ErrorCommandArgs", "/custom help - 查看命令帮助."));
        }
    }

    private void printHelp(CommandSender sender) {
        sender.sendMessage(StringUtil.toColor(new String[] {
                "&b&l&m          &d CustomDrop &7By &6Month_Light &b&l&m          ",
                "&6/customdrop help &7- 查看插件命令帮助.",
                "&6/customdrop reload &7- 重新载入插件配置文件.",
        }));
    }
}
