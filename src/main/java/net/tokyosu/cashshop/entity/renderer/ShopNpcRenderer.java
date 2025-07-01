package net.tokyosu.cashshop.entity.renderer;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.tokyosu.cashshop.CashShop;
import org.jetbrains.annotations.NotNull;

public class ShopNpcRenderer  extends MobRenderer<Villager, VillagerModel<Villager>> {
    private static final ResourceLocation VILLAGER_SKIN = ResourceLocation.fromNamespaceAndPath(CashShop.MOD_ID, "textures/entity/villager.png");

    public ShopNpcRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Villager villager) {
        return VILLAGER_SKIN;
    }
}
