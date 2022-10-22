package net.zeeraa.novacore.spigot.version.v1_19_R1;

import java.util.Collections;
import java.util.UUID;

import net.zeeraa.novacore.spigot.abstraction.enums.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependentItems;
import net.zeeraa.novacore.spigot.abstraction.log.AbstractionLogger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.novauniverse.novacore1_17plus.shared.DyeColorToMaterialMapper_1_17;
import net.zeeraa.novacore.commons.utils.LoopableIterator;
import net.zeeraa.novacore.spigot.abstraction.ChunkLoader;
import net.zeeraa.novacore.spigot.abstraction.ItemBuilderRecordList;
import net.zeeraa.novacore.spigot.abstraction.LabyModProtocol;

public class VersionIndependentUtils extends net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils {
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

	public VersionIndependentUtils() {
		itemBuilderRecordList = new ItemBuilderRecordListv1_17();
	}

	@Override
	public ItemBuilderRecordList getItembBuilderRecordList() {
		return itemBuilderRecordList;
	}

	@Override
	public void setBlockAsPlayerSkull(Block block) {
		block.setType(Material.PLAYER_HEAD);

		block.getState().update(true);
	}

	@Override
	public ItemStack getItemInMainHand(Player player) {
		return player.getInventory().getItemInMainHand();
	}

	@Override
	public ItemStack getItemInOffHand(Player player) {
		return player.getInventory().getItemInOffHand();
	}

	@Override
	public double getEntityMaxHealth(LivingEntity livingEntity) {
		return livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
	}

	@Override
	public void setEntityMaxHealth(LivingEntity livingEntity, double health) {
		livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
	}

	@Override
	public void resetEntityMaxHealth(LivingEntity livingEntity) {
		livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public double[] getRecentTps() {
		// Deprecated but still the way spigot does it in the /tps command
		return MinecraftServer.getServer().recentTps;
	}

	@Override
	public int getPlayerPing(Player player) {
		return player.getPing();
	}

	@Override
	public void cloneBlockData(Block source, Block target) {
		target.setBlockData(source.getBlockData());
	}

	@Override
	public void setItemInMainHand(Player player, ItemStack item) {
		player.getInventory().setItemInMainHand(item);
	}

	@Override
	public void setItemInOffHand(Player player, ItemStack item) {
		player.getInventory().setItemInOffHand(item);
	}

	@Override
	public void sendTabList(Player player, String header, String footer) {
		player.setPlayerListHeaderFooter(header, footer);
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
	public void setColoredBlock(Block block, DyeColor color, ColoredBlockType type) {
		Material material = getColoredMaterial(color, type);

		block.setType(material);
	}

	@Override
	public ItemStack getColoredItem(DyeColor color, ColoredBlockType type) {
		return new ItemStack(getColoredMaterial(color, type));
	}

	@Override
	public void setShapedRecipeIngredientAsColoredBlock(ShapedRecipe recipe, char ingredient, ColoredBlockType type, DyeColor color) {
		recipe.setIngredient(ingredient, getColoredMaterial(color, type));
	}

	@Override
	public void addShapelessRecipeIngredientAsColoredBlock(ShapelessRecipe recipe, char ingredient, ColoredBlockType type, DyeColor color) {
		recipe.addIngredient(getColoredMaterial(color, type));
	}

	private Material getColoredMaterial(DyeColor color, ColoredBlockType type) {
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
	public void attachMapView(ItemStack item, MapView mapView) {
		MapMeta meta = (MapMeta) item.getItemMeta();

		meta.setMapView(mapView);

		item.setItemMeta(meta);
	}

	@Override
	public MapView getAttachedMapView(ItemStack item) {
		MapMeta meta = (MapMeta) item.getItemMeta();

		return meta.getMapView();
	}

	@Override
	public int getMapViewId(MapView mapView) {
		return mapView.getId();
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

		default:
			setLastError(VersionIndependenceLayerError.MISSING_SOUND);
			AbstractionLogger.getLogger().error("VersionIndependentUtils", "VersionIndependantSound " + sound.name() + " is not defined in this version. Please add it to " + this.getClass().getName());
			return null;
		}
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
	}

	@Override
	public ItemStack getPlayerSkullWithBase64Texture(String b64stringtexture) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		PropertyMap propertyMap = profile.getProperties();
		if (propertyMap == null) {
			throw new IllegalStateException("Profile doesn't contain a property map");
		}
		propertyMap.put("textures", new Property("textures", b64stringtexture));
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		ItemMeta headMeta = head.getItemMeta();
		Class<?> headMetaClass = headMeta.getClass();
		try {
			getField(headMetaClass, "profile", GameProfile.class, 0).set(headMeta, profile);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}

	@Override
	public VersionIndependentItems getVersionIndependantItems() {
		return new net.zeeraa.novacore.spigot.version.v1_19_R1.VersionIndependantItems();
	}

	@Override
	public void setShapedRecipeIngredientAsPlayerSkull(ShapedRecipe recipe, char ingredient) {
		recipe.setIngredient(ingredient, Material.PLAYER_HEAD);
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

		default:
			setLastError(VersionIndependenceLayerError.MISSING_MATERIAL);
			AbstractionLogger.getLogger().warning("VersionIndependentUtils", "Unknown version Independent material: " + material.name());
			return null;
		}
	}

	private LabyModProtocolImpl lmp = null;

	@Override
	public LabyModProtocol getLabyModProtocol() {
		if (lmp == null) {
			lmp = new LabyModProtocolImpl();
		}
		return lmp;
	}

	@Override
	public NovaCoreGameVersion getNovaCoreGameVersion() {
		return NovaCoreGameVersion.V_1_19_R1;
	}

	@Override
	public ItemStack getPlayerSkullitem() {
		return new ItemStack(Material.PLAYER_HEAD, 1);
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
	public void sendActionBarMessage(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
	}

	@Override
	public int getMinY() {
		return -64;
	}

	@Override
	public ItemMeta setUnbreakable(ItemMeta meta, boolean unbreakable) {
		meta.setUnbreakable(unbreakable);
		return meta;
	}

	@Override
	public void setCreatureItemInMainHand(Creature creature, ItemStack item) {
		creature.getEquipment().setItemInMainHand(item);
	}

	@Override
	public float getPlayerBodyRotation(Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		return craftPlayer.getHandle().aY;
	}

	@Override
	public void setCustomModelData(ItemMeta meta, int data) {
		meta.setCustomModelData(data);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setGameRule(World world, String rule, String value) {
		world.setGameRuleValue(rule, value);
	}

	@Override
	public boolean isInteractEventMainHand(PlayerInteractEvent e) {
		return e.getHand() == EquipmentSlot.HAND;
	}

	@Override
	public Entity getEntityByUUID(UUID uuid) {
		return Bukkit.getEntity(uuid);
	}

	@Override
	public void setShapedRecipeIngredientAsDye(ShapedRecipe recipe, char ingredient, DyeColor color) {
		recipe.setIngredient(ingredient, DyeColorToMaterialMapper_1_17.dyeColorToMaterial(color));
	}

	@Override
	public void addShapelessRecipeIngredientAsDye(ShapelessRecipe recipe, int count, DyeColor color) {
		recipe.addIngredient(count, DyeColorToMaterialMapper_1_17.dyeColorToMaterial(color));
	}

	@Override
	public void setAI(LivingEntity entity, boolean ai) {
		entity.setAI(ai);
	}

	@Override
	public void setSilent(LivingEntity entity, boolean silent) {
		entity.setSilent(silent);
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
				return DeathType.COMBAT_WITHER;
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
		case SONIC_BOOM:
			return DeathType.SONIC_BOOM;
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

	@Override
	public String colorize(Color color, String message) {
		String thing = "&x" + asChatColor(color.getRed() + "" + color.getGreen() + "" + color.getBlue()) + message;

		return org.bukkit.ChatColor.translateAlternateColorCodes('&', thing);
	}

	@Override
	public String colorizeGradient(Color[] colors, String message) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < message.length(); i++) {
			builder.append(asChatColor(colors[i].getRed() + "" + colors[i].getGreen() + "" + colors[i].getBlue())).append(message.toCharArray()[i]);
		}
		return builder.toString();
	}

	@Override
	public String colorizeRainbow(Color[] colors, int charsPerColor, String message) {
		LoopableIterator<Color> iterator = new LoopableIterator<>();
		Collections.addAll(iterator, colors);
		StringBuilder finalBuild = new StringBuilder();
		for (int i = 0; i < message.length(); i++) {

			if (i % charsPerColor == 0) {
				finalBuild.append(colorize(iterator.next(), message.toCharArray()[i] + ""));
			} else {
				finalBuild.append(message.toCharArray()[i]);
			}

		}
		return finalBuild.toString();
	}

	@Override
	public String asChatColor(String rgb) {
		return net.md_5.bungee.api.ChatColor.of(rgb).toString();
	}

	@Override
	public boolean canBreakBlock(ItemStack item, Material material) {
		// TODO: Implement
		return false;
	}
}