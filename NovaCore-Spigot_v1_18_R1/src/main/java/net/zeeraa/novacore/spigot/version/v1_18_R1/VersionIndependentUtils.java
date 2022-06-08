package net.zeeraa.novacore.spigot.version.v1_18_R1;

import java.util.UUID;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.NovaCoreGameVersion;
import net.zeeraa.novacore.spigot.abstraction.enums.PlayerDamageReason;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMetarial;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.abstraction.log.AbstractionLogger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
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
			AbstractionLogger.getLogger().warning("Nova VersionIndependentUtils v1_17_R1", "damagePlayer will not work on 1.17 and will instead call Player#damage(double)");
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

		default:
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
		return new net.zeeraa.novacore.spigot.version.v1_18_R1.VersionIndependantItems();
	}

	@Override
	public void setShapedRecipeIngredientAsPlayerSkull(ShapedRecipe recipe, char ingredient) {
		recipe.setIngredient(ingredient, Material.PLAYER_HEAD);
	}

	@Override
	public Material getMaterial(VersionIndependentMetarial material) {
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
			
		default:
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
		return NovaCoreGameVersion.V_1_18;
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
		return 0;
	}
}