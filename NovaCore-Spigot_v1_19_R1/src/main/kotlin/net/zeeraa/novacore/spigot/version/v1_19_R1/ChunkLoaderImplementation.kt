package net.zeeraa.novacore.spigot.version.v1_19_R1

import net.zeeraa.novacore.spigot.abstraction.ChunkLoader
import org.bukkit.Chunk

class ChunkLoaderImplementation : ChunkLoader() {
	override fun onAdd(chunk: Chunk) {
		chunk.isForceLoaded = true
	}

	override fun onRemove(chunk: Chunk) {
		chunk.isForceLoaded = false
	}
}