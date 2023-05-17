package net.zeeraa.novacore.spigot.version.v1_19_R3;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftFallingBlock;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import com.mojang.authlib.GameProfile;

import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.phys.AxisAlignedBB;
import net.novauniverse.novacore1_17plus.shared.BaseVersionIndependentUtilImplementation1_17Plus;
import net.novauniverse.novacore1_17plus.shared.DyeColorToMaterialMapper_1_17;
import net.zeeraa.novacore.commons.utils.ListUtils;
import net.zeeraa.novacore.spigot.abstraction.ChunkLoader;
import net.zeeraa.novacore.spigot.abstraction.ItemBuilderRecordList;
import net.zeeraa.novacore.spigot.abstraction.MaterialNameList;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentItems;
import net.zeeraa.novacore.spigot.abstraction.commons.AttributeInfo;
import net.zeeraa.novacore.spigot.abstraction.commons.EntityBoundingBox;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.DeathType;
import net.zeeraa.novacore.spigot.abstraction.enums.NovaCoreGameVersion;
import net.zeeraa.novacore.spigot.abstraction.enums.PlayerDamageReason;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependenceLayerError;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.abstraction.log.AbstractionLogger;
import net.zeeraa.novacore.spigot.abstraction.manager.CustomSpectatorManager;

public class VersionIndependentUtilsImplementation extends BaseVersionIndependentUtilImplementation1_17Plus {
	private ItemBuilderRecordList itemBuilderRecordList;
	private boolean damagePlayerWarningShown = false;

	private ChunkLoader chunkLoader;

	@Override
	public ChunkLoader getChunkLoader() {
		if (chunkLoader == null) {
			chunkLoader = new ChunkLoaderImplementation();
		}
		return chunkLoader;
	}

	public VersionIndependentUtilsImplementation() {
		super(new DyeColorToMaterialMapper_1_17());
		itemBuilderRecordList = new ItemBuilderRecordListv1_17();
	}

	@Override
	public ItemBuilderRecordList getItemBuilderRecordList() {
		return itemBuilderRecordList;
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public double[] getRecentTps() {
		// Deprecated but still the way spigot does it in the /tps command
		return MinecraftServer.getServer().recentTps;
	}

	@Override
	public void damagePlayer(Player player, PlayerDamageReason reason, float damage) {
		// TODO: implement this

		if (!damagePlayerWarningShown) {
			damagePlayerWarningShown = true;
			AbstractionLogger.getLogger().warning("Nova VersionIndependentUtils v1_19_R1", "damagePlayer will not work on 1.17 and will instead call Player#damage(double)");
		}

		player.damage(damage);

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

	@Override
	public Material getColoredMaterial(DyeColor color, ColoredBlockType type) {
		// For some reason this returned air every time so i decide to hard code it
		// instead since i did not have a lot of time to fix the issue
		if (color == DyeColor.WHITE && type == ColoredBlockType.GLASS_PANE) {
			return Material.WHITE_STAINED_GLASS_PANE;
		}

		Material material;

		if (type == ColoredBlockType.GLASS_BLOCK) {
			switch (color) {
			case BLACK:
				material = Material.BLACK_STAINED_GLASS;
				break;

			case BLUE:
				material = Material.BLUE_STAINED_GLASS;
				break;

			case BROWN:
				material = Material.BROWN_STAINED_GLASS;
				break;

			case CYAN:
				material = Material.CYAN_STAINED_GLASS;
				break;

			case GRAY:
				material = Material.LIGHT_GRAY_STAINED_GLASS;
				break;

			case GREEN:
				material = Material.GREEN_STAINED_GLASS;
				break;

			case LIGHT_BLUE:
				material = Material.LIGHT_BLUE_STAINED_GLASS;
				break;

			case LIGHT_GRAY:
				material = Material.LIGHT_GRAY_STAINED_GLASS;
				break;

			case LIME:
				material = Material.LIME_STAINED_GLASS;
				break;

			case MAGENTA:
				material = Material.MAGENTA_STAINED_GLASS;
				break;

			case ORANGE:
				material = Material.ORANGE_STAINED_GLASS;
				break;

			case PINK:
				material = Material.PINK_STAINED_GLASS;
				break;

			case PURPLE:
				material = Material.PURPLE_STAINED_GLASS;
				break;

			case RED:
				material = Material.RED_STAINED_GLASS;
				break;

			case YELLOW:
				material = Material.YELLOW_STAINED_GLASS;
				break;

			case WHITE:
				material = Material.WHITE_STAINED_GLASS;
				break;

			default:
				material = Material.AIR;
				break;
			}
		} else if (type == ColoredBlockType.GLASS_PANE) {
			switch (color) {
			case BLACK:
				material = Material.BLACK_STAINED_GLASS_PANE;
				break;

			case BLUE:
				material = Material.BLUE_STAINED_GLASS_PANE;
				break;

			case BROWN:
				material = Material.BROWN_STAINED_GLASS_PANE;
				break;

			case CYAN:
				material = Material.CYAN_STAINED_GLASS_PANE;
				break;

			case GRAY:
				material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
				break;

			case GREEN:
				material = Material.GREEN_STAINED_GLASS_PANE;
				break;

			case LIGHT_BLUE:
				material = Material.LIGHT_BLUE_STAINED_GLASS_PANE;
				break;

			case LIGHT_GRAY:
				material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
				break;

			case LIME:
				material = Material.LIME_STAINED_GLASS_PANE;
				break;

			case MAGENTA:
				material = Material.MAGENTA_STAINED_GLASS_PANE;
				break;

			case ORANGE:
				material = Material.ORANGE_STAINED_GLASS_PANE;
				break;

			case PINK:
				material = Material.PINK_STAINED_GLASS_PANE;
				break;

			case PURPLE:
				material = Material.PURPLE_STAINED_GLASS_PANE;
				break;

			case RED:
				material = Material.RED_STAINED_GLASS_PANE;
				break;

			case YELLOW:
				material = Material.YELLOW_STAINED_GLASS_PANE;
				break;

			case WHITE:
				material = Material.WHITE_STAINED_GLASS_PANE;

			default:
				material = Material.AIR;
				break;
			}
		} else if (type == ColoredBlockType.WOOL) {
			switch (color) {
			case BLACK:
				material = Material.BLACK_WOOL;
				break;

			case BLUE:
				material = Material.BLUE_WOOL;
				break;

			case BROWN:
				material = Material.BROWN_WOOL;
				break;

			case CYAN:
				material = Material.CYAN_WOOL;
				break;

			case GRAY:
				material = Material.LIGHT_GRAY_WOOL;
				break;

			case GREEN:
				material = Material.GREEN_WOOL;
				break;

			case LIGHT_BLUE:
				material = Material.LIGHT_BLUE_WOOL;
				break;

			case LIGHT_GRAY:
				material = Material.LIGHT_GRAY_WOOL;
				break;

			case LIME:
				material = Material.LIME_WOOL;
				break;

			case MAGENTA:
				material = Material.MAGENTA_WOOL;
				break;

			case ORANGE:
				material = Material.ORANGE_WOOL;
				break;

			case PINK:
				material = Material.PINK_WOOL;
				break;

			case PURPLE:
				material = Material.PURPLE_WOOL;
				break;

			case RED:
				material = Material.RED_WOOL;
				break;

			case YELLOW:
				material = Material.YELLOW_WOOL;
				break;

			case WHITE:
				material = Material.WHITE_WOOL;
				break;

			default:
				material = Material.AIR;
				break;
			}
		} else if (type == ColoredBlockType.CLAY) {
			switch (color) {
			case BLACK:
				material = Material.BLACK_TERRACOTTA;
				break;

			case BLUE:
				material = Material.BLUE_TERRACOTTA;
				break;

			case BROWN:
				material = Material.BROWN_TERRACOTTA;
				break;

			case CYAN:
				material = Material.CYAN_TERRACOTTA;
				break;

			case GRAY:
				material = Material.LIGHT_GRAY_TERRACOTTA;
				break;

			case GREEN:
				material = Material.GREEN_TERRACOTTA;
				break;

			case LIGHT_BLUE:
				material = Material.LIGHT_BLUE_TERRACOTTA;
				break;

			case LIGHT_GRAY:
				material = Material.LIGHT_GRAY_TERRACOTTA;
				break;

			case LIME:
				material = Material.LIME_TERRACOTTA;
				break;

			case MAGENTA:
				material = Material.MAGENTA_TERRACOTTA;
				break;

			case ORANGE:
				material = Material.ORANGE_TERRACOTTA;
				break;

			case PINK:
				material = Material.PINK_TERRACOTTA;
				break;

			case PURPLE:
				material = Material.PURPLE_TERRACOTTA;
				break;

			case RED:
				material = Material.RED_TERRACOTTA;
				break;

			case YELLOW:
				material = Material.YELLOW_TERRACOTTA;
				break;

			case WHITE:
				material = Material.WHITE_TERRACOTTA;
				break;

			default:
				material = Material.AIR;
				break;
			}
		} else {
			material = Material.AIR;
		}

		return material;
	}

	@Override
	public Sound getSound(VersionIndependentSound sound) {
		switch (sound) {
		case NOTE_PLING:
			return Sound.BLOCK_NOTE_BLOCK_PLING;

		case NOTE_HAT:
			return Sound.BLOCK_NOTE_BLOCK_HAT;

		case WITHER_DEATH:
			return Sound.ENTITY_WITHER_DEATH;

		case WITHER_HURT:
			return Sound.ENTITY_WITHER_HURT;

		case ITEM_BREAK:
			return Sound.ENTITY_ITEM_BREAK;

		case ITEM_PICKUP:
			return Sound.ENTITY_ITEM_PICKUP;

		case ORB_PICKUP:
			return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

		case ANVIL_LAND:
			return Sound.BLOCK_ANVIL_LAND;

		case EXPLODE:
			return Sound.ENTITY_GENERIC_EXPLODE;

		case LEVEL_UP:
			return Sound.ENTITY_PLAYER_LEVELUP;

		case WITHER_SHOOT:
			return Sound.ENTITY_WITHER_SHOOT;

		case EAT:
			return Sound.ENTITY_GENERIC_EAT;

		case ANVIL_BREAK:
			return Sound.BLOCK_ANVIL_BREAK;

		case FIZZ:
			return Sound.BLOCK_FIRE_EXTINGUISH;

		case ENDERMAN_TELEPORT:
			return Sound.ENTITY_ENDERMAN_TELEPORT;

		case CLICK:
			return Sound.BLOCK_LEVER_CLICK;

		case BLAZE_HIT:
			return Sound.ENTITY_BLAZE_HURT;
			
		case BURP:
			return Sound.ENTITY_PLAYER_BURP;

		default:
			setLastError(VersionIndependenceLayerError.MISSING_SOUND);
			AbstractionLogger.getLogger().error("VersionIndependentUtils", "VersionIndependantSound " + sound.name() + " is not defined in this version. Please add it to " + this.getClass().getName());
			return null;
		}
	}

	@Override
	public VersionIndependentItems getVersionIndependantItems() {
		return new net.zeeraa.novacore.spigot.version.v1_19_R3.VersionIndependantItemsImplementation();
	}

	@Override
	public Material getMaterial(VersionIndependentMaterial material) {
		switch (material) {
		case FILLED_MAP:
			return Material.FILLED_MAP;

		case END_STONE:
			return Material.END_STONE;

		case WORKBENCH:
			return Material.CRAFTING_TABLE;

		case OAK_BOAT:
			return Material.OAK_BOAT;

		case DIAMOND_SHOVEL:
			return Material.DIAMOND_SHOVEL;

		case SNOWBALL:
			return Material.SNOWBALL;

		case FARMLAND:
			return Material.FARMLAND;

		case GOLDEN_AXE:
			return Material.GOLDEN_AXE;

		case GOLDEN_HOE:
			return Material.GOLDEN_HOE;

		case GOLDEN_PICKAXE:
			return Material.GOLDEN_PICKAXE;

		case GOLDEN_SHOVEL:
			return Material.GOLDEN_SHOVEL;

		case GOLDEN_SWORD:
			return Material.GOLDEN_SWORD;

		case WOODEN_AXE:
			return Material.WOODEN_AXE;

		case WOODEN_HOE:
			return Material.WOODEN_HOE;

		case WOODEN_PICKAXE:
			return Material.WOODEN_PICKAXE;

		case WOODEN_SHOVEL:
			return Material.WOODEN_SHOVEL;

		case WOODEN_SWORD:
			return Material.WOODEN_SWORD;

		case WATCH:
			return Material.CLOCK;

		case GOLD_HELMET:
			return Material.GOLDEN_HELMET;

		case GOLD_CHESTPLATE:
			return Material.GOLDEN_CHESTPLATE;

		case GOLD_LEGGINGS:
			return Material.GOLDEN_LEGGINGS;

		case GOLD_BOOTS:
			return Material.GOLDEN_BOOTS;

		case GRILLED_PORK:
			return Material.COOKED_PORKCHOP;

		case EXP_BOTTLE:
			return Material.EXPERIENCE_BOTTLE;

		case WOOL:
			return Material.WHITE_WOOL;

		case FIREBALL:
			return Material.FIRE_CHARGE;

		case GUNPOWDER:
			return Material.GUNPOWDER;

		default:
			setLastError(VersionIndependenceLayerError.MISSING_MATERIAL);
			AbstractionLogger.getLogger().warning("VersionIndependentUtils", "Unknown version Independent material: " + material.name());
			return null;
		}
	}

	@Override
	public NovaCoreGameVersion getNovaCoreGameVersion() {
		return NovaCoreGameVersion.V_1_19_R3;
	}

	public static final Material[] SIGN_MATERIALS = { Material.ACACIA_SIGN, Material.ACACIA_WALL_SIGN, Material.BIRCH_SIGN, Material.BIRCH_WALL_SIGN, Material.CRIMSON_SIGN, Material.CRIMSON_WALL_SIGN, Material.DARK_OAK_SIGN, Material.DARK_OAK_WALL_SIGN, Material.JUNGLE_SIGN, Material.JUNGLE_WALL_SIGN, Material.OAK_SIGN, Material.OAK_WALL_SIGN, Material.SPRUCE_SIGN, Material.SPRUCE_WALL_SIGN, Material.WARPED_SIGN, Material.WARPED_WALL_SIGN };

	@Override
	public boolean isSign(Material material) {
		for (Material m2 : SIGN_MATERIALS) {
			if (material == m2) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int getMinY() {
		return -64;
	}

	@Override
	public float getPlayerBodyRotation(Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		return craftPlayer.getHandle().aU;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setGameRule(World world, String rule, String value) {
		world.setGameRuleValue(rule, value);
	}

	@SuppressWarnings("deprecation")
	@Override
	public DeathType getDeathTypeFromDamage(EntityDamageEvent e, Entity lastDamager) {
		switch (e.getCause()) {
		case FIRE:
			if (lastDamager != null)
				return DeathType.FIRE_SOURCE_COMBAT;
			return DeathType.FIRE_SOURCE;
		case LAVA:
			if (lastDamager != null)
				return DeathType.LAVA_COMBAT;
			return DeathType.LAVA;
		case FALL:
			if (e.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE) <= 2.0) {
				if (lastDamager != null)
					return DeathType.FALL_SMALL_COMBAT;
				return DeathType.FALL_SMALL;
			} else {
				return DeathType.FALL_BIG;
			}
		case VOID:
			if (lastDamager != null)
				return DeathType.VOID_COMBAT;
			return DeathType.VOID;

		case THORNS:
			return DeathType.THORNS;
		case WITHER:
			if (lastDamager != null)
				return DeathType.EFFECT_WITHER_COMBAT;
			return DeathType.EFFECT_WITHER;

		case CONTACT:
			if (e instanceof EntityDamageByBlockEvent) {
				EntityDamageByBlockEvent blockEvent = (EntityDamageByBlockEvent) e;
				if (lastDamager != null) {
					if (blockEvent.getDamager().getType() == Material.SWEET_BERRY_BUSH)
						return DeathType.BUSH_COMBAT;
					else if (blockEvent.getDamager().getType() == Material.CACTUS)
						return DeathType.CACTUS_COMBAT;
					else if (blockEvent.getDamager().getType() == Material.POINTED_DRIPSTONE)
						return DeathType.FALL_STALAGMITE_COMBAT;
				} else {
					if (blockEvent.getDamager().getType() == Material.SWEET_BERRY_BUSH)
						return DeathType.BUSH;
					else if (blockEvent.getDamager().getType() == Material.CACTUS)
						return DeathType.CACTUS;
					else if (blockEvent.getDamager().getType() == Material.POINTED_DRIPSTONE)
						return DeathType.FALL_STALAGMITE;
				}
			}
		case DROWNING:
			if (lastDamager != null)
				return DeathType.DROWN_COMBAT;
			return DeathType.DROWN;

		case LIGHTNING:
			if (lastDamager != null)
				return DeathType.LIGHTNING_COMBAT;
			return DeathType.LIGHTNING;

		case PROJECTILE:
			if (lastDamager.getType() == EntityType.ARROW) {
				return DeathType.PROJECTILE_ARROW;
			}
			return DeathType.PROJECTILE_OTHER;
		case STARVATION:
			if (lastDamager != null)
				return DeathType.STARVING_COMBAT;
			return DeathType.STARVING;

		case SUFFOCATION:
			if (lastDamager != null)
				return DeathType.SUFFOCATION_COMBAT;
			return DeathType.SUFFOCATION;
		case ENTITY_ATTACK:
		case ENTITY_SWEEP_ATTACK:
			switch (lastDamager.getType()) {
			case WITHER:
				return DeathType.COMBAT_WITHER_SKULL;
			case FIREBALL:
			case SMALL_FIREBALL:
				return DeathType.COMBAT_FIREBALL;
			case BEE:
				return DeathType.COMBAT_BEE;
			default:
				return DeathType.COMBAT_NORMAL;
			}
		case FALLING_BLOCK:
			if (e instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) e;
				if (entityEvent.getDamager() instanceof FallingBlock) {
					FallingBlock block = (FallingBlock) entityEvent.getDamager();
					switch (block.getBlockData().getMaterial()) {
					case ANVIL:
						if (lastDamager != null)
							return DeathType.ANVIL_FALL_COMBAT;
						return DeathType.ANVIL_FALL;
					case POINTED_DRIPSTONE:
						if (lastDamager != null)
							return DeathType.STALAGTITE_FALL_COMBAT;
						return DeathType.STALAGTITE_FALL;
					default:
						if (lastDamager != null)
							return DeathType.BLOCK_FALL_COMBAT;
						return DeathType.BLOCK_FALL;
					}
				}
			}
		case BLOCK_EXPLOSION:
		case ENTITY_EXPLOSION:
			if (lastDamager != null)
				return DeathType.EXPLOSION_COMBAT;
			return DeathType.EXPLOSION;

		case FIRE_TICK:
			if (lastDamager != null)
				return DeathType.FIRE_NATURAL_COMBAT;
			return DeathType.FIRE_NATURAL;

		case MAGIC:
			DeathType type = DeathType.MAGIC;
			if (lastDamager != null) {
				if (e instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) e;
					if (entityEvent.getDamager() instanceof ThrownPotion) {
						ThrownPotion potion = (ThrownPotion) entityEvent.getDamager();
						if (potion.getShooter() instanceof Entity) {
							if (((Entity) potion.getShooter()).getUniqueId().toString().equalsIgnoreCase(lastDamager.getUniqueId().toString())) {
								type = DeathType.MAGIC_COMBAT;
							} else {
								type = DeathType.MAGIC_COMBAT_ACCIDENT;
							}
						}
					}
				}
			}
			return type;
		case CRAMMING:
			if (lastDamager != null)
				return DeathType.SUFFOCATION_CRAMMING_COMBAT;
			return DeathType.SUFFOCATION_COMBAT;

		case HOT_FLOOR:
			if (lastDamager != null)
				return DeathType.MAGMA_BLOCK_COMBAT;
			return DeathType.MAGMA_BLOCK;

		case DRAGON_BREATH:
			if (lastDamager != null)
				return DeathType.DRAGON_BREATH_COMBAT;
			return DeathType.DRAGON_BREATH;
		case FLY_INTO_WALL:
			if (lastDamager != null)
				return DeathType.ELYTRA_WALL_COMBAT;
			return DeathType.ELYTRA_WALL;
		case FREEZE:
			if (lastDamager != null)
				return DeathType.FROZEN_COMBAT;
			return DeathType.FROZEN;
		case SUICIDE:
		case DRYOUT:
		case CUSTOM:
		case MELTING:
		case POISON:
		default:
			if (lastDamager != null)
				return DeathType.GENERIC_COMBAT;
			return DeathType.GENERIC;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canBreakBlock(ItemStack itemStack, Material material) {
		net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound nbtTag = nmsItem.v();
		NBTTagList list = nbtTag.c("CanDestroy", 8);
		if (list == null) {
			return false;
		}
		try {
			Field f = NBTTagList.class.getDeclaredField("c");
			f.setAccessible(true);

			for (NBTTagString nbt : (List<NBTTagString>) f.get(list)) {
				boolean b = getMaterialFromName(nbt.f_()) == material;

				if (b) {
					return true;
				}
			}
		} catch (Exception e1) {
			return false;
		}

		return false;

	}

	@Override
	public MaterialNameList getMaterialNameList() {
		// I believe 1.16+ has all names mirror their Material type, if not tell me
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Material getMaterialFromName(String s) {
		try {
			int value = Integer.parseInt(s);
			for (Material material : Material.values()) {
				if (value == material.getId()) {
					return material;
				}
			}
			return null;
		} catch (Exception ignored) {
		}

		return Material.matchMaterial(s.replace("minecraft:", "").toLowerCase(Locale.ROOT));

	}

	@Override
	public void sendPacket(Player player, Object packet) {
		if (packet instanceof Packet) {
			((CraftPlayer) player).getHandle().b.a((Packet<?>) packet);
		} else {
			AbstractionLogger.getLogger().warning("NovaCore", "Packet sent isnt instance of " + Packet.class.getCanonicalName());
		}
	}

	@Override
	public void addAttribute(ItemStack item, ItemMeta meta, AttributeInfo attributeInfo) {
		if (attributeInfo == null) {
			AbstractionLogger.getLogger().error("VersionIndependentUtils", "AttributeInfo is null");
			return;
		}

		if (attributeInfo.getAttribute() == null) {
			AbstractionLogger.getLogger().error("VersionIndependentUtils", "Attribute is null");
			return;
		}

		List<net.zeeraa.novacore.spigot.abstraction.enums.EquipmentSlot> newList = ListUtils.removeDuplicates(attributeInfo.getEquipmentSlots());

		if (newList.contains(net.zeeraa.novacore.spigot.abstraction.enums.EquipmentSlot.ALL)) {
			AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attributeInfo.getAttribute().getKey(),
					attributeInfo.getValue(), AttributeModifier.Operation.valueOf(attributeInfo.getOperation().name()));

			if (!meta.addAttributeModifier(Attribute.valueOf(attributeInfo.getAttribute().name()), modifier)) {
				AbstractionLogger.getLogger().error("VersionIndependentUtils", "Something went wrong when adding the attribute " + attributeInfo.getAttribute().getKey());
			}

		} else {
			for (net.zeeraa.novacore.spigot.abstraction.enums.EquipmentSlot eSlot : newList) {
				AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attributeInfo.getAttribute().getKey(),
						attributeInfo.getValue(), AttributeModifier.Operation.valueOf(attributeInfo.getOperation().name()), EquipmentSlot.valueOf(eSlot.name()));
				if (!meta.addAttributeModifier(Attribute.valueOf(attributeInfo.getAttribute().name()), modifier)) {
					AbstractionLogger.getLogger().error("VersionIndependentUtils", "Something went wrong when adding the attribute " + attributeInfo.getAttribute().getKey());
				}
			}
		}
	}

	@Override
	public Block getTargetBlockExact(LivingEntity entity, int distance, List<Material> ignore) {
		RayTraceResult returned;
		if (ignore.contains(Material.AIR) || ignore.contains(Material.WATER) || ignore.contains(Material.LAVA)) {
			returned = entity.getWorld().rayTraceBlocks(entity.getEyeLocation(), entity.getEyeLocation().getDirection(), distance, FluidCollisionMode.ALWAYS, true);
		} else {
			returned = entity.getWorld().rayTraceBlocks(entity.getEyeLocation(), entity.getEyeLocation().getDirection(), distance, FluidCollisionMode.NEVER, false);
		}
		if (returned == null) {
			return null;
		} else {
			return returned.getHitBlock();
		}
	}

	@Override
	public Block getReacheableBlockExact(LivingEntity entity) {
		List<Material> ignore = new ArrayList<>();
		ignore.add(Material.LAVA);
		ignore.add(Material.WATER);
		return getTargetBlockExact(entity, 5, ignore);
	}

	@Override
	public FallingBlock spawnFallingBlock(Location location, Material material, byte data, Consumer<FallingBlock> consumer) {
		try {
			EntityFallingBlock fb = EntityFallingBlock.fall(((CraftWorld) location.getWorld()).getHandle(), new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), CraftMagicNumbers.getBlock(material).o(), CreatureSpawnEvent.SpawnReason.CUSTOM);
			if (fb.getBukkitEntity() instanceof CraftFallingBlock) {
				CraftFallingBlock cfb = (CraftFallingBlock) fb.getBukkitEntity();
				consumer.accept(cfb);
				((CraftWorld) location.getWorld()).getHandle().addFreshEntity(fb, CreatureSpawnEvent.SpawnReason.CUSTOM);
				return cfb;
			} else {
				throw new IllegalStateException("[VersionIndependentUtils] An unexpected error occurred");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new IllegalStateException("[VersionIndependentUtils] An unexpected error occurred");
	}

	@Override
	public void displayTotem(Player player) {
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(((CraftPlayer) player).getHandle(), (byte) 35);
		((CraftPlayer) player).getHandle().b.a(packet);
	}

	@Override
	public void displayCustomTotem(Player player, int cmd) {
		ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
		ItemMeta meta = totem.getItemMeta();
		assert meta != null;
		meta.setCustomModelData(cmd);
		totem.setItemMeta(meta);
		ItemStack hand = player.getInventory().getItemInMainHand();
		player.getInventory().setItemInMainHand(totem);
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(((CraftPlayer) player).getHandle(), (byte) 35);
		((CraftPlayer) player).getHandle().b.a(packet);
		player.getInventory().setItemInMainHand(hand);
	}

	@Override
	public void setCustomSpectator(Player player, boolean value, Collection<? extends Player> players) {
		if (value) {
			if (!CustomSpectatorManager.isSpectator(player)) {
				player.setAllowFlight(true);
				player.setFlying(true);
				player.setCollidable(false);
				player.setSilent(true);
				player.setHealth(20);
				player.setFoodLevel(20);
				player.getEquipment().clear();
				player.getInventory().clear();
				player.getActivePotionEffects().clear();
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
				CustomSpectatorManager.getSpectators().add(player);
				for (Player list : players) {
					list.hidePlayer(Bukkit.getPluginManager().getPlugin("NovaCore"), player);
				}
			}
		} else {
			if (CustomSpectatorManager.isSpectator(player)) {
				player.setFlying(false);
				player.setAllowFlight(false);
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
				player.setCollidable(true);
				player.setSilent(false);
				CustomSpectatorManager.getSpectators().remove(player);
				for (Player list : players) {
					list.showPlayer(Bukkit.getPluginManager().getPlugin("NovaCore"), player);
				}
			}
		}
	}

	@Override
	public EntityBoundingBox getEntityBoundingBox(Entity entity) {
		net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		AxisAlignedBB aabb = nmsEntity.cD();

		DecimalFormat df = new DecimalFormat("0.00");
		double currentWidth = aabb.d - entity.getLocation().getX();
		double currentHeight = aabb.e - entity.getLocation().getY();
		float width = Float.parseFloat(df.format(currentWidth).replace(',', '.')) * 2;
		float height = Float.parseFloat(df.format(currentHeight).replace(',', '.'));
		return new EntityBoundingBox(height, width);
	}

	@Override
	public void setSource(TNTPrimed tnt, LivingEntity source) {
		EntityTNTPrimed etp = ((CraftTNTPrimed) tnt).getHandle();
		EntityLiving el = ((CraftLivingEntity) source).getHandle();
		try {
			Field f = etp.getClass().getDeclaredField("d");
			f.setAccessible(true);
			f.set(etp, el);
		} catch (Exception e) {
			AbstractionLogger.getLogger().error("VersionIndependentUtils", "Could not set TNT's source. Entity UUID: " + tnt.getUniqueId() + " Entity ID: " + tnt.getEntityId());
			e.printStackTrace();
		}
	}

	@Override
	public void spawnCustomEntity(Object entity, Location location) {
		if (net.minecraft.world.entity.Entity.class.isAssignableFrom(entity.getClass())) {
			net.minecraft.world.entity.Entity nmsEntity = (net.minecraft.world.entity.Entity) entity;
			nmsEntity.a(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			((CraftWorld) location.getWorld()).getHandle().addFreshEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
		} else {
			AbstractionLogger.getLogger().error("VersionIndependentUtils", "Object isnt instance of Entity.");
		}
	}

	@Override
	public float getBlockBlastResistance(Material material) {
		if (material.isBlock()) {
			net.minecraft.world.level.block.Block block = CraftMagicNumbers.getBlock(material);
			try {
				Field str = BlockBase.class.getDeclaredField("aH");
				str.setAccessible(true);
				return str.getFloat(block);
			} catch (Exception e) {
				AbstractionLogger.getLogger().error("VersionIndependentUtils", "An error occured");
				e.printStackTrace();
				return 0;
			}
		} else {
			AbstractionLogger.getLogger().warning("VersionIndependentUtils", "Material isnt a block.");
			return 0;
		}
	}

	@Override
	public GameProfile getGameProfile(Player player) {
		return ((CraftPlayer) player).getHandle().fI();
	}
}