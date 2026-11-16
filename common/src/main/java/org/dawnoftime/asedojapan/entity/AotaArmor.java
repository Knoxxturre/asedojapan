package org.dawnoftime.asedojapan.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Armor of the Ages armor looked up by id, so the mod does not need Armor of the Ages at compile time.
 */
public final class AotaArmor {

    private static final String MOD_ID = "armoroftheages";

    private AotaArmor() {
    }

    /** Puts every piece of an armor set (e.g. "o_yoroi_armor") on the mob; a missing piece resolves to air. */
    public static void equip(Mob mob, String armorSet) {
        mob.setItemSlot(EquipmentSlot.HEAD, piece(armorSet, "head"));
        mob.setItemSlot(EquipmentSlot.CHEST, piece(armorSet, "chest"));
        mob.setItemSlot(EquipmentSlot.LEGS, piece(armorSet, "legs"));
        mob.setItemSlot(EquipmentSlot.FEET, piece(armorSet, "feet"));
    }

    private static ItemStack piece(String armorSet, String slot) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MOD_ID, armorSet + "_" + slot));
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }
}
