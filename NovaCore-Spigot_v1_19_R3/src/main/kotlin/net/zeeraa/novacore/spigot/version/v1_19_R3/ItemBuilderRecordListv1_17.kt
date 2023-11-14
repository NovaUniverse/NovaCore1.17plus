package net.zeeraa.novacore.spigot.version.v1_19_R3

import net.zeeraa.novacore.spigot.abstraction.ItemBuilderRecordList
import org.bukkit.Material

class ItemBuilderRecordListv1_17 : ItemBuilderRecordList {
	private val recordMap: HashMap<String, Material> = HashMap()
	override fun getRecordMap(): HashMap<String, Material> {
		return recordMap
	}

	init {
		recordMap["11"] = Material.MUSIC_DISC_11
		recordMap["13"] = Material.MUSIC_DISC_13
		recordMap["blocks"] = Material.MUSIC_DISC_BLOCKS
		recordMap["cat"] = Material.MUSIC_DISC_CAT
		recordMap["chirp"] = Material.MUSIC_DISC_CHIRP
		recordMap["far"] = Material.MUSIC_DISC_FAR
		recordMap["mall"] = Material.MUSIC_DISC_MALL
		recordMap["mellohi"] = Material.MUSIC_DISC_MELLOHI
		recordMap["pigstep"] = Material.MUSIC_DISC_PIGSTEP
		recordMap["stal"] = Material.MUSIC_DISC_STAL
		recordMap["strad"] = Material.MUSIC_DISC_STRAD
		recordMap["wait"] = Material.MUSIC_DISC_WAIT
		recordMap["ward"] = Material.MUSIC_DISC_WARD
	}
}