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


package com.minecraft.moonlake.customdrop.manager;

import com.minecraft.moonlake.api.player.MoonLakePlayer;
import com.minecraft.moonlake.customdrop.CustomDropPlugin;
import com.minecraft.moonlake.customdrop.data.CustomDrop;
import com.minecraft.moonlake.customdrop.data.CustomDropItem;
import com.minecraft.moonlake.customdrop.data.CustomDropMatch;
import com.minecraft.moonlake.data.Conversions;
import com.minecraft.moonlake.manager.EntityManager;
import com.minecraft.moonlake.manager.IoManager;
import com.minecraft.moonlake.manager.ItemManager;
import com.minecraft.moonlake.manager.PlayerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class CustomDropManager {

    private final CustomDropPlugin main;
    private final HashMap<String, CustomDrop> dropDataMap;
    private final HashMap<EntityType, ArrayList<String>> mobDropMap;
    private final HashMap<CustomDropMatch, ArrayList<String>> blockDropMap;
    private final File dropsDir;
    private final File itemsDir;

    public CustomDropManager(CustomDropPlugin main) {
        this.main = main;
        this.mobDropMap = new HashMap<>();
        this.dropDataMap = new HashMap<>();
        this.blockDropMap = new HashMap<>();
        this.dropsDir = new File(main.getDataFolder(), File.separator + "drops");
        this.itemsDir = new File(main.getDataFolder(), File.separator + "items");
        this.checkDir();
    }

    public CustomDropPlugin getMain() {
        return main;
    }

    private void checkDir() {
        // 检测目录是否存在
        if(!dropsDir.exists())
            dropsDir.mkdirs();
        if(!itemsDir.exists())
            itemsDir.mkdirs();
        // 模版文件输出
        File dropExample = new File(dropsDir, File.separator + "drop-example.yml");
        if(!dropExample.exists())
            IoManager.outInputStream(dropExample, getMain().getResource(dropExample.getName()));
        File itemExample = new File(itemsDir, File.separator + "item-example.yml");
        if(!itemExample.exists())
            IoManager.outInputStream(itemExample, getMain().getResource(itemExample.getName()));
    }

    public void reload() {
        // 重新加载配置文件数据

        // 先清空缓存
        dropDataMap.clear();
        mobDropMap.clear();
        blockDropMap.clear();

        // 再进行读取
        FileConfiguration fileConfiguration = getMain().getConfig();
        ConfigurationSection blockSection = fileConfiguration.getConfigurationSection("BlockList");

        if(blockSection != null) {
            // 方块自定义掉落节点不为 null
            Set<String> keys = blockSection.getKeys(false);

            if(keys != null && !keys.isEmpty()) {
                // 不为 null 并且不为空则读取
                keys.forEach((key) -> {
                    // 循环遍历键
                    CustomDropMatch match = null;

                    try {
                        match = new CustomDropMatch(key);
                    } catch (Exception e) {
                        getMain().getLogger().log(Level.SEVERE, "错误: 加载方块自定义掉落键为 '" + key + "' 时异常:");
                        e.printStackTrace();
                    }
                    if(match != null) {
                        // 不为 null 则 put 到缓存 map
                        List<String> dropTables = fileConfiguration.getStringList("BlockList." + key);
                        blockDropMap.put(match, new ArrayList<>(dropTables));
                    }
                });
            }
        }
        ConfigurationSection mobSection = fileConfiguration.getConfigurationSection("MobList");

        if(mobSection != null) {
            // 实体自定义掉落节点不为 null
            Set<String> keys = mobSection.getKeys(false);

            if(keys != null && !keys.isEmpty()) {
                // 不为 null 并且不为空则读取
                keys.forEach((key) -> {
                    // 循环遍历键
                    EntityType entityType = null;

                    try {
                        entityType = EntityType.valueOf(key.toUpperCase());
                    } catch (Exception e) {
                        getMain().getLogger().log(Level.SEVERE, "错误: 加载实体自定义掉落键为 '" + key + "' 时未知实体类型.");
                    }
                    if(entityType != null) {
                        // 不为 null 则 put 到缓存 map
                        List<String> dropTables = fileConfiguration.getStringList("MobList." + key);
                        mobDropMap.put(entityType, new ArrayList<>(dropTables));
                    }
                });
            }
        }
        // 加载掉落表目录的所有数据
        this.readCustomDrop(dropsDir);
    }

    private void readCustomDrop(File file) {
        File[] dataFiles = file.listFiles();

        if(dataFiles != null && dataFiles.length > 0) {
            // 不为 null 并且长度大于 0 则读取
            for(final File dataFile : dataFiles) {
                // 循环遍历数据文件
                if(dataFile.isDirectory()) {
                    // 如果数据文件是目录则继续读取
                    readCustomDrop(dataFile);
                    continue;
                }
                String name = dataFile.getName();

                if(dataFile.exists() && dataFile.isFile() && name.endsWith(".yml")) {
                    // 文件存在并且是文件并且后缀是 yml 类型
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);

                    if(!yaml.isSet("DropList")) {
                        getMain().getLogger().log(Level.SEVERE, "错误: 掉落表名为 '" + name + "' 的配置无 'DropList' 节点.");
                        continue;
                    }
                    ArrayList<CustomDropItem> dropItems = new ArrayList<>();
                    ConfigurationSection dropListSection = yaml.getConfigurationSection("DropList");
                    Set<String> keys = dropListSection.getKeys(false);

                    if(keys != null && !keys.isEmpty()) {
                        // 不为 null 并且不为空
                        keys.forEach((key) -> {
                            // 循环遍历键
                            int chance = yaml.getInt("DropList." + key + ".Chance", 100);
                            CustomDrop.AbstractValue amount = format(yaml.getString("DropList." + key + ".Amount", null));
                            dropItems.add(new CustomDropItem(key, chance, amount));
                        });
                    }
                    ArrayList<CustomDropMatch> requiredTools = new ArrayList<>();

                    if(yaml.isSet("RequiredTools")) {
                        // 不为 null 则处理
                        List<String> tools = yaml.getStringList("RequiredTools");

                        if(tools != null && !tools.isEmpty()) {
                            // 不为 null 并且不为空
                            tools.forEach((key) -> {
                                // 循环遍历键
                                CustomDropMatch match = null;

                                try {
                                    match = new CustomDropMatch(key);
                                } catch (Exception e) {
                                    getMain().getLogger().log(Level.SEVERE, "错误: 加载掉落表需求工具键为 '" + key +"' 时异常:");
                                    e.printStackTrace();
                                }
                                if(match != null) {
                                    // 不为 null 则 add 到列表
                                    requiredTools.add(match);
                                }
                            });
                        }
                    }

                    String requiredMobName = yaml.getString("RequiredMobName", null);
                    String requiredPermission = yaml.getString("RequiredPermission", null);
                    boolean removeDefaultDrops = yaml.getBoolean("RemoveDefaultDrops", false);
                    CustomDrop.AbstractValue dropExp = format(yaml.getString("DropExp", "0"));
                    boolean dropExpOrb = yaml.getBoolean("DropExpOrb", true);
                    int dropExpChance = yaml.getInt("DropExpChance", dropExp != null ? 100 : 0);
                    CustomDrop.AbstractValue dropMoney = format(yaml.getString("DropMoney", "0"));
                    int dropMoneyChance = yaml.getInt("DropMoneyChance", dropMoney != null ? 100 : 0);
                    // 创建实例并 put 到缓存 map
                    CustomDrop customDrop = new CustomDrop(
                            requiredTools,
                            requiredMobName,
                            requiredPermission,
                            removeDefaultDrops,
                            dropExp,
                            dropExpOrb,
                            dropExpChance,
                            dropMoney,
                            dropMoneyChance,
                            dropItems
                    );
                    dropDataMap.put(name, customDrop);
                    // 给控制台提示信息读取成功
                    getMain().getLogger().info("成功载入名为 '" + name + "' 的自定义掉落表数据.");
                }
            }
        }
    }

    private CustomDrop.AbstractValue format(String data) {
        // 格式化值或范围值
        if(data == null)
            return null;
        // 不为 null 则处理
        CustomDrop.AbstractValue value = null;

        try {
            value = new CustomDrop.RangeValue(data);
        } catch (Exception e) {
            value = new CustomDrop.Value(Conversions.toInt(data));
        }
        return value;
    }

    protected ArrayList<String> getDropTableKeys(Block block) {
        // 获取指定方块的掉落表键
        ArrayList<String> keys = new ArrayList<>();

        if(block == null)
            return keys;
        // 不为 null
        CustomDropMatch match = new CustomDropMatch(block.getType(), block.getType().getMaxDurability());
        ArrayList<String> keyCaches = blockDropMap.get(match);

        if(keyCaches != null && !keyCaches.isEmpty())
            // 不为空则 add 到 keys 列表
            keys.addAll(keyCaches);
        return keys;
    }

    protected ArrayList<String> getDropTableKeys(LivingEntity entity) {
        // 获取指定实体的掉落表键
        ArrayList<String> keys = new ArrayList<>();

        if(entity == null)
            return keys;
        // 不为 null
        ArrayList<String> keyCaches = mobDropMap.get(entity.getType());

        if(keyCaches != null && !keyCaches.isEmpty())
            // 不为空则 add 到 keys 列表
            keys.addAll(keyCaches);
        return keys;
    }

    protected void dropExp(Location location, int value) {
        // 掉落指定经验到指定位置
        if(value == 0)
            return;
        // 不为 0 则掉落
        ExperienceOrb expOrb = location.getWorld().spawn(location, ExperienceOrb.class);
        expOrb.setExperience(value);
    }

    protected void dropMoney(Player player, int value, String type) {
        // 掉落（给予）指定金币给玩家
        if(value == 0)
            return;
        // 不为 0 则处理
        MoonLakePlayer moonLakePlayer = PlayerManager.adapter(player);

        if(getMain().isVaultEconomyHook()) {
            // Vault 经济插件
            try {
                if(value > 0)
                    moonLakePlayer.depositEconomyVaultBalance(value);
                else
                    moonLakePlayer.withdrawEconomyVaultBalance(value);
                // 发送消息
                moonLakePlayer.send(getMain().getMessage("GrantCustomDropMoney", value, type));
            } catch (Exception e) {
                getMain().getLogger().log(Level.SEVERE, "错误: 给玩家 '" + player.getName() + "' 掉落金币时异常:");
                e.printStackTrace();
            }
        }
    }

    protected void dropCustomItem(Location location, ArrayList<CustomDropItem> dropItems) {
        // 将指定自定义物品掉落在指定位置
        if(dropItems != null && !dropItems.isEmpty()) {
            // 不为 null 并且不为空则处理
            dropItems.forEach((item) -> {
                // 遍历掉落物品并处理
                String path = item.getKey().replaceAll("\\\\/", File.separator) + ".yml";
                ItemStack target = null;

                try {
                    target = ItemManager.deserializeFromFile(new File(itemsDir, File.separator + path));
                } catch (Exception e) {
                    getMain().getLogger().log(Level.SEVERE, "错误: 反序列化自定义物品文件名为 '" + path + "' 时异常:");
                    e.printStackTrace();
                }
                if(target != null && item.canDrop()) {
                    // 不为 null 并且可以掉落则处理
                    int finalAmount = item.getFinalAmount(target.getAmount());
                    target.setAmount(finalAmount);
                    // 最终掉落
                    EntityManager.dropItem(location, target);
                }
            });
        }
    }

    public void handlerBlockBreak(Player player, Block block, BlockBreakEvent event) {
        // 处理玩家破坏方块事件
        ArrayList<String> dropTableKeys = getDropTableKeys(block);

        if(dropTableKeys != null && !dropTableKeys.isEmpty()) {
            // 不为 null 并且不为空则处理
            dropTableKeys.forEach((table) -> {
                // 遍历掉落表并处理
                CustomDrop dropObj = dropDataMap.get(table);

                if(dropObj == null)
                    return;
                // 不为 null 则处理
                if(!dropObj.containsTool(player.getItemInHand()))
                    // 判断工具
                    return;
                if(!dropObj.hasPermission(player))
                    // 判断权限
                    return;

                if(dropObj.isRemoveDefaultDrops()) {
                    // 清除默认掉落
                    // 由于方块破坏事件没有设置掉落
                    // 所以只能阻止事件并且设置方块为空气
                    if(!event.isCancelled()) {
                        event.setCancelled(true);
                        event.getBlock().setType(Material.AIR);
                    }
                }
                if(dropObj.isDropExpOrb())
                    // 如果经验以经验球方式
                    dropExp(block.getLocation(), dropObj.getFinalDropExp());
                else
                    // 否则直接将经验给玩家
                    player.giveExp(dropObj.getFinalDropExp());

                dropMoney(player, dropObj.getFinalDropMoney(), "方块");
                dropCustomItem(block.getLocation(), dropObj.getDropItems());
            });
        }
    }

    public void handlerEntityDead(LivingEntity entity, EntityDeathEvent event) {
        // 处理实体死亡事件
        ArrayList<String> dropTableKeys = getDropTableKeys(entity);

        if(dropTableKeys != null && !dropTableKeys.isEmpty()) {
            // 不为 null 并且不为空则处理
            dropTableKeys.forEach((table) -> {
                // 循环遍历表并处理
                CustomDrop dropObj = dropDataMap.get(table);

                if(dropObj == null)
                    return;
                // 不为 null 则处理
                if(!dropObj.isMobName(entity))
                    // 判断名称
                    return;
                if(dropObj.isRemoveDefaultDrops()) {
                    // 清除默认掉落
                    event.getDrops().clear();
                }
                if(dropObj.isDropExpOrb())
                    // 掉落经验球实体
                    event.setDroppedExp(dropObj.getFinalDropExp());
                // 击杀者处理
                EntityDamageByEntityEvent edbee = entity.getLastDamageCause() instanceof EntityDamageByEntityEvent ?
                        (EntityDamageByEntityEvent) entity.getLastDamageCause() : null;

                if(edbee != null) {
                    // 为伤害由实体所为事件
                    Player killer = edbee.getDamager() instanceof Player ? (Player) edbee.getDamager() : null;

                    if(killer != null) {
                        // 为玩家击杀
                        if(dropObj.containsTool(killer.getItemInHand()) && dropObj.hasPermission(killer)) {
                            // 判断击杀者手中的工具并且判断权限
                            if(!dropObj.isDropExpOrb()) {
                                // 经验给予给玩家
                                killer.giveExp(dropObj.getFinalDropExp());
                                event.setDroppedExp(0);
                            }
                            // 掉落金币
                            dropMoney(killer, dropObj.getFinalDropMoney(), "实体");
                        }
                    }
                }
                // 掉落自定义物品
                dropCustomItem(entity.getLocation(), dropObj.getDropItems());
            });
        }
    }
}
