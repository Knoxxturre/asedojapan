package org.dawnoftime.asedojapan.client.guard;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.dawnoftime.asedojapan.entity.guard.EdoGuard;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Renders Edo guards with arm poses for shields and crossbows.
 *
 * Adapted from GuardRenderer of Guard Villagers by seymourimadeit (TallestEgg), MIT License (see licenses/guardvillagers-LICENSE.txt).
 * Placeholder look: the vanilla nitwit villager (base skin, biome clothing, nitwit robe), referenced
 * from the vanilla resource pack rather than shipped with the mod.
 */
public class EdoGuardRenderer extends HumanoidMobRenderer<EdoGuard, EdoGuardModel> {

    private static final String TEXTURE_ROOT = "textures/entity/villager/";
    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(TEXTURE_ROOT + "villager.png");
    private static final ResourceLocation NITWIT_TEXTURE = new ResourceLocation(TEXTURE_ROOT + "profession/nitwit.png");
    private static final ResourceLocation DEFAULT_VARIANT_TEXTURE = variantTexture("plains");
    private static final Map<String, ResourceLocation> VARIANT_TEXTURES = Map.of(
            "desert", variantTexture("desert"),
            "jungle", variantTexture("jungle"),
            "plains", DEFAULT_VARIANT_TEXTURE,
            "savanna", variantTexture("savanna"),
            "snow", variantTexture("snow"),
            "swamp", variantTexture("swamp"),
            "taiga", variantTexture("taiga"));

    public EdoGuardRenderer(EntityRendererProvider.Context context) {
        super(context, new EdoGuardModel(context.bakeLayer(EdoGuardModelLayers.GUARD)), 0.5F);
        this.addLayer(new VariantLayer(this));
        this.addLayer(new GuardArmorLayer(this,
                new EdoGuardArmorModel(context.bakeLayer(EdoGuardModelLayers.GUARD_INNER_ARMOR)),
                new EdoGuardArmorModel(context.bakeLayer(EdoGuardModelLayers.GUARD_OUTER_ARMOR)),
                context.getModelManager()));
    }

    private static ResourceLocation variantTexture(String variant) {
        return new ResourceLocation(TEXTURE_ROOT + "type/" + variant + ".png");
    }

    @Override
    public void render(EdoGuard guard, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.setArmPoses(guard);
        super.render(guard, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void setArmPoses(EdoGuard guard) {
        EdoGuardModel model = this.getModel();
        ItemStack mainHand = guard.getMainHandItem();
        ItemStack offHand = guard.getOffhandItem();
        model.setAllVisible(true);
        HumanoidModel.ArmPose mainPose = armPose(guard, mainHand, offHand, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose offPose = armPose(guard, mainHand, offHand, InteractionHand.OFF_HAND);
        model.crouching = guard.isCrouching();
        if (guard.getMainArm() == HumanoidArm.RIGHT) {
            model.rightArmPose = mainPose;
            model.leftArmPose = offPose;
        } else {
            model.rightArmPose = offPose;
            model.leftArmPose = mainPose;
        }
    }

    private static HumanoidModel.ArmPose armPose(EdoGuard guard, ItemStack mainHand, ItemStack offHand, InteractionHand hand) {
        ItemStack stack = hand == InteractionHand.MAIN_HAND ? mainHand : offHand;
        if (stack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        if (guard.getUseItemRemainingTicks() > 0 && guard.getUsedItemHand() == hand) {
            UseAnim anim = stack.getUseAnimation();
            return switch (anim) {
                case BLOCK -> HumanoidModel.ArmPose.BLOCK;
                case BOW -> HumanoidModel.ArmPose.BOW_AND_ARROW;
                case SPEAR -> HumanoidModel.ArmPose.THROW_SPEAR;
                case CROSSBOW -> HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                default -> HumanoidModel.ArmPose.ITEM;
            };
        }
        if (guard.isAggressive()) {
            if (mainHand.getItem() instanceof CrossbowItem) {
                return hand == InteractionHand.MAIN_HAND ? HumanoidModel.ArmPose.CROSSBOW_HOLD : HumanoidModel.ArmPose.ITEM;
            }
            if (offHand.getItem() instanceof CrossbowItem && mainHand.getUseAnimation() == UseAnim.NONE) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
        }
        return HumanoidModel.ArmPose.ITEM;
    }

    @Override
    protected void scale(EdoGuard guard, PoseStack poseStack, float partialTicks) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(EdoGuard guard) {
        return BASE_TEXTURE;
    }

    /** Draws the biome clothing, then the nitwit robe, over the base skin (same order as vanilla villagers). */
    private static class VariantLayer extends RenderLayer<EdoGuard, EdoGuardModel> {

        VariantLayer(RenderLayerParent<EdoGuard, EdoGuardModel> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, EdoGuard guard,
                           float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (guard.isInvisible()) {
                return;
            }
            ResourceLocation texture = VARIANT_TEXTURES.getOrDefault(guard.getGuardVariant(), DEFAULT_VARIANT_TEXTURE);
            renderColoredCutoutModel(this.getParentModel(), texture, poseStack, buffer, packedLight, guard, 1.0F, 1.0F, 1.0F);
            renderColoredCutoutModel(this.getParentModel(), NITWIT_TEXTURE, poseStack, buffer, packedLight, guard, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Armor layer that lifts helmets onto the villager head, which is 2 pixels taller than a player's.
     * Vanilla and Armor of the Ages armor models both copy their head pose from the guard model, so the
     * head pivot is shifted for the duration of this layer only.
     */
    private static class GuardArmorLayer extends HumanoidArmorLayer<EdoGuard, EdoGuardModel, EdoGuardArmorModel> {

        private static final float HELMET_LIFT = 2.0F;

        GuardArmorLayer(RenderLayerParent<EdoGuard, EdoGuardModel> parent, EdoGuardArmorModel innerModel,
                        EdoGuardArmorModel outerModel, ModelManager modelManager) {
            super(parent, innerModel, outerModel, modelManager);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, EdoGuard guard,
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
                super.render(poseStack, buffer, packedLight, guard, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
            } finally {
                head.loadPose(headPose);
                hat.loadPose(hatPose);
            }
        }
    }
}
