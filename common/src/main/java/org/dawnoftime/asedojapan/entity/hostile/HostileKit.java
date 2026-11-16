package org.dawnoftime.asedojapan.entity.hostile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ServerLevelAccessor;
import org.dawnoftime.asedojapan.entity.AotaArmor;

/**
 * Fixed loadouts for the hostile Edo mobs: a vanilla undead mob or illager wearing one Armor of the Ages Japanese
 * armor set and holding one weapon, optionally riding a skeleton horse. Each kit is registered as its own entity type so that
 * mobs saved inside structure templates keep their kit.
 */
public enum HostileKit {
    WITHER_RAIJIN_SAMURAI("wither_raijin_samurai", Base.WITHER_SKELETON, "raijin_armor", Weapon.SWORD, false),
    WITHER_RAIJIN_OYUMI("wither_raijin_oyumi", Base.WITHER_SKELETON, "raijin_armor", Weapon.BOW, false),
    SKELETON_O_YOROI_SAMURAI("skeleton_o_yoroi_samurai", Base.SKELETON, "o_yoroi_armor", Weapon.SWORD, false),
    SKELETON_O_YOROI_OYUMI("skeleton_o_yoroi_oyumi", Base.SKELETON, "o_yoroi_armor", Weapon.BOW, false),
    SKELETON_O_YOROI_SAMURAI_RIDER("skeleton_o_yoroi_samurai_rider", Base.SKELETON, "o_yoroi_armor", Weapon.SWORD, true),
    SKELETON_O_YOROI_OYUMI_RIDER("skeleton_o_yoroi_oyumi_rider", Base.SKELETON, "o_yoroi_armor", Weapon.BOW, true),
    ZOMBIE_SAMURAI("zombie_samurai", Base.ZOMBIE, "japanese_light_armor", Weapon.SWORD, false),
    SKELETON_SAMURAI("skeleton_samurai", Base.SKELETON, "japanese_light_armor", Weapon.SWORD, false),
    SKELETON_OYUMI("skeleton_oyumi", Base.SKELETON, "japanese_light_armor", Weapon.BOW, false),
    PILLAGER_OYUMI("pillager_oyumi", Base.PILLAGER, "japanese_light_armor", Weapon.CROSSBOW, false),
    VINDICATOR_SAMURAI("vindicator_samurai", Base.VINDICATOR, "japanese_light_armor", Weapon.SWORD, false);

    /** Undead kits spot and chase players from further away than vanilla undead; illagers keep vanilla stats. */
    private static final double UNDEAD_FOLLOW_RANGE = 40.0D;
    /** Vanilla skeleton horse egg colors, so rider eggs read as mounted mobs. */
    private static final int RIDER_EGG_BACKGROUND_COLOR = 0x68684F;
    private static final int RIDER_EGG_SPOTS_COLOR = 0xE5E5D8;

    private final String id;
    private final Base base;
    private final String armorSet;
    private final Weapon weapon;
    private final boolean mounted;

    HostileKit(String id, Base base, String armorSet, Weapon weapon, boolean mounted) {
        this.id = id;
        this.base = base;
        this.armorSet = armorSet;
        this.weapon = weapon;
        this.mounted = mounted;
    }

    public String getId() {
        return this.id;
    }

    public String getSpawnEggId() {
        return this.id + "_spawn_egg";
    }

    public Base getBase() {
        return this.base;
    }

    /** Egg colors: the base mob's, or the skeleton horse's for riders. The kit's helmet, drawn on the egg model, tells eggs apart. */
    public int getEggBackgroundColor() {
        return this.mounted ? RIDER_EGG_BACKGROUND_COLOR : this.base.eggBackgroundColor;
    }

    public int getEggSpotsColor() {
        return this.mounted ? RIDER_EGG_SPOTS_COLOR : this.base.eggSpotsColor;
    }

    /** Replaces the mob's whole equipment with this kit. */
    public void equip(Mob mob) {
        AotaArmor.equip(mob, this.armorSet);
        mob.setItemSlot(EquipmentSlot.MAINHAND, this.weapon.create());
        mob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
    }

    /**
     * Re-applies the kit at the end of spawning: vanilla spawning may enchant the gear or swap the helmet for a
     * Halloween pumpkin. Loot pickup is turned off so the mob never trades its kit for dropped gear.
     * Riders also get their skeleton horse here.
     */
    public void finishSpawn(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason) {
        this.equip(mob);
        mob.setCanPickUpLoot(false);
        // Only in a live world: during worldgen the horse would be saved before the rider gets on, losing the rider.
        if (this.mounted && !mob.isPassenger() && level instanceof ServerLevel serverLevel) {
            mountSkeletonHorse(mob, serverLevel, difficulty, reason);
        }
    }

    /** Puts the mob on a new skeleton horse, set up like the horses of vanilla skeleton traps. */
    private static void mountSkeletonHorse(Mob rider, ServerLevel level, DifficultyInstance difficulty, MobSpawnType reason) {
        SkeletonHorse horse = EntityType.SKELETON_HORSE.create(level);
        if (horse == null) {
            return;
        }
        horse.moveTo(rider.getX(), rider.getY(), rider.getZ(), rider.getYRot(), 0.0F);
        horse.finalizeSpawn(level, difficulty, reason, null, null);
        horse.setTamed(true);
        horse.setAge(0);
        horse.setPersistenceRequired();
        rider.startRiding(horse);
        // The rider is added by whoever is spawning it; the horse is not part of that, so add it here.
        level.addFreshEntity(horse);
    }

    /** The vanilla mob a kit is built on. */
    public enum Base {
        SKELETON(0xC1C1C1, 0x494949),
        WITHER_SKELETON(0x141414, 0x474D4D),
        ZOMBIE(0x00AFAF, 0x799C65),
        // Vanilla pillager colors darkened by 40%: the vanilla ones look almost like the guard egg colors.
        PILLAGER(0x321C20, 0x595D5D),
        VINDICATOR(0x959B9B, 0x275E61);

        /** Spawn egg colors of the base mob (vanilla, except the pillager's). */
        private final int eggBackgroundColor;
        private final int eggSpotsColor;

        Base(int eggBackgroundColor, int eggSpotsColor) {
            this.eggBackgroundColor = eggBackgroundColor;
            this.eggSpotsColor = eggSpotsColor;
        }

        public AttributeSupplier.Builder createAttributes() {
            return switch (this) {
                case SKELETON, WITHER_SKELETON -> AbstractSkeleton.createAttributes().add(Attributes.FOLLOW_RANGE, UNDEAD_FOLLOW_RANGE);
                case ZOMBIE -> Zombie.createAttributes().add(Attributes.FOLLOW_RANGE, UNDEAD_FOLLOW_RANGE);
                case PILLAGER -> Pillager.createAttributes();
                case VINDICATOR -> Vindicator.createAttributes();
            };
        }
    }

    private enum Weapon {
        SWORD {
            @Override
            ItemStack create() {
                return new ItemStack(Items.IRON_SWORD);
            }
        },
        /** Power V bow. */
        BOW {
            @Override
            ItemStack create() {
                ItemStack bow = new ItemStack(Items.BOW);
                bow.enchant(Enchantments.POWER_ARROWS, 5);
                return bow;
            }
        },
        /** Plain crossbow, like a vanilla pillager's. */
        CROSSBOW {
            @Override
            ItemStack create() {
                return new ItemStack(Items.CROSSBOW);
            }
        };

        abstract ItemStack create();
    }
}
