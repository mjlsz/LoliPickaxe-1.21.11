package com.anotherstar.client.render;

import com.anotherstar.LoliPickaxe;
import com.anotherstar.client.model.LoliModel;
import com.anotherstar.common.entity.EntityLoli;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class LoliRenderer extends MobRenderer<EntityLoli, LoliRenderState, LoliModel> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(LoliPickaxe.id("loli"), "main");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(LoliPickaxe.MODID, "textures/entities/loli.png");

    public LoliRenderer(EntityRendererProvider.Context context) {
        super(context, new LoliModel(context.bakeLayer(LAYER)), 0.5F);
    }

    @Override
    public LoliRenderState createRenderState() {
        return new LoliRenderState();
    }

    @Override
    public Identifier getTextureLocation(LoliRenderState state) {
        return TEXTURE;
    }
}
