package org.dawnoftime.asedojapan.client.guard;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;

/**
 * Villager-shaped humanoid model laid out for the vanilla 64x64 villager textures.
 * Head, robe and legs use the vanilla villager UVs; each free arm is built from the villager's
 * crossed-arms texture (upper arm + half of the forearm bar).
 *
 * Kick and weapon animations adapted from GuardModel of Guard Villagers by seymourimadeit (TallestEgg), MIT License (see licenses/guardvillagers-LICENSE.txt).
 */
public class EdoGuardModel extends HumanoidModel<EdoGuard> {

    public EdoGuardModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        // HumanoidModel copies the head pose onto the hat every frame.
        PartDefinition hat = root.addOrReplaceChild("hat",
                CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.51F)),
                PartPose.ZERO);
        hat.addOrReplaceChild("hat_rim",
                CubeListBuilder.create().texOffs(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F),
                PartPose.rotation(-Mth.HALF_PI, 0.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F),
                PartPose.ZERO);
        body.addOrReplaceChild("jacket",
                CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)),
                PartPose.ZERO);

        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
                        .texOffs(40, 38).addBox(-3.0F, 6.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create().mirror()
                        .texOffs(44, 22).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
                        .texOffs(40, 38).addBox(-1.0F, 6.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-2.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(2.0F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(EdoGuard guard, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(guard, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // Biome hats would clip through helmets.
        this.hat.visible = guard.getItemBySlot(EquipmentSlot.HEAD).isEmpty();

        if (guard.getKickTicks() > 0) {
            float progress = 1.0F - (float) Mth.abs(10 - 2 * guard.getKickTicks()) / 10.0F;
            this.rightLeg.xRot = Mth.lerp(progress, this.rightLeg.xRot, -1.4F);
        }

        // Hold melee weapons high while charging at a target.
        boolean holdingRanged = guard.getMainHandItem().getItem() instanceof ProjectileWeaponItem;
        if (this.attackTime == 0.0F
                && guard.isAggressive()
                && !holdingRanged
                && guard.getDeltaMovement().horizontalDistanceSqr() > 0.005D
                && !guard.getMainHandItem().isEmpty()
                && !guard.isBlocking()) {
            if (guard.isLeftHanded()) {
                this.leftArm.xRot = -1.8F;
            } else {
                this.rightArm.xRot = -1.8F;
            }
        }
    }
}
