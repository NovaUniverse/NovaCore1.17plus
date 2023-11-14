package net.zeeraa.novacore.spigot.version.v1_20_R2

import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPosition
import net.minecraft.nbt.GameProfileSerializer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.EntityFallingBlock
import net.minecraft.world.level.block.state.BlockBase
import net.novauniverse.novacore1_17plus.shared.BaseVersionIndependentUtilImplementation1_17Plus
import net.novauniverse.novacore1_17plus.shared.DyeColorToMaterialMapper_1_17
import net.zeeraa.novacore.commons.log.Log
import net.zeeraa.novacore.commons.utils.ListUtils
import net.zeeraa.novacore.spigot.abstraction.ChunkLoader
import net.zeeraa.novacore.spigot.abstraction.ItemBuilderRecordList
import net.zeeraa.novacore.spigot.abstraction.MaterialNameList
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentItems
import net.zeeraa.novacore.spigot.abstraction.commons.AttributeInfo
import net.zeeraa.novacore.spigot.abstraction.commons.EntityBoundingBox
import net.zeeraa.novacore.spigot.abstraction.enums.*
import net.zeeraa.novacore.spigot.abstraction.log.AbstractionLogger
import net.zeeraa.novacore.spigot.abstraction.manager.CustomSpectatorManager
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld
import org.bukkit.craftbukkit.v1_20_R2.entity.*
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_20_R2.util.CraftMagicNumbers
import org.bukkit.entity.*
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.text.DecimalFormat
import java.util.*
import java.util.function.Consumer

class VersionIndependentUtilsImplementation(loader: VersionIndependentLoader?) : BaseVersionIndependentUtilImplementation1_17Plus(loader!!, DyeColorToMaterialMapper_1_17()) {
	private val itemBuilderRecordList: ItemBuilderRecordList
	private var damagePlayerWarningShown = false
	private var chunkLoader: ChunkLoader? = null
	override fun getChunkLoader(): ChunkLoader {
		if (chunkLoader == null) {
			chunkLoader = ChunkLoaderImplementation()
		}
		return chunkLoader!!
	}

	override fun getItemBuilderRecordList(): ItemBuilderRecordList {
		return itemBuilderRecordList
	}

	@Suppress("DEPRECATION")
	override fun getRecentTps(): DoubleArray {
		// Deprecated but still the way spigot does it in the /tps command
		return MinecraftServer.getServer().recentTps
	}

	override fun damagePlayer(player: Player, reason: PlayerDamageReason, damage: Float) {
		// TODO: implement this
		if (!damagePlayerWarningShown) {
			damagePlayerWarningShown = true
			AbstractionLogger.getLogger().warning("Nova VersionIndependentUtils v1_20_R1", "damagePlayer will not work on 1.17 and will instead call Player#damage(double)")
		}
		player.damage(damage.toDouble())

		/*
		 * DamageSource source;
		 *
		 * switch (reason) { case FALL: source = DamageSource.FALL;
		 *
		 * case FALLING_BLOCK: source = DamageSource.FALLING_BLOCK; break; case
		 * OUT_OF_WORLD: source = DamageSource.OUT_OF_WORLD; break;
		 *
		 * case BURN: source = DamageSource.BURN; break;
		 *
		 * case LIGHTNING: source = DamageSource.LIGHTNING; break;
		 *
		 * case MAGIC: source = DamageSource.MAGIC; break;
		 *
		 * case DROWN: source = DamageSource.DROWN; break;
		 *
		 * case STARVE: source = DamageSource.STARVE; break;
		 *
		 * case LAVA: source = DamageSource.LAVA; break;
		 *
		 * case GENERIC: source = DamageSource.GENERIC; break;
		 *
		 * default: source = DamageSource.GENERIC; break; }
		 *
		 * ((CraftPlayer) player).getHandle().damageEntity(source, damage);
		 */
	}

	override fun getColoredMaterial(color: DyeColor, type: ColoredBlockType): Material {
		// For some reason this returned air every time, so I decide to hard code it
		// instead since I did not have a lot of time to fix the issue
		if (color == DyeColor.WHITE && type == ColoredBlockType.GLASS_PANE) {
			return Material.WHITE_STAINED_GLASS_PANE
		}
		val material: Material
		if (type == ColoredBlockType.GLASS_BLOCK) {
			material = when (color) {
				DyeColor.BLACK -> Material.BLACK_STAINED_GLASS
				DyeColor.BLUE -> Material.BLUE_STAINED_GLASS
				DyeColor.BROWN -> Material.BROWN_STAINED_GLASS
				DyeColor.CYAN -> Material.CYAN_STAINED_GLASS
				DyeColor.GRAY -> Material.LIGHT_GRAY_STAINED_GLASS
				DyeColor.GREEN -> Material.GREEN_STAINED_GLASS
				DyeColor.LIGHT_BLUE -> Material.LIGHT_BLUE_STAINED_GLASS
				DyeColor.LIGHT_GRAY -> Material.LIGHT_GRAY_STAINED_GLASS
				DyeColor.LIME -> Material.LIME_STAINED_GLASS
				DyeColor.MAGENTA -> Material.MAGENTA_STAINED_GLASS
				DyeColor.ORANGE -> Material.ORANGE_STAINED_GLASS
				DyeColor.PINK -> Material.PINK_STAINED_GLASS
				DyeColor.PURPLE -> Material.PURPLE_STAINED_GLASS
				DyeColor.RED -> Material.RED_STAINED_GLASS
				DyeColor.YELLOW -> Material.YELLOW_STAINED_GLASS
				DyeColor.WHITE -> Material.WHITE_STAINED_GLASS
				else -> Material.AIR
			}
		} else if (type == ColoredBlockType.GLASS_PANE) {
			when (color) {
				DyeColor.BLACK -> material = Material.BLACK_STAINED_GLASS_PANE
				DyeColor.BLUE -> material = Material.BLUE_STAINED_GLASS_PANE
				DyeColor.BROWN -> material = Material.BROWN_STAINED_GLASS_PANE
				DyeColor.CYAN -> material = Material.CYAN_STAINED_GLASS_PANE
				DyeColor.GRAY -> material = Material.LIGHT_GRAY_STAINED_GLASS_PANE
				DyeColor.GREEN -> material = Material.GREEN_STAINED_GLASS_PANE
				DyeColor.LIGHT_BLUE -> material = Material.LIGHT_BLUE_STAINED_GLASS_PANE
				DyeColor.LIGHT_GRAY -> material = Material.LIGHT_GRAY_STAINED_GLASS_PANE
				DyeColor.LIME -> material = Material.LIME_STAINED_GLASS_PANE
				DyeColor.MAGENTA -> material = Material.MAGENTA_STAINED_GLASS_PANE
				DyeColor.ORANGE -> material = Material.ORANGE_STAINED_GLASS_PANE
				DyeColor.PINK -> material = Material.PINK_STAINED_GLASS_PANE
				DyeColor.PURPLE -> material = Material.PURPLE_STAINED_GLASS_PANE
				DyeColor.RED -> material = Material.RED_STAINED_GLASS_PANE
				DyeColor.YELLOW -> material = Material.YELLOW_STAINED_GLASS_PANE

				else -> material = Material.AIR
			}
		} else if (type == ColoredBlockType.WOOL) {
			material = when (color) {
				DyeColor.BLACK -> Material.BLACK_WOOL
				DyeColor.BLUE -> Material.BLUE_WOOL
				DyeColor.BROWN -> Material.BROWN_WOOL
				DyeColor.CYAN -> Material.CYAN_WOOL
				DyeColor.GRAY -> Material.LIGHT_GRAY_WOOL
				DyeColor.GREEN -> Material.GREEN_WOOL
				DyeColor.LIGHT_BLUE -> Material.LIGHT_BLUE_WOOL
				DyeColor.LIGHT_GRAY -> Material.LIGHT_GRAY_WOOL
				DyeColor.LIME -> Material.LIME_WOOL
				DyeColor.MAGENTA -> Material.MAGENTA_WOOL
				DyeColor.ORANGE -> Material.ORANGE_WOOL
				DyeColor.PINK -> Material.PINK_WOOL
				DyeColor.PURPLE -> Material.PURPLE_WOOL
				DyeColor.RED -> Material.RED_WOOL
				DyeColor.YELLOW -> Material.YELLOW_WOOL
				DyeColor.WHITE -> Material.WHITE_WOOL
				else -> Material.AIR
			}
		} else if (type == ColoredBlockType.CLAY) {
			material = when (color) {
				DyeColor.BLACK -> Material.BLACK_TERRACOTTA
				DyeColor.BLUE -> Material.BLUE_TERRACOTTA
				DyeColor.BROWN -> Material.BROWN_TERRACOTTA
				DyeColor.CYAN -> Material.CYAN_TERRACOTTA
				DyeColor.GRAY -> Material.LIGHT_GRAY_TERRACOTTA
				DyeColor.GREEN -> Material.GREEN_TERRACOTTA
				DyeColor.LIGHT_BLUE -> Material.LIGHT_BLUE_TERRACOTTA
				DyeColor.LIGHT_GRAY -> Material.LIGHT_GRAY_TERRACOTTA
				DyeColor.LIME -> Material.LIME_TERRACOTTA
				DyeColor.MAGENTA -> Material.MAGENTA_TERRACOTTA
				DyeColor.ORANGE -> Material.ORANGE_TERRACOTTA
				DyeColor.PINK -> Material.PINK_TERRACOTTA
				DyeColor.PURPLE -> Material.PURPLE_TERRACOTTA
				DyeColor.RED -> Material.RED_TERRACOTTA
				DyeColor.YELLOW -> Material.YELLOW_TERRACOTTA
				DyeColor.WHITE -> Material.WHITE_TERRACOTTA
				else -> Material.AIR
			}
		} else {
			material = Material.AIR
		}
		return material
	}

	override fun getSound(sound: VersionIndependentSound): Sound? {
		return when (sound) {
			VersionIndependentSound.NOTE_PLING -> Sound.BLOCK_NOTE_BLOCK_PLING
			VersionIndependentSound.NOTE_HAT -> Sound.BLOCK_NOTE_BLOCK_HAT
			VersionIndependentSound.WITHER_DEATH -> Sound.ENTITY_WITHER_DEATH
			VersionIndependentSound.WITHER_HURT -> Sound.ENTITY_WITHER_HURT
			VersionIndependentSound.ITEM_BREAK -> Sound.ENTITY_ITEM_BREAK
			VersionIndependentSound.ITEM_PICKUP -> Sound.ENTITY_ITEM_PICKUP
			VersionIndependentSound.ORB_PICKUP -> Sound.ENTITY_EXPERIENCE_ORB_PICKUP
			VersionIndependentSound.ANVIL_LAND -> Sound.BLOCK_ANVIL_LAND
			VersionIndependentSound.EXPLODE -> Sound.ENTITY_GENERIC_EXPLODE
			VersionIndependentSound.LEVEL_UP -> Sound.ENTITY_PLAYER_LEVELUP
			VersionIndependentSound.WITHER_SHOOT -> Sound.ENTITY_WITHER_SHOOT
			VersionIndependentSound.EAT -> Sound.ENTITY_GENERIC_EAT
			VersionIndependentSound.ANVIL_BREAK -> Sound.BLOCK_ANVIL_BREAK
			VersionIndependentSound.FIZZ -> Sound.BLOCK_FIRE_EXTINGUISH
			VersionIndependentSound.ENDERMAN_TELEPORT -> Sound.ENTITY_ENDERMAN_TELEPORT
			VersionIndependentSound.CLICK -> Sound.BLOCK_LEVER_CLICK
			VersionIndependentSound.BLAZE_HIT -> Sound.ENTITY_BLAZE_HURT
			VersionIndependentSound.BURP -> Sound.ENTITY_PLAYER_BURP
			VersionIndependentSound.FUSE -> Sound.ENTITY_TNT_PRIMED
			else -> {
				lastError = VersionIndependenceLayerError.MISSING_SOUND
				AbstractionLogger.getLogger().error("VersionIndependentUtils", "VersionIndependentSound " + sound.name + " is not defined in this version. Please add it to " + this.javaClass.name)
				null
			}
		}
	}

	override fun getVersionIndependantItems(): VersionIndependentItems {
		return VersionIndependantItemsImplementation()
	}

	override fun getMaterial(material: VersionIndependentMaterial): Material? {
		return when (material) {
			VersionIndependentMaterial.FILLED_MAP -> Material.FILLED_MAP
			VersionIndependentMaterial.END_STONE -> Material.END_STONE
			VersionIndependentMaterial.WORKBENCH -> Material.CRAFTING_TABLE
			VersionIndependentMaterial.OAK_BOAT -> Material.OAK_BOAT
			VersionIndependentMaterial.DIAMOND_SHOVEL -> Material.DIAMOND_SHOVEL
			VersionIndependentMaterial.SNOWBALL -> Material.SNOWBALL
			VersionIndependentMaterial.FARMLAND -> Material.FARMLAND
			VersionIndependentMaterial.GOLDEN_AXE -> Material.GOLDEN_AXE
			VersionIndependentMaterial.GOLDEN_HOE -> Material.GOLDEN_HOE
			VersionIndependentMaterial.GOLDEN_PICKAXE -> Material.GOLDEN_PICKAXE
			VersionIndependentMaterial.GOLDEN_SHOVEL -> Material.GOLDEN_SHOVEL
			VersionIndependentMaterial.GOLDEN_SWORD -> Material.GOLDEN_SWORD
			VersionIndependentMaterial.WOODEN_AXE -> Material.WOODEN_AXE
			VersionIndependentMaterial.WOODEN_HOE -> Material.WOODEN_HOE
			VersionIndependentMaterial.WOODEN_PICKAXE -> Material.WOODEN_PICKAXE
			VersionIndependentMaterial.WOODEN_SHOVEL -> Material.WOODEN_SHOVEL
			VersionIndependentMaterial.WOODEN_SWORD -> Material.WOODEN_SWORD
			VersionIndependentMaterial.WATCH -> Material.CLOCK
			VersionIndependentMaterial.GOLD_HELMET -> Material.GOLDEN_HELMET
			VersionIndependentMaterial.GOLD_CHESTPLATE -> Material.GOLDEN_CHESTPLATE
			VersionIndependentMaterial.GOLD_LEGGINGS -> Material.GOLDEN_LEGGINGS
			VersionIndependentMaterial.GOLD_BOOTS -> Material.GOLDEN_BOOTS
			VersionIndependentMaterial.GRILLED_PORK -> Material.COOKED_PORKCHOP
			VersionIndependentMaterial.EXP_BOTTLE -> Material.EXPERIENCE_BOTTLE
			VersionIndependentMaterial.WOOL -> Material.WHITE_WOOL
			VersionIndependentMaterial.FIREBALL -> Material.FIRE_CHARGE
			VersionIndependentMaterial.GUNPOWDER -> Material.GUNPOWDER
			else -> {
				lastError = VersionIndependenceLayerError.MISSING_MATERIAL
				AbstractionLogger.getLogger().warning("VersionIndependentUtils", "Unknown version Independent material: " + material.name)
				null
			}
		}
	}

	override fun getNovaCoreGameVersion(): NovaCoreGameVersion {
		return NovaCoreGameVersion.V_1_20_R2
	}

	init {
		itemBuilderRecordList = ItemBuilderRecordListv1_17()
	}

	override fun isSign(material: Material): Boolean {
		for (m2 in SIGN_MATERIALS) {
			if (material == m2) {
				return true
			}
		}
		return false
	}

	override fun getMinY(): Int {
		return -64
	}

	override fun getPlayerBodyRotation(player: Player): Float {
		val craftPlayer = player as CraftPlayer
		return craftPlayer.handle.aT
	}

	@Suppress("DEPRECATION")
	override fun setGameRule(world: World, rule: String, value: String) {
		world.setGameRuleValue(rule, value)
	}

	@Suppress("DEPRECATION")
	override fun getDeathTypeFromDamage(e: EntityDamageEvent, lastDamager: org.bukkit.entity.Entity?): DeathType {
		return when (e.cause) {
			EntityDamageEvent.DamageCause.FIRE -> {
				if (lastDamager != null) DeathType.FIRE_SOURCE_COMBAT else DeathType.FIRE_SOURCE
			}

			EntityDamageEvent.DamageCause.LAVA -> {
				if (lastDamager != null) DeathType.LAVA_COMBAT else DeathType.LAVA
			}

			EntityDamageEvent.DamageCause.FALL -> if (e.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE) <= 2.0) {
				if (lastDamager != null) DeathType.FALL_SMALL_COMBAT else DeathType.FALL_SMALL
			} else {
				DeathType.FALL_BIG
			}

			EntityDamageEvent.DamageCause.VOID -> {
				if (lastDamager != null) DeathType.VOID_COMBAT else DeathType.VOID
			}

			EntityDamageEvent.DamageCause.THORNS -> DeathType.THORNS
			EntityDamageEvent.DamageCause.WITHER -> {
				if (lastDamager != null) DeathType.EFFECT_WITHER_COMBAT else DeathType.EFFECT_WITHER
			}

			EntityDamageEvent.DamageCause.CONTACT -> {
				if (e is EntityDamageByBlockEvent) {
					if (lastDamager != null) {
						if (e.damager!!.type == Material.SWEET_BERRY_BUSH) return DeathType.BUSH_COMBAT else if (e.damager!!.type == Material.CACTUS) return DeathType.CACTUS_COMBAT else if (e.damager!!.type == Material.POINTED_DRIPSTONE) return DeathType.FALL_STALAGMITE_COMBAT
					} else {
						if (e.damager!!.type == Material.SWEET_BERRY_BUSH) return DeathType.BUSH else if (e.damager!!.type == Material.CACTUS) return DeathType.CACTUS else if (e.damager!!.type == Material.POINTED_DRIPSTONE) return DeathType.FALL_STALAGMITE
					}
				}
				if (lastDamager != null) DeathType.DROWN_COMBAT else DeathType.DROWN
			}

			EntityDamageEvent.DamageCause.DROWNING -> {
				if (lastDamager != null) DeathType.DROWN_COMBAT else DeathType.DROWN
			}

			EntityDamageEvent.DamageCause.LIGHTNING -> {
				if (lastDamager != null) DeathType.LIGHTNING_COMBAT else DeathType.LIGHTNING
			}

			EntityDamageEvent.DamageCause.PROJECTILE -> {
				if (lastDamager!!.type == EntityType.ARROW) {
					DeathType.PROJECTILE_ARROW
				} else DeathType.PROJECTILE_OTHER
			}

			EntityDamageEvent.DamageCause.STARVATION -> {
				if (lastDamager != null) DeathType.STARVING_COMBAT else DeathType.STARVING
			}

			EntityDamageEvent.DamageCause.SUFFOCATION -> {
				if (lastDamager != null) DeathType.SUFFOCATION_COMBAT else DeathType.SUFFOCATION
			}

			EntityDamageEvent.DamageCause.ENTITY_ATTACK, EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK -> when (lastDamager!!.type) {
				EntityType.WITHER -> DeathType.COMBAT_WITHER_SKULL
				EntityType.FIREBALL, EntityType.SMALL_FIREBALL -> DeathType.COMBAT_FIREBALL
				EntityType.BEE -> DeathType.COMBAT_BEE
				else -> DeathType.COMBAT_NORMAL
			}

			EntityDamageEvent.DamageCause.FALLING_BLOCK -> {
				if (e is EntityDamageByEntityEvent) {
					if (e.damager is FallingBlock) {
						val block = e.damager as FallingBlock
						return when (block.blockData.material) {
							Material.ANVIL -> {
								if (lastDamager != null) DeathType.ANVIL_FALL_COMBAT else DeathType.ANVIL_FALL
							}

							Material.POINTED_DRIPSTONE -> {
								if (lastDamager != null) DeathType.STALAGTITE_FALL_COMBAT else DeathType.STALAGTITE_FALL
							}

							else -> {
								if (lastDamager != null) DeathType.BLOCK_FALL_COMBAT else DeathType.BLOCK_FALL
							}
						}
					}
				}
				if (lastDamager != null) DeathType.EXPLOSION_COMBAT else DeathType.EXPLOSION
			}

			EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION -> {
				if (lastDamager != null) DeathType.EXPLOSION_COMBAT else DeathType.EXPLOSION
			}

			EntityDamageEvent.DamageCause.FIRE_TICK -> {
				if (lastDamager != null) DeathType.FIRE_NATURAL_COMBAT else DeathType.FIRE_NATURAL
			}

			EntityDamageEvent.DamageCause.MAGIC -> {
				var type = DeathType.MAGIC
				if (lastDamager != null) {
					if (e is EntityDamageByEntityEvent) {
						if (e.damager is ThrownPotion) {
							val potion = e.damager as ThrownPotion
							if (potion.shooter is org.bukkit.entity.Entity) {
								type = if ((potion.shooter as org.bukkit.entity.Entity?)!!.uniqueId.toString().equals(lastDamager.uniqueId.toString(), ignoreCase = true)) {
									DeathType.MAGIC_COMBAT
								} else {
									DeathType.MAGIC_COMBAT_ACCIDENT
								}
							}
						}
					}
				}
				type
			}

			EntityDamageEvent.DamageCause.CRAMMING -> {
				if (lastDamager != null) DeathType.SUFFOCATION_CRAMMING_COMBAT else DeathType.SUFFOCATION_COMBAT
			}

			EntityDamageEvent.DamageCause.HOT_FLOOR -> {
				if (lastDamager != null) DeathType.MAGMA_BLOCK_COMBAT else DeathType.MAGMA_BLOCK
			}

			EntityDamageEvent.DamageCause.DRAGON_BREATH -> {
				if (lastDamager != null) DeathType.DRAGON_BREATH_COMBAT else DeathType.DRAGON_BREATH
			}

			EntityDamageEvent.DamageCause.FLY_INTO_WALL -> {
				if (lastDamager != null) DeathType.ELYTRA_WALL_COMBAT else DeathType.ELYTRA_WALL
			}

			EntityDamageEvent.DamageCause.FREEZE -> {
				if (lastDamager != null) DeathType.FROZEN_COMBAT else DeathType.FROZEN
			}

			EntityDamageEvent.DamageCause.SUICIDE, EntityDamageEvent.DamageCause.DRYOUT, EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.MELTING, EntityDamageEvent.DamageCause.POISON -> {
				if (lastDamager != null) DeathType.GENERIC_COMBAT else DeathType.GENERIC
			}

			else -> {
				if (lastDamager != null) DeathType.GENERIC_COMBAT else DeathType.GENERIC
			}
		}
	}

	override fun canBreakBlock(itemStack: ItemStack, material: Material): Boolean {
		val nmsItem = CraftItemStack.asNMSCopy(itemStack)
		val nbtTag = nmsItem.v()
		val list = nbtTag!!.c("CanDestroy", 8) ?: return false
		try {
			val f = NBTTagList::class.java.getDeclaredField("c")
			f.isAccessible = true
			for (nbt in f[list] as List<NBTTagString>) {
				val b = getMaterialFromName(nbt.r_()) == material
				if (b) {
					return true
				}
			}
		} catch (e1: Exception) {
			return false
		}
		return false
	}

	override fun getMaterialNameList(): MaterialNameList? {
		// I believe 1.16+ has all names mirror their Material type, if not tell me
		return null
	}

	override fun getMaterialFromName(s: String): Material? {
		try {
			val value = s.toInt()
			for (material in Material.values()) {
				@Suppress("DEPRECATION")
				if (value == material.id) {
					return material
				}
			}
			return null
		} catch (ignored: Exception) {
		}
		return Material.matchMaterial(s.replace("minecraft:", "").lowercase())!!
	}

	override fun sendPacket(player: Player, packet: Any) {
		if (packet is Packet<*>) {
			(player as CraftPlayer).handle.c.a(packet)
		} else {
			AbstractionLogger.getLogger().warning("NovaCore", "Packet sent isn't instance of " + Packet::class.java.canonicalName)
		}
	}

	override fun addAttribute(item: ItemStack, meta: ItemMeta, attributeInfo: AttributeInfo?) {
		if (attributeInfo == null) {
			AbstractionLogger.getLogger().error("VersionIndependentUtils", "AttributeInfo is null")
			return
		}
		if (attributeInfo.attribute == null) {
			AbstractionLogger.getLogger().error("VersionIndependentUtils", "Attribute is null")
			return
		}
		val newList = ListUtils.removeDuplicates(attributeInfo.equipmentSlots)
		if (newList.contains(net.zeeraa.novacore.spigot.abstraction.enums.EquipmentSlot.ALL)) {
			val modifier = AttributeModifier(UUID.randomUUID(), attributeInfo.attribute.key,
				attributeInfo.value, AttributeModifier.Operation.valueOf(attributeInfo.operation.name))
			if (!meta.addAttributeModifier(Attribute.valueOf(attributeInfo.attribute.name), modifier)) {
				AbstractionLogger.getLogger().error("VersionIndependentUtils", "Something went wrong when adding the attribute " + attributeInfo.attribute.key)
			}
		} else {
			for (eSlot in newList) {
				val modifier = AttributeModifier(UUID.randomUUID(), attributeInfo.attribute.key,
					attributeInfo.value, AttributeModifier.Operation.valueOf(attributeInfo.operation.name), EquipmentSlot.valueOf(eSlot.name))
				if (!meta.addAttributeModifier(Attribute.valueOf(attributeInfo.attribute.name), modifier)) {
					AbstractionLogger.getLogger().error("VersionIndependentUtils", "Something went wrong when adding the attribute " + attributeInfo.attribute.key)
				}
			}
		}
	}

	override fun getTargetBlockExact(entity: LivingEntity, distance: Int, ignore: List<Material>): Block? {
		return (if (ignore.contains(Material.AIR) || ignore.contains(Material.WATER) || ignore.contains(Material.LAVA)) {
			entity.world.rayTraceBlocks(entity.eyeLocation, entity.eyeLocation.direction, distance.toDouble(), FluidCollisionMode.ALWAYS, true)
		} else {
			entity.world.rayTraceBlocks(entity.eyeLocation, entity.eyeLocation.direction, distance.toDouble(), FluidCollisionMode.NEVER, false)
		})?.hitBlock
	}

	override fun getReacheableBlockExact(entity: LivingEntity): Block? {
		val ignore: MutableList<Material> = ArrayList()
		ignore.add(Material.LAVA)
		ignore.add(Material.WATER)
		return getTargetBlockExact(entity, 5, ignore)
	}

	override fun spawnFallingBlock(location: Location, material: Material, data: Byte, consumer: Consumer<FallingBlock>?): FallingBlock {
		try {
			val fb = EntityFallingBlock.fall((location.world as CraftWorld?)!!.handle, BlockPosition(location.blockX, location.blockY, location.blockZ), CraftMagicNumbers.getBlock(material).n(), CreatureSpawnEvent.SpawnReason.CUSTOM)
			return if (fb.bukkitEntity is CraftFallingBlock) {
				val cfb = fb.bukkitEntity as CraftFallingBlock
				consumer?.accept(cfb)
				(location.world as CraftWorld?)!!.handle.addFreshEntity(fb, CreatureSpawnEvent.SpawnReason.CUSTOM)
				cfb
			} else {
				throw IllegalStateException("[VersionIndependentUtils] An unexpected error occurred")
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
		throw IllegalStateException("[VersionIndependentUtils] An unexpected error occurred")
	}

	override fun displayTotem(player: Player) {
		val packet = PacketPlayOutEntityStatus((player as CraftPlayer).handle, 35.toByte())
		player.handle.c.a(packet)
	}

	override fun displayCustomTotem(player: Player, cmd: Int) {
		val totem = ItemStack(Material.TOTEM_OF_UNDYING)
		val meta = totem.itemMeta!!
		meta.setCustomModelData(cmd)
		totem.setItemMeta(meta)
		val hand = player.inventory.itemInMainHand
		player.inventory.setItemInMainHand(totem)
		val packet = PacketPlayOutEntityStatus((player as CraftPlayer).handle, 35.toByte())
		player.handle.c.a(packet)
		player.getInventory().setItemInMainHand(hand)
	}

	override fun setCustomSpectator(player: Player, value: Boolean, players: Collection<Player>) {
		if (value) {
			if (!CustomSpectatorManager.isSpectator(player)) {
				player.allowFlight = true
				player.isFlying = true
				player.isCollidable = false
				player.isSilent = true
				player.health = 20.0
				player.foodLevel = 20
				player.equipment!!.clear()
				player.inventory.clear()
				player.activePotionEffects.clear()
				player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 0, false, false))
				CustomSpectatorManager.getSpectators().add(player)
				for (list in players) {
					list.hidePlayer(Bukkit.getPluginManager().getPlugin("NovaCore")!!, player)
				}
			}
		} else {
			if (CustomSpectatorManager.isSpectator(player)) {
				player.isFlying = false
				player.allowFlight = false
				player.removePotionEffect(PotionEffectType.INVISIBILITY)
				player.isCollidable = true
				player.isSilent = false
				CustomSpectatorManager.getSpectators().remove(player)
				for (list in players) {
					list.showPlayer(Bukkit.getPluginManager().getPlugin("NovaCore")!!, player)
				}
			}
		}
	}

	override fun getEntityBoundingBox(entity: org.bukkit.entity.Entity): EntityBoundingBox {
		val nmsEntity = (entity as CraftEntity).handle
		val aabb = nmsEntity.cG()
		val df = DecimalFormat("0.00")
		val currentWidth = aabb.d - entity.getLocation().x
		val currentHeight = aabb.e - entity.getLocation().y
		val width = df.format(currentWidth).replace(',', '.').toFloat() * 2
		val height = df.format(currentHeight).replace(',', '.').toFloat()
		return EntityBoundingBox(height, width)
	}

	override fun setSource(tnt: TNTPrimed, source: LivingEntity) {
		val etp = (tnt as CraftTNTPrimed).handle
		val el = (source as CraftLivingEntity).handle
		try {
			val f = etp.javaClass.getDeclaredField("d")
			f.isAccessible = true
			f[etp] = el
		} catch (e: Exception) {
			AbstractionLogger.getLogger().error("VersionIndependentUtils", "Could not set TNT's source. Entity UUID: " + tnt.getUniqueId() + " Entity ID: " + tnt.getEntityId())
			e.printStackTrace()
		}
	}

	override fun spawnCustomEntity(entity: Any, location: Location) {
		if (Entity::class.java.isAssignableFrom(entity.javaClass)) {
			val nmsEntity = entity as Entity
			nmsEntity.a(location.x, location.y, location.z, location.yaw, location.pitch)
			(location.world as CraftWorld?)!!.handle.addFreshEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM)
		} else {
			AbstractionLogger.getLogger().error("VersionIndependentUtils", "Object isn't instance of Entity.")
		}
	}

	override fun getBlockBlastResistance(material: Material): Float {
		return if (material.isBlock) {
			val block = CraftMagicNumbers.getBlock(material)
			try {
				val str = BlockBase::class.java.getDeclaredField("aH")
				str.isAccessible = true
				str.getFloat(block)
			} catch (e: Exception) {
				AbstractionLogger.getLogger().error("VersionIndependentUtils", "An error occurred")
				e.printStackTrace()
				0F
			}
		} else {
			AbstractionLogger.getLogger().warning("VersionIndependentUtils", "Material isn't a block.")
			0F
		}
	}

	override fun getGameProfile(player: Player): GameProfile {
		return (player as CraftPlayer).handle.fQ()
	}

	override fun preProcessHeadMetaApplication(meta: ItemMeta, profile: GameProfile, stack: ItemStack) {
		val serializedProfile = GameProfileSerializer.a(NBTTagCompound(), profile)
		try {
			val field = meta.javaClass.getDeclaredField("serializedProfile")
			field.isAccessible = true
			field[meta] = serializedProfile
		} catch (e: Exception) {
			Log.error("NovaCore 1.20.2", "Failed to patch skull meta. " + e.javaClass.name + " " + e.message)
			e.printStackTrace()
		}
	}

	companion object {
		val SIGN_MATERIALS = arrayOf(Material.ACACIA_SIGN, Material.ACACIA_WALL_SIGN, Material.BIRCH_SIGN, Material.BIRCH_WALL_SIGN, Material.CRIMSON_SIGN, Material.CRIMSON_WALL_SIGN, Material.DARK_OAK_SIGN, Material.DARK_OAK_WALL_SIGN, Material.JUNGLE_SIGN, Material.JUNGLE_WALL_SIGN, Material.OAK_SIGN, Material.OAK_WALL_SIGN, Material.SPRUCE_SIGN, Material.SPRUCE_WALL_SIGN, Material.WARPED_SIGN, Material.WARPED_WALL_SIGN)
	}
}