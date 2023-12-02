package ru.mdashlw.hypixel.pit.stats.util;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class ItemStackUtils {

    public static ItemStack withDisplay(ItemStack itemStack, String name, List lore) {
        NBTTagCompound tag = itemStack.getTagCompound();

        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTagCompound(tag);
        }

        if (!tag.hasKey("display", 10)) {
            tag.setTag("display", new NBTTagCompound());
        }

        NBTTagCompound displayTag = tag.getCompoundTag("display");

        displayTag.setString("Name", name);
        displayTag.setTag("Lore", NBTUtils.asStringTagList(lore));
        return itemStack;
    }

    public static ItemStack withNoAttributeModifiers(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();

        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTagCompound(tag);
        }

        tag.setTag("AttributeModifiers", new NBTTagList());
        return itemStack;
    }

    public static ItemStack withSkullOwner(ItemStack itemStack, String owner) {
        NBTTagCompound tag = itemStack.getTagCompound();

        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTagCompound(tag);
        }

        tag.setString("SkullOwner", owner);
        return itemStack;
    }
}
