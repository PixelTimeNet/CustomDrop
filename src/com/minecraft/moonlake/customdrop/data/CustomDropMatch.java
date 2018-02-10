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


package com.minecraft.moonlake.customdrop.data;

import com.minecraft.moonlake.MoonLakeAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomDropMatch {

    private final Material material;
    private final int subId;

    public CustomDropMatch(String key) throws RuntimeException {
        // 判断 key 是否拥有副 ID
        try {
            if(key.matches("([a-zA-Z0-9]+)-([0-9]+)")) {
                // 拥有副 ID 则
                String[] dataArray = key.split("-");
                material = Material.matchMaterial(dataArray[0]);
                subId = Integer.parseInt(dataArray[1]);
            } else {
                // 没有副 ID 则默认 0
                material = Material.matchMaterial(key);
                subId = 0;
            }
            if(material == null)
                throw new RuntimeException("获取键的物品类型或物品副Id错误");
        } catch (Exception e) {
            throw new RuntimeException("获取键的物品类型或物品副Id错误");
        }
    }

    public CustomDropMatch(Material material) {
        this(material, 0);
    }

    public CustomDropMatch(Material material, int subId) {
        this.material = material;
        this.subId = subId;
    }

    public CustomDropMatch(ItemStack itemStack) {
        this(itemStack.getType(), MoonLakeAPI.getItemLibrary().isTool(itemStack) ? 0 : itemStack.getType().getMaxDurability());
    }

    public Material getMaterial() {
        return material;
    }

    public int getSubId() {
        return subId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomDropMatch that = (CustomDropMatch) o;

        if (subId != that.subId) return false;
        return material == that.material;
    }

    @Override
    public int hashCode() {
        int result = material.hashCode();
        result = 31 * result + subId;
        return result;
    }

    @Override
    public String toString() {
        return "CustomDropMatch{" +
                "material=" + material +
                ", subId=" + subId +
                '}';
    }
}
