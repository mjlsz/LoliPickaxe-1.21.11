package com.anotherstar.client.model;

import com.anotherstar.client.render.LoliRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code ModelLoli} (1.12.2 box model, 128x128 texture). */
public class LoliModel extends EntityModel<LoliRenderState> {

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public LoliModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.5F, -1.0F, -1.5F, 3, 8, 3), PartPose.offset(2.0F, 17.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(12, 0).addBox(-1.5F, -1.0F, -1.5F, 3, 8, 3), PartPose.offset(-2.0F, 17.0F, 0.0F));

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 41).addBox(-5.0F, 2.0F, -5.0F, 10, 2, 10)
                .texOffs(4, 31).addBox(-4.0F, 0.0F, -4.0F, 8, 2, 8)
                .texOffs(8, 20).addBox(-3.0F, -5.0F, -3.0F, 6, 5, 6)
                .texOffs(12, 13).addBox(-2.0F, -8.0F, -2.0F, 4, 3, 4), PartPose.offset(0.0F, 14.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(48, 0).addBox(-4.0F, -4.5F, -4.0F, 8, 8, 8, new CubeDeformation(-0.5F)),
                PartPose.offset(0.0F, 5.0F, 0.0F));

        head.addOrReplaceChild("hair", CubeListBuilder.create()
                .texOffs(80, 0).addBox(-4.0F, -4.5F, -4.0F, 8, 1, 8)
                .texOffs(80, 9).addBox(-4.0F, -3.5F, 3.0F, 8, 9, 1)
                .texOffs(112, 24).addBox(-3.0F, 5.5F, 3.0F, 6, 2, 1)
                .texOffs(80, 19).addBox(-2.0F, 7.5F, 3.5F, 4, 1, 1)
                .texOffs(90, 19).addBox(-2.0F, 8.5F, 4.0F, 4, 1, 1)
                .texOffs(80, 21).addBox(-1.0F, 9.5F, 4.5F, 2, 1, 1)
                .texOffs(86, 21).addBox(-1.0F, 10.5F, 5.0F, 2, 1, 1)
                .texOffs(104, 14).addBox(0.0F, -1.5F, -4.0F, 1, 1, 1)
                .texOffs(98, 15).addBox(1.0F, -3.5F, -4.0F, 1, 1, 1)
                .texOffs(104, 12).addBox(-2.0F, -3.5F, -4.0F, 1, 1, 1)
                .texOffs(98, 17).addBox(-4.0F, -1.5F, -4.0F, 1, 1, 1)
                .texOffs(104, 16).addBox(3.0F, -1.5F, -4.0F, 1, 1, 1)
                .texOffs(104, 9).addBox(-1.0F, -3.5F, -4.0F, 2, 2, 1)
                .texOffs(98, 9).addBox(2.0F, -3.5F, -4.0F, 2, 2, 1)
                .texOffs(98, 12).addBox(-4.0F, -3.5F, -4.0F, 2, 2, 1)
                .texOffs(112, 0).addBox(-4.0F, -3.5F, -3.0F, 1, 4, 6)
                .texOffs(102, 18).addBox(-4.0F, 0.5F, -1.0F, 1, 1, 4)
                .texOffs(112, 20).addBox(-4.0F, 1.5F, 1.0F, 1, 1, 2)
                .texOffs(124, 20).addBox(-4.0F, 2.5F, 2.0F, 1, 1, 1)
                .texOffs(102, 23).addBox(3.0F, 0.5F, -1.0F, 1, 1, 4)
                .texOffs(118, 20).addBox(3.0F, 1.5F, 1.0F, 1, 1, 2)
                .texOffs(124, 22).addBox(3.0F, 2.5F, 2.0F, 1, 1, 1)
                .texOffs(112, 10).addBox(3.0F, -3.5F, -3.0F, 1, 4, 6),
                PartPose.ZERO);

        root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(26, 0).addBox(0.0F, 2.0F, -1.0F, 2, 5, 2)
                .texOffs(24, 7).addBox(-0.5F, -1.0F, -1.5F, 3, 3, 3),
                PartPose.offsetAndRotation(3.0F, 10.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(38, 0).addBox(-2.0F, 2.0F, -1.0F, 2, 5, 2)
                .texOffs(36, 7).addBox(-2.5F, -1.0F, -1.5F, 3, 3, 3),
                PartPose.offsetAndRotation(-3.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(LoliRenderState state) {
        float limbSwing = state.walkAnimationPos;
        float limbSwingAmount = state.walkAnimationSpeed;
        float netHeadYaw = state.yRot;
        float headPitch = state.xRot;
        this.leftArm.xRot = Mth.cos(limbSwing + (float) Math.PI) * limbSwingAmount * 1.5F;
        this.rightArm.xRot = Mth.cos(limbSwing) * limbSwingAmount * 1.5F;
        this.leftLeg.xRot = Mth.cos(limbSwing) * limbSwingAmount * 1.5F;
        this.rightLeg.xRot = Mth.cos(limbSwing + (float) Math.PI) * limbSwingAmount * 1.5F;
        this.head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
        this.head.xRot = headPitch * (float) (Math.PI / 180.0);
    }
}

