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

public class CustomDropItem {

    private final String key;
    private final int chance;
    private final CustomDrop.AbstractValue amount;

    public CustomDropItem(String key) {
        this(key, 100, null);
    }

    public CustomDropItem(String key, int chance, CustomDrop.AbstractValue amount) {
        this.key = key;
        this.chance = chance;
        this.amount = amount;
    }

    public String getKey() {
        return key;
    }

    public int getChance() {
        return chance;
    }

    public CustomDrop.AbstractValue getAmount() {
        return amount;
    }

    public boolean canDrop() {
        // 获取此物品是否能够几率掉落
        if(getChance() == 100)
            return true;
        // 否则获取几率掉落
        return CustomDrop.chance(getChance());
    }

    public int getFinalAmount(int def) {
        // 获取最终数量
        return getAmount() != null ? getAmount().getValue() : def;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomDropItem that = (CustomDropItem) o;

        if (chance != that.chance) return false;
        if (!key.equals(that.key)) return false;
        return amount != null ? amount.equals(that.amount) : that.amount == null;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + chance;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CustomDropItem{" +
                "key='" + key + '\'' +
                ", chance=" + chance +
                ", amount=" + amount +
                '}';
    }
}
