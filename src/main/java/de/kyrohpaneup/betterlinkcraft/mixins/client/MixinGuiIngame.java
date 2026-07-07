package de.kyrohpaneup.betterlinkcraft.mixins.client;

import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @SideOnly(Side.CLIENT)
    @Inject(
            method = "renderScoreboard",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderScoreboard(ScoreObjective objective, ScaledResolution resolution, CallbackInfo ci) {
        if (Option.HIDE_SCOREBOARD.getBooleanValue()) {
            ci.cancel();
        }
    }
}
