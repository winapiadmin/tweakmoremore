// credit to stackable127
package net.winapiadmin.tweakmoremore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.winapiadmin.tweakmoremore.Main;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class DrawContextMixin
{
    @Shadow @Final private Matrix3x2fStack matrices;

    /**
     * author Lonkachu
     * This code is a band-aid over the rendering issues that come from extending stack sizes above 999 as the text will start to creep onto other parts of the block,
     * I do want to at some point replace this with auto resizing text, maybe for next rewrite.
     * ModifyVariable is the best bet for this section as this method just so happens to include a string that is only used if the block text is not overwriten, extremely convienent
     */
    @Unique
    private final String[] suffixes = { "", "K", "M", "B" };

    @ModifyVariable(method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String modifyString(String value, TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String countOverride)
    {
        int count = stack.getCount();
        if (count <= 1)
        {
            return "";
        }
        if (Main.config.get(Registries.ITEM.getId(stack.getItem()).toShortString()+"_truncateItemCount", true))
        {
            int suffix = (int)Math.log10(count) / 3;
            count /= (int) Math.pow(1000, suffix);
            return count + suffixes[suffix];
        }
        else
        {
            return String.valueOf(count);
        }
    }

    @Unique
    private float scale(String s)
    {
        if (s.length() <= 2)
        {
            return 1.0f;
        }

        return 2.5f / s.length();
    }
    @Redirect(
            method = "drawStackCount",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I"
            )
    )
    private int width(TextRenderer instance, String s) {
        float f = scale(s);
        return (int)(instance.getWidth(s) * f);
    }
    @Inject(
            method = "drawStackCount",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)V"
            )
    )
    private void width(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String stackCountText, CallbackInfo ci) {
        String s = stackCountText == null ? String.valueOf(stack.getCount()) : stackCountText;

        float f = scale(s);
        this.matrices.translate(x * (1 - f), y * (1 - f) + (1 - f) * 16);
        this.matrices.scale(f, f);
    }
}