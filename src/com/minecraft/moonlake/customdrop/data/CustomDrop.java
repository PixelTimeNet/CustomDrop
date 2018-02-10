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

import com.minecraft.moonlake.manager.RandomManager;
import com.minecraft.moonlake.util.StringUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CustomDrop {

    private final ArrayList<CustomDropMatch> requiredTools;
    private final String requiredMobName;
    private final String requiredPermission;
    private final boolean removeDefaultDrops;
    private final AbstractValue dropExp;
    private final boolean dropExpOrb;
    private final int dropExpChance;
    private final AbstractValue dropMoney;
    private final int dropMoneyChance;
    private final ArrayList<CustomDropItem> dropItems;

    public CustomDrop(ArrayList<CustomDropMatch> requiredTools, String requiredMobName, String requiredPermission, boolean removeDefaultDrops, AbstractValue dropExp, boolean dropExpOrb, int dropExpChance, AbstractValue dropMoney, int dropMoneyChance, ArrayList<CustomDropItem> dropItems) {
        this.requiredTools = requiredTools;
        this.requiredMobName = requiredMobName;
        this.requiredPermission = requiredPermission;
        this.removeDefaultDrops = removeDefaultDrops;
        this.dropExp = dropExp;
        this.dropExpOrb = dropExpOrb;
        this.dropExpChance = dropExpChance;
        this.dropMoney = dropMoney;
        this.dropMoneyChance = dropMoneyChance;
        this.dropItems = dropItems;
    }

    public ArrayList<CustomDropMatch> getRequiredTools() {
        return requiredTools;
    }

    public String getRequiredMobName() {
        return requiredMobName;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public boolean isRemoveDefaultDrops() {
        return removeDefaultDrops;
    }

    public AbstractValue getDropExp() {
        return dropExp;
    }

    public boolean isDropExpOrb() {
        return dropExpOrb;
    }

    public int getDropExpChance() {
        return dropExpChance;
    }

    public AbstractValue getDropMoney() {
        return dropMoney;
    }

    public int getDropMoneyChance() {
        return dropMoneyChance;
    }

    public ArrayList<CustomDropItem> getDropItems() {
        return dropItems;
    }

    public boolean containsTool(ItemStack itemStack) {
        // 判断指定物品栈是否包含在工具列表
        if(getRequiredTools().isEmpty())
            return true;
        if(itemStack == null)
            return false;
        return getRequiredTools().contains(new CustomDropMatch(itemStack));
    }

    public boolean hasPermission(Player player) {
        // 判断是否拥有权限
        if(getRequiredPermission() == null)
            return true;
        // 否则不为 null 则判断玩家是否拥有权限
        return player.hasPermission(getRequiredPermission());
    }

    public boolean isMobName(LivingEntity entity) {
        // 判断是否符合实体名称
        if(getRequiredMobName() == null)
            return true;
        // 否则不为 null 则判断实体是否拥有名称
        return StringUtil.toColor(getRequiredMobName()).equals(entity.getCustomName());
    }

    public int getFinalDropExp() {
        if(getDropExp() == null)
            return 0;
        // 不为 null 则获取最终经验值
        if(getDropExpChance() == 100)
            // 表示没有几率则直接返回
            return getDropExp().getValue();
        // 否则判断几率
        return chance(getDropExpChance()) ? getDropExp().getValue() : 0;
    }

    public int getFinalDropMoney() {
        if(getDropMoney() == null)
            return 0;
        // 不为 null 则获取最终金币值
        if(getDropMoneyChance() == 100)
            // 表示没有几率则直接返回
            return getDropMoney().getValue();
        // 否则判断几率
        return chance(getDropMoneyChance()) ? getDropExp().getValue() : 0;
    }

    static boolean chance(int chance) {
        // 获取指定随机几率是否成功
        int value = (int) (RandomManager.getRandom().nextDouble() * 100);
        return value <= chance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomDrop that = (CustomDrop) o;

        if (removeDefaultDrops != that.removeDefaultDrops) return false;
        if (dropExpOrb != that.dropExpOrb) return false;
        if (dropExpChance != that.dropExpChance) return false;
        if (dropMoneyChance != that.dropMoneyChance) return false;
        if (!requiredTools.equals(that.requiredTools)) return false;
        if (requiredMobName != null ? !requiredMobName.equals(that.requiredMobName) : that.requiredMobName != null)
            return false;
        if (requiredPermission != null ? !requiredPermission.equals(that.requiredPermission) : that.requiredPermission != null)
            return false;
        if (dropExp != null ? !dropExp.equals(that.dropExp) : that.dropExp != null) return false;
        if (dropMoney != null ? !dropMoney.equals(that.dropMoney) : that.dropMoney != null) return false;
        return dropItems.equals(that.dropItems);
    }

    @Override
    public int hashCode() {
        int result = requiredTools.hashCode();
        result = 31 * result + (requiredMobName != null ? requiredMobName.hashCode() : 0);
        result = 31 * result + (requiredPermission != null ? requiredPermission.hashCode() : 0);
        result = 31 * result + (removeDefaultDrops ? 1 : 0);
        result = 31 * result + (dropExp != null ? dropExp.hashCode() : 0);
        result = 31 * result + (dropExpOrb ? 1 : 0);
        result = 31 * result + dropExpChance;
        result = 31 * result + (dropMoney != null ? dropMoney.hashCode() : 0);
        result = 31 * result + dropMoneyChance;
        result = 31 * result + dropItems.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CustomDrop{" +
                "requiredTools=" + requiredTools +
                ", requiredMobName='" + requiredMobName + '\'' +
                ", requiredPermission='" + requiredPermission + '\'' +
                ", removeDefaultDrops=" + removeDefaultDrops +
                ", dropExp=" + dropExp +
                ", dropExpOrb=" + dropExpOrb +
                ", dropExpChance=" + dropExpChance +
                ", dropMoney=" + dropMoney +
                ", dropMoneyChance=" + dropMoneyChance +
                ", dropItems=" + dropItems +
                '}';
    }

    public static abstract class AbstractValue {

        private final int value;

        public AbstractValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AbstractValue value1 = (AbstractValue) o;

            return value == value1.value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public String toString() {
            return "AbstractValue{" +
                    "value=" + value +
                    '}';
        }
    }

    public final static class Value extends AbstractValue {

        public Value(int value) {
            super(value);
        }
    }

    public final static class RangeValue extends AbstractValue {

        private final int min;
        private final int max;

        public RangeValue(String value) throws RuntimeException {
            super(0);

            if(value == null || !value.matches("([-]?)([0-9]+)~([-]?)([0-9]+)"))
                throw new RuntimeException();
            // 处理
            try {
                String[] dataArray = value.split("~");
                min = Integer.parseInt(dataArray[0]);
                max = Integer.parseInt(dataArray[1]);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        public RangeValue(int min, int max) {
            super(0);

            this.min = min;
            this.max = max;
        }

        @Override
        public int getValue() {
            return RandomManager.nextInt(getMin(), getMax());
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RangeValue rangeValue = (RangeValue) o;

            if (min != rangeValue.min) return false;
            return max == rangeValue.max;
        }

        @Override
        public int hashCode() {
            int result = min;
            result = 31 * result + max;
            return result;
        }

        @Override
        public String toString() {
            return "RangeValue{" +
                    "min=" + min +
                    ", max=" + max +
                    '}';
        }
    }
}
