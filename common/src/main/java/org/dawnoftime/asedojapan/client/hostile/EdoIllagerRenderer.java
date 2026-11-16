package org.dawnoftime.asedojapan.client.hostile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;
import org.dawnoftime.asedojapan.client.VillagerHeadArmorLayer;
import org.dawnoftime.asedojapan.client.guard.EdoGuardModelLayers;

/**
 * Renders armored illagers on {@link EdoIllagerModel} with the vanilla illager texture of their base mob, plus an
 * armor layer that fits helmets to the villager-sized head.
 */
public class EdoIllagerRenderer extends HumanoidMobRenderer<AbstractIllager, EdoIllagerModel> {

    public static final ResourceLocation PILLAGER_TEXTURE = new ResourceLocation("textures/entity/illager/pillager.png");
    public static final ResourceLocation VINDICATOR_TEXTURE = new ResourceLocation("textures/entity/illager/vindicator.png");

    private final ResourceLocation texture;

    public EdoIllagerRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context, new EdoIllagerModel(context.bakeLayer(EdoIllagerModel.LAYER)), 0.5F);
        this.texture = texture;
        // Same player-sized armor layers as the guards, who have the same body shape.
        this.addLayer(new VillagerHeadArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(EdoGuardModelLayers.GUARD_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(EdoGuardModelLayers.GUARD_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public void render(AbstractIllager illager, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.setArmPoses(illager);
        super.render(illager, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    /** Maps the vanilla illager arm pose onto the humanoid arm poses; the melee swing is done by the model. */
    private void setArmPoses(AbstractIllager illager) {
        EdoIllagerModel model = this.getModel();
        HumanoidModel.ArmPose mainPose = switch (illager.getArmPose()) {
            case CROSSBOW_HOLD -> HumanoidModel.ArmPose.CROSSBOW_HOLD;
            case CROSSBOW_CHARGE -> HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            default -> illager.getMainHandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        };
        HumanoidModel.ArmPose offPose = illager.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        if (illager.getMainArm() == HumanoidArm.RIGHT) {
            model.rightArmPose = mainPose;
            model.leftArmPose = offPose;
        } else {
            model.rightArmPose = offPose;
            model.leftArmPose = mainPose;
        }
    }

    /** Same scale as the vanilla illager renderer. */
    @Override
    protected void scale(AbstractIllager illager, PoseStack poseStack, float partialTicks) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractIllager illager) {
        return this.texture;
    }
}
