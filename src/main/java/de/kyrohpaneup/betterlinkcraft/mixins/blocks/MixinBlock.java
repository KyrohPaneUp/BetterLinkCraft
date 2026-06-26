package de.kyrohpaneup.betterlinkcraft.mixins.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBarrier;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock {

    @SideOnly(Side.CLIENT)
    @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
    public void getBarrierBlockLayer(CallbackInfoReturnable<EnumWorldBlockLayer> cir) {
        if ((Object) this instanceof BlockBarrier) {
            cir.setReturnValue(EnumWorldBlockLayer.TRANSLUCENT);
        }
    }
}