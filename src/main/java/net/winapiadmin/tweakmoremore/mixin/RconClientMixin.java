package net.winapiadmin.tweakmoremore.mixin;


import net.winapiadmin.tweakmoremore.Main;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.Socket;
import net.minecraft.server.rcon.RconClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RconClient.class)
public class RconClientMixin {

	@Shadow
	private Socket socket;

	@Unique
	private BufferedInputStream mixin$bufferedStream;

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/net/Socket;setSoTimeout(I)V"))
	private void redirectSetSoTimeout(Socket socket, int timeout) {
                if (!Main.config.get("bugfix.RconClient.setTimeout", false)) return;
		try {
			socket.setSoTimeout(30000);
		} catch (java.net.SocketException e) {
			throw new RuntimeException("Failed to set RCON socket timeout", e);
		}
	}

	@Redirect(method = "run", at = @At(value = "NEW", target = "(Ljava/io/InputStream;)Ljava/io/BufferedInputStream;"))
	private BufferedInputStream redirectBufferedStream(InputStream in) {
   	  if   (!Main.config.get("bugfix.RconClient.resetBufferedStreamProperly", false)) {
   	     return new BufferedInputStream(in); // vanilla behavior
  	        }
		if (this.mixin$bufferedStream == null) {
			this.mixin$bufferedStream = new BufferedInputStream(in);
		}
		return this.mixin$bufferedStream;
	}

	@Inject(method = "close", at = @At("HEAD"))
	private void onClose(CallbackInfo ci) {
                if (!Main.config.get("bugfix.RconClient.resetBufferedStreamProperly", false)) return;
		if (this.mixin$bufferedStream != null) {
			try {
				this.mixin$bufferedStream.close();
			} catch (java.io.IOException e) {
				// ignore
			}
			this.mixin$bufferedStream = null;
		}
	}
}
