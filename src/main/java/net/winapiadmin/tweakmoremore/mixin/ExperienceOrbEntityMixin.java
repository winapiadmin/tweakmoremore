package net.winapiadmin.tweakmoremore.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.winapiadmin.tweakmoremore.Main;
@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin {

    @Shadow
    public int getValue(){return 0;}

    @Shadow
    private void setValue(int value){}

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void writeNewValue(WriteView view, CallbackInfo ci) {
        view.putInt("NewValue", this.getValue());
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void readNewValue(ReadView view, CallbackInfo ci) {
        if (Main.config.get("bugfix.experienceOrbNoOverflow", false)) {
            this.setValue(view.getInt(
                "NewValue",
                view.getInt("Value", 0)
            ));
        }
    }
}