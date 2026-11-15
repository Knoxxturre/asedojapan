package org.dawnoftime.asedojapan.client.guard;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;

/**
 * Vanilla armor model raised to fit the taller villager head. Armor of the Ages pieces use their own models instead.
 *
 * Adapted from GuardArmorModel of Guard Villagers by seymourimadeit (TallestEgg), MIT License (see licenses/guardvillagers-LICENSE.txt).
 */
public class EdoGuardArmorModel extends HumanoidModel<EdoGuard> {

    public EdoGuardArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createOuterArmorLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(1.0F), 0.0F);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
                PartPose.offset(0.0F, 1.0F, 0.0F));
        root.addOrReplaceChild("hat",
                CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.5F)),
                PartPose.offset(0.0F, 1.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition createInnerArmorLayer() {
        return LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.5F), 0.0F), 64, 32);
    }
}
