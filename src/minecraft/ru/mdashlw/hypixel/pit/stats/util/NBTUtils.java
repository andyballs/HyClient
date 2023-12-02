package ru.mdashlw.hypixel.pit.stats.util;

import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public final class NBTUtils {

    public static NBTTagList asStringTagList(List elements) {
        NBTTagList tag = new NBTTagList();
        Iterator iterator = elements.iterator();

        while (iterator.hasNext()) {
            String element = (String) iterator.next();

            if (element != null) {
                tag.appendTag(new NBTTagString(element));
            }
        }

        return tag;
    }
}
