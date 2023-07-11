package collection.collector;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CollectorCommand implements CommandExecutor, Listener {
    List<String> fishNames = Arrays.asList(
            "collection_arapaima",
            "collection_anglerfish",
            "collection_armored_catfish",
            "collection_bayad",
            "collection_blackfish",
            "collection_bluegill",
            "collection_tilapia",
            "collection_brown_trout",
            "collection_capitaine",
            "collection_carp",
            "collection_catfish",
            "collection_electric_eel",
            "collection_gar",
            "collection_halibut",
            "collection_herring",
            "collection_minnow",
            "collection_muskellunge",
            "collection_perch",
            "collection_pink_salmon",
            "collection_pollock",
            "collection_rainbow_trout",
            "collection_red_bellied_piranha",
            "collection_red_grouper",
            "collection_smallmouth_bass",
            "collection_synodontis",
            "collection_tambaqui",
            "collection_tuna"
    );
    private JavaPlugin plugin;
    public CollectorCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("물고기도감")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                createFishCollectionInventory(player);
                return true;
            }
        }
        return false;
    }
    public void createFishCollectionInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "물고기 도감");
        for (int i=0;i<27;i++){
            setItem(inventory, i, collectoritem(fishNames.get(i)));
        }
        File playerFile = new File(plugin.getDataFolder(), "data" + File.separator + player.getUniqueId() + ".yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
        List<String> caughtFishList = playerData.getStringList("잡은물고기");
        if (!caughtFishList.isEmpty()) {
            for (String fishData : caughtFishList) {
                String[] fishInfo = fishData.split(":");
                String fishKey = fishInfo[0];
                int customModelData = Integer.parseInt(fishInfo[1]);
                if (customModelData==70){
                    setItem(inventory, 6, collectoritem(collectionItem(fishKey, customModelData)));
                }else if (customModelData==270){
                    setItem(inventory, 26, collectoritem(collectionItem(fishKey, customModelData)));
                }else {
                    setItem(inventory, customModelData - 1, collectoritem(collectionItem(fishKey, customModelData)));
                }
            }
        }
        player.openInventory(inventory);
    }
    public static ItemStack collectoritem(String itemId) {
        // itemsadder에서 생성한 아이템을 가져오는 코드
        ItemStack fishItem = ItemsAdder.getCustomItem(itemId);
        ItemMeta meta = fishItem.getItemMeta();
        fishItem.setItemMeta(meta);
        // 아이템 메타 수정 등 추가적인 작업을 수행할 수 있습니다.

        return fishItem;
    }
    private void setItem(Inventory inventory, int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }
    public String collectionItem(String name, int customModelData) {
        if (name.equals("ROTTEN_FLESH")) {
            if (customModelData == 1) {
                return "arapaima";
            } else if (customModelData == 2) {
                return "anglerfish";
            } else if (customModelData == 3) {
                return "armored_catfish";
            } else if (customModelData == 4) {
                return "bayad";
            } else if (customModelData == 5) {
                return "blackfish";
            } else if (customModelData == 6) {
                return "bluegill";
            } else if (customModelData == 70) {
                return "tilapia";
            } else if (customModelData == 8) {
                return "brown_trout";
            } else if (customModelData == 9) {
                return "capitaine";
            } else if (customModelData == 10) {
                return "carp";
            } else if (customModelData == 11) {
                return "catfish";
            } else if (customModelData == 12) {
                return "electric_eel";
            } else if (customModelData == 13) {
                return "gar";
            } else if (customModelData == 14) {
                return "halibut";
            } else if (customModelData == 15) {
                return "herring";
            } else if (customModelData == 16) {
                return "minnow";
            } else if (customModelData == 17) {
                return "muskellunge";
            } else if (customModelData == 18) {
                return "perch";
            } else if (customModelData == 19) {
                return "pink_salmon";
            } else if (customModelData == 20) {
                return "pollock";
            } else if (customModelData == 21) {
                return "rainbow_trout";
            } else if (customModelData == 22) {
                return "red_bellied_piranha";
            } else if (customModelData == 23) {
                return "red_grouper";
            } else if (customModelData == 24) {
                return "smallmouth_bass";
            } else if (customModelData == 25) {
                return "synodontis";
            } else if (customModelData == 26) {
                return "tambaqui";
            } else if (customModelData == 27) {
                return "tuna";
            }
        }
        return null;
    }
}

