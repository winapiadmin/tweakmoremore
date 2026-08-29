package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.util.thread.ExecutorSampling;
import net.minecraft.util.thread.SampleableExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExecutorSampling.class)
public class ExecutorSamplingMixin {

	@Shadow
	private WeakHashMap<SampleableExecutor, Void> activeExecutors;

	@Redirect(
		method = "add",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/WeakHashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
		)
	)
	private Object synchronizedPut(WeakHashMap<SampleableExecutor, Void> map, Object key, Object value) {
		if (Main.config.get("bugfix.ExecutorSampling.syncPut", false)) return this.activeExecutors.put((SampleableExecutor)key, (Void)value);
		synchronized (this.activeExecutors) {
			return this.activeExecutors.put((SampleableExecutor)key, (Void)value);
		}
	}

	@Redirect(
		method = "createSamplers",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/WeakHashMap;keySet()Ljava/util/Set;"
		)
	)
	private Set<SampleableExecutor> synchronizedKeySet(WeakHashMap<SampleableExecutor, Void> map) {
		if (Main.config.get("bugfix.ExecutorSampling.syncKeySet", false)) return this.activeExecutors.keySet();
		synchronized (this.activeExecutors) {
			return new HashSet<>(this.activeExecutors.keySet());
		}
	}
}
