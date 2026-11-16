package org.dawnoftime.asedojapan.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Armor layer for mobs with the villager head (guards and illagers), which is 2 pixels taller than a player's.
 * Vanilla and Armor of the Ages armor models both copy their head pose from the parent model, so the head pivot
 * is shifted up for the duration of this layer only, which lifts the helmet onto the head.
 */
public class VillagerHeadArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
        extends HumanoidArmorLayer<T, M, A> {

    private static final float HELMET_LIFT = 2.0F;

    public VillagerHeadArmorLayer(RenderLayerParent<T, M> parent, A innerModel, A outerModel, ModelManager modelManager) {
        super(parent, innerModel, outerModel, modelManager);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart head = this.getParentModel().head;
        ModelPart hat = this.getParentModel().hat;
        PartPose headPose = head.storePose();
        PartPose hatPose = hat.storePose();
        // Lift along the head's own up axis so the helmet keeps turning around the real neck pivot.
        Vector3f lift = new Vector3f(0.0F, -HELMET_LIFT, 0.0F)
                .rotate(new Quaternionf().rotationZYX(head.zRot, head.yRot, head.xRot));
        head.offsetPos(lift);
        hat.offsetPos(lift);
        try {
            super.render(poseStack, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
        } finally {
            head.loadPose(headPose);
            hat.loadPose(hatPose);
        }
    }
}
