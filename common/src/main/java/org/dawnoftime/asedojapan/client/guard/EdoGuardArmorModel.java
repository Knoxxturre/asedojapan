package org.dawnoftime.asedojapan.client.guard;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;

/**
 * Player-sized vanilla armor model for guards; the illagers reuse its layers. Armor of the Ages pieces use their own
 * models instead; {@link org.dawnoftime.asedojapan.client.VillagerHeadArmorLayer} lifts the helmet of both to fit
 * the taller villager head.
 */
public class EdoGuardArmorModel extends HumanoidModel<EdoGuard> {

    public EdoGuardArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createOuterArmorLayer() {
        return LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(1.0F), 0.0F), 64, 32);
    }

    public static LayerDefinition createInnerArmorLayer() {
        return LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.5F), 0.0F), 64, 32);
    }
}
