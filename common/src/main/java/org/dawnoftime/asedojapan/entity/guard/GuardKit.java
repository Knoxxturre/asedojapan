package org.dawnoftime.asedojapan.entity.guard;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.dawnoftime.asedojapan.entity.AotaArmor;

/**
 * Fixed loadouts for Edo guards: one Armor of the Ages Japanese armor set combined with one weapon.
 * Each kit is registered as its own entity type so that guards saved inside structure templates keep their kit.
 */
public enum GuardKit {
    JAPANESE_LIGHT_SWORD("japanese_light_guard_sword", "japanese_light_armor", Weapon.SWORD),
    JAPANESE_LIGHT_CROSSBOW("japanese_light_guard_crossbow", "japanese_light_armor", Weapon.CROSSBOW),
    O_YOROI_SWORD("o_yoroi_guard_sword", "o_yoroi_armor", Weapon.SWORD),
    O_YOROI_CROSSBOW("o_yoroi_guard_crossbow", "o_yoroi_armor", Weapon.CROSSBOW);

    /** Shared egg colors (same as the Guard Villagers guard egg); the kit's helmet, drawn on the egg model, tells eggs apart. */
    public static final int EGG_BACKGROUND_COLOR = 0x563C33;
    public static final int EGG_SPOTS_COLOR = 0x959B9B;

    private final String id;
    private final String armorSet;
    private final Weapon weapon;

    GuardKit(String id, String armorSet, Weapon weapon) {
        this.id = id;
        this.armorSet = armorSet;
        this.weapon = weapon;
    }

    public String getId() {
        return this.id;
    }

    public String getSpawnEggId() {
        return this.id + "_spawn_egg";
    }

    /** Replaces the mob's whole equipment with this kit. */
    public void equip(Mob mob) {
        AotaArmor.equip(mob, this.armorSet);
        mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(this.weapon.mainHand));
        mob.setItemSlot(EquipmentSlot.OFFHAND, this.weapon.offHand == Items.AIR ? ItemStack.EMPTY : new ItemStack(this.weapon.offHand));
    }

    private enum Weapon {
        SWORD(Items.IRON_SWORD, Items.SHIELD),
        CROSSBOW(Items.CROSSBOW, Items.AIR);

        private final Item mainHand;
        private final Item offHand;

        Weapon(Item mainHand, Item offHand) {
            this.mainHand = mainHand;
            this.offHand = offHand;
        }
    }
}
