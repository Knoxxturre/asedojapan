package org.dawnoftime.asedojapan.item;

/**
 * Tinting shared by the mod's spawn eggs. Their models add a helmet (layer 2) and a weapon marker (layer 3)
 * on top of the vanilla egg, and those layers keep their own colors instead of the egg tint.
 */
public final class EdoSpawnEggs {

    /** Model layers from this index up are not tinted with the egg colors. */
    public static final int FIRST_UNTINTED_LAYER = 2;

    private EdoSpawnEggs() {
    }

    /** Egg tint for a model layer: the vanilla egg color for the base layers, untinted (-1) for the rest. */
    public static int getColor(int tintIndex, int eggColor) {
        return tintIndex >= FIRST_UNTINTED_LAYER ? -1 : eggColor;
    }
}
