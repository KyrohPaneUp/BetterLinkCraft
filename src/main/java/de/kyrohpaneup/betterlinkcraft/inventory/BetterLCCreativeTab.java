package de.kyrohpaneup.betterlinkcraft.inventory;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BetterLCCreativeTab extends CreativeTabs {

    public BetterLCCreativeTab() {
        super("BetterLC");
    }

    @Override
    public String getTranslatedTabLabel() {
        return "BetterLC";
    }

    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(Blocks.dragon_egg);
    }

    @Override
    public void displayAllReleventItems(List<ItemStack> items) {
        {
            items.addAll(Arrays.asList(
                    new ItemStack(Items.wooden_axe),
                    getSword(),
                    new ItemStack(Blocks.dragon_egg),
                    new ItemStack(Blocks.barrier)
            ));
            List<ItemStack> toAdd = getMissingItems(16);
            items.addAll(toAdd);
        }

        for (int i = 0; i <= 15; i++) {
            items.add(new ItemStack(Blocks.stained_hardened_clay, 1, i));
            items.add(new ItemStack(Blocks.wool, 1, i));
            items.add(new ItemStack(Blocks.stained_glass, 1, i));
            items.add(new ItemStack(Blocks.stained_glass_pane, 1, i));
            List<ItemStack> toAdd = getMissingItems(i);
            items.addAll(toAdd);
        }
        super.displayAllReleventItems(items);
    }

    private List<ItemStack> getMissingItems(int color) {
        List<ItemStack> items = new ArrayList<>();
        switch (color) {
            case 0:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.snow),
                        new ItemStack(Blocks.iron_block),
                        new ItemStack(Blocks.quartz_block),
                        new ItemStack(Blocks.stone, 1, 3),
                        new ItemStack(Blocks.stone, 1, 4)
                ));
                break;
            case 1:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.red_sandstone),
                        new ItemStack(Blocks.sand, 1, 1),
                        new ItemStack(Blocks.planks, 1, 4),
                        new ItemStack(Blocks.log2)));
                break;
            case 2:
            case 10:
            case 6:
                break;
            case 3:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.diamond_block),
                        new ItemStack(Blocks.ice),
                        new ItemStack(Blocks.packed_ice),
                        new ItemStack(Blocks.prismarine),
                        new ItemStack(Blocks.prismarine, 1, 1)
                ));
                break;
            case 4:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.gold_block),
                        new ItemStack(Blocks.end_stone),
                        new ItemStack(Blocks.hay_block),
                        new ItemStack(Blocks.planks, 1, 2),
                        new ItemStack(Blocks.log, 1, 2)
                ));
                break;
            case 5:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.emerald_block),
                        new ItemStack(Blocks.melon_block)
                ));
                break;
            case 7:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.stone),
                        new ItemStack(Blocks.cobblestone),
                        new ItemStack(Blocks.bedrock),
                        new ItemStack(Blocks.stonebrick),
                        new ItemStack(Blocks.mycelium)
                ));
                break;
            case 8:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.gravel),
                        new ItemStack(Blocks.stone_slab),
                        new ItemStack(Blocks.clay),
                        new ItemStack(Blocks.stone, 1, 5),
                        new ItemStack(Blocks.stone, 1, 6)
                ));
                break;
            case 9:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.prismarine, 1, 2),
                        new ItemStack(Blocks.sea_lantern)
                ));
                break;
            case 11:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.lapis_block)
                ));
                break;
            case 12:

                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.dirt),
                        new ItemStack(Blocks.dirt, 1, 1),
                        new ItemStack(Blocks.dirt, 1, 2),
                        new ItemStack(Blocks.soul_sand),
                        new ItemStack(Blocks.noteblock)
                ));
                break;
            case 13:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.grass),
                        new ItemStack(Blocks.leaves, 1, 0),
                        new ItemStack(Blocks.leaves, 1, 1),
                        new ItemStack(Blocks.leaves, 1, 2),
                        new ItemStack(Blocks.leaves, 1, 3)
                ));
                break;
            case 14:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.netherrack),
                        new ItemStack(Blocks.brick_block),
                        new ItemStack(Blocks.redstone_block),
                        new ItemStack(Blocks.tnt)
                ));
                break;
            case 15:
                items.addAll(Arrays.asList(
                        new ItemStack(Blocks.obsidian),
                        new ItemStack(Blocks.nether_brick),
                        new ItemStack(Blocks.coal_block),
                        new ItemStack(Blocks.planks, 1, 5),
                        new ItemStack(Blocks.log2, 1, 1)
                ));
        }
        int size = items.size();
        for (int i = 1; i <= 5 - size; i++) {
            items.add(new ItemStack(Blocks.stained_glass_pane, 1, 8));
        }
        return items;
    }

    private ItemStack getSword() {
        ItemStack sword = new ItemStack(Items.iron_sword);

        sword.setStackDisplayName("BetterLC");

        sword.addEnchantment(Enchantment.sharpness, 10);

        NBTTagCompound tag = sword.getTagCompound();

        if (tag == null) {
            tag = new NBTTagCompound();
        }

        tag.setBoolean("Unbreakable", true);

        NBTTagCompound display = tag.getCompoundTag("display");

        if (display == null) {
            display = new NBTTagCompound();
        }

        NBTTagList lore = new NBTTagList();
        lore.appendTag(new NBTTagString("Quite pointy"));

        display.setTag("Lore", lore);
        tag.setTag("display", display);

        sword.setTagCompound(tag);

        return sword;
    }
}
