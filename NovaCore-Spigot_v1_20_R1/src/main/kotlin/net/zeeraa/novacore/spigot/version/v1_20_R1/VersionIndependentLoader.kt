package net.zeeraa.novacore.spigot.version.v1_20_R1

import net.novauniverse.spigot.version.shared.v1_16plus.NativeParticleImplementation
import net.zeeraa.novacore.spigot.abstraction.CommandRegistrator
import net.zeeraa.novacore.spigot.abstraction.Listeners
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentLoader
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils
import net.zeeraa.novacore.spigot.abstraction.particle.NovaParticleProvider

class VersionIndependentLoader : VersionIndependentLoader() {
	override fun getCommandRegistrator(): CommandRegistrator {
		return NMSBasedCommandRegistrator()
	}

	override fun getVersionIndependentUtils(): VersionIndependentUtils {
		return VersionIndependentUtilsImplementation(this)
	}

	override fun getListeners(): Listeners {
		return ListenersImplementation()
	}

	override fun getVersionSpecificParticleProvider(): NovaParticleProvider {
		return NativeParticleImplementation()
	}
}