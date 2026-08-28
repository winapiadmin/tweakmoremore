package net.winapiadmin.tweakmoremore.mixin;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import net.minecraft.server.BannedIpList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import net.winapiadmin.tweakmoremore.Main;
@Mixin(BannedIpList.class)
public class BannedIpListMixin {

	@Overwrite
	private String stringifyAddress(SocketAddress address) {
                if (Main.config.get("bugfix.BannedIpList.IPnormalization",false))
		if (address instanceof InetSocketAddress inetSocketAddress) {
			InetAddress inetAddress = inetSocketAddress.getAddress();
			if (inetAddress != null) {
				byte[] bytes = inetAddress.getAddress();
				if (bytes.length == 16) {
					String normalized = mixin$tryNormalizeEmbeddedIpv4(bytes);
					if (normalized != null) {
						return normalized;
					}
				}

				return inetAddress.getHostAddress();
			}
		}

		String string = address.toString();
		if (string.contains("/")) {
			string = string.substring(string.indexOf(47) + 1);
		}

		if (string.contains(":")) {
			string = string.substring(0, string.indexOf(58));
		}

		return string;
	}

	@Unique
	private static String mixin$tryNormalizeEmbeddedIpv4(byte[] bytes) {
		boolean allZeroPrefix = true;
		for (int i = 0; i < 12; i++) {
			if (bytes[i] != 0) {
				allZeroPrefix = false;
				break;
			}
		}

		if (allZeroPrefix && bytes[10] == 0 && bytes[11] == 0) {
			return mixin$ipv4FromBytes(bytes[12], bytes[13], bytes[14], bytes[15]);
		} else if (allZeroPrefix && bytes[10] == (byte)255 && bytes[11] == (byte)255) {
			return mixin$ipv4FromBytes(bytes[12], bytes[13], bytes[14], bytes[15]);
		} else {
			return null;
		}
	}

	@Unique
	private static String mixin$ipv4FromBytes(byte a, byte b, byte c, byte d) {
		try {
			return InetAddress.getByAddress(new byte[]{a, b, c, d}).getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}
}