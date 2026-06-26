package de.kyrohpaneup.betterlinkcraft.mixins.blocks;

import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.block.BlockBarrier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBarrier.class)
public class MixinBlockBarrier {
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    public void getRenderType(final CallbackInfoReturnable<Integer> cir) {
        if (Option.SHOW_BARRIERS.getBooleanValue()) cir.setReturnValue(3);
    }
}