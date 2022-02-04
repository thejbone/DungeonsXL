/*
 * Copyright (C) 2012-2022 Frank Baumann
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
package de.erethon.dungeonsxl.sign.passive;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.ContainerAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

/**
 * @author Daniel Saukel
 */
public class DungeonChestSign extends ChestSign {

    public DungeonChestSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "DungeonChest";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".dungeonchest";
    }

    @Override
    public void initialize() {
        // For consistency with reward chests but also for intuitiveness, both lines should be possible
        if (!getLine(1).isEmpty()) {
            lootTable = api.getCaliburn().getLootTable(getLine(1));
        }
        if (!getLine(2).isEmpty()) {
            lootTable = api.getCaliburn().getLootTable(getLine(2));
        }

        checkChest();
        if (chest != null) {
            setToAir();
        } else {
            getSign().getBlock().setType(VanillaItem.CHEST.getMaterial());
            chest = getSign().getBlock();
        }

        List<ItemStack> list = null;
        if (lootTable != null) {
            list = lootTable.generateLootList();
        }
        if (chestContent != null) {
            if (list != null) {
                list.addAll(Arrays.asList(chestContent));
            } else {
                list = Arrays.asList(chestContent);
            }
        }
        if (list == null) {
            return;
        }

        chestContent = Arrays.copyOfRange(list.toArray(new ItemStack[list.size()]), 0, 26);
        Inventory inventory = ContainerAdapter.getBlockInventory(chest);

        Random random = new Random();

        for (ItemStack itemStack : chestContent) {
            if (itemStack == null ||  Material.AIR.equals(itemStack.getType())) {
                continue;
            }

            int i = 0;
            do {
                int j = random.nextInt(inventory.getSize());
                if (inventory.getItem(j) == null || inventory.getItem(j).getType().equals(Material.AIR)) {
                    if (itemStack.getMaxStackSize() > 1) {
                        itemStack.setAmount(random.nextInt(Math.min(itemStack.getAmount() + 1, itemStack.getMaxStackSize())));
                    }
                    inventory.setItem(j, itemStack);

                } else {
                    i++;
                }
            } while (i < 3);
            if (i == 3) {
                inventory.addItem(itemStack);
            }
        }
    }

}
