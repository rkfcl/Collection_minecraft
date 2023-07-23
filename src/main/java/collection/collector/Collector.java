package collection.collector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Collector extends JavaPlugin implements Listener {
    private File dataFolder;
    @Override
    public void onEnable() {
        getLogger().info("FishCollection 플러그인이 활성화되었습니다.");

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginCommand("물고기도감").setExecutor(new CollectorCommand(this));
        getServer().getPluginCommand("요리도감").setExecutor(new CollectorCommand(this));
        dataFolder = new File(getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }
    @Override
    public void onDisable() {
        getLogger().info("FishCollection 플러그인이 비활성화되었습니다.");
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        ItemStack itemStack = event.getItem().getItemStack();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (itemStack.getType() == Material.ROTTEN_FLESH && itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasCustomModelData()) {
                    int customModelData = itemMeta.getCustomModelData();
                    // 플레이어의 UUID로 파일명 생성
                    String fileName = player.getUniqueId().toString() + ".yml";
                    File playerFile = new File(dataFolder, fileName);
                    try {

                        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
                        // 파일이 존재하지 않는 경우에만 기본 양식을 생성
                        if (!playerFile.exists()) {
                            playerData.set("플레이어이름", player.getName());
                            playerData.set("플레이어UUID", player.getUniqueId().toString());
                            playerData.set("잡은물고기", new ArrayList<String>());
                            playerData.set("9보상", false);
                            playerData.set("18보상", false);
                            playerData.set("27보상", false);
                            playerData.set("요리", new ArrayList<String>());
                            playerData.save(playerFile);
                        }

                        // 물고기 정보 추가
                        Set<String> fishSet = new HashSet<>(playerData.getStringList("잡은물고기"));
                        String fishData = itemStack.getType().name() + ":" + customModelData;
                        if (!fishSet.contains(fishData)) {
                            fishSet.add(fishData);
                            playerData.set("잡은물고기", new ArrayList<>(fishSet));
                            // 잡은 물고기 개수 확인
                            int caughtFishCount = fishSet.size();
                            // 보상을 받았는지 여부 확인
                            boolean hasReceived9Reward = playerData.getBoolean("9보상");
                            boolean hasReceived18Reward = playerData.getBoolean("18보상");
                            boolean hasReceived27Reward = playerData.getBoolean("27보상");

                            if (caughtFishCount >= 9 && caughtFishCount < 18 && !hasReceived9Reward) {
                                // 보상 지급 로직
                                sendTitle(player, "도감 보상 지급!", "§a[ 물고기도감 ] §f9개 채우기 §a완료!", 10, 70, 10);
                                player.sendMessage(ChatColor.GREEN + "9개 도감을 채웠습니다!");
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "iagive " + player.getName() + " protectblock:rkfcl_coin " + 9);
                                // 보상 지급 후 상태 업데이트
                                playerData.set("9보상", true);
                            } else if (caughtFishCount >= 18 && caughtFishCount < 27 && !hasReceived18Reward) {
                                // 보상 지급 로직
                                sendTitle(player, "도감 보상 지급!", "§a[ 물고기도감 ] §f18개 채우기 §a완료!", 10, 70, 10);
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "iagive " + player.getName() + " protectblock:rkfcl_coin " + 18);
                                // 보상 지급 후 상태 업데이트
                                playerData.set("18보상", true);
                            } else if (caughtFishCount >= 27 && !hasReceived27Reward) {
                                // 보상 지급 로직
                                sendTitle(player, "도감 보상 지급!", "§a[ 물고기도감 ] §f모든 도감 획득 §a완료!", 10, 70, 10);
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "iagive " + player.getName() + " protectblock:rkfcl_coin " + 27);
                                // 보상 지급 후 상태 업데이트
                                playerData.set("27보상", true);
                            }
                            playerData.save(playerFile);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void ShopMenuInventory(InventoryClickEvent event) {
        CollectorCommand collectorCommand= new CollectorCommand(this);
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();

        if (event.getClickedInventory() == null) return;

        if (event.getView().getTitle().equalsIgnoreCase("물고기 도감")||event.getView().getTitle().equalsIgnoreCase("요리 도감")) {
            event.setCancelled(true); // 이벤트 취소하여 아이템을 메뉴로 옮기지 못하도록 함
            if (inventory != null && inventory.getType() == InventoryType.PLAYER) {
                // 클릭한 인벤토리가 플레이어 인벤토리인 경우
                event.setCancelled(true); // 이벤트 취소하여 아이템을 메뉴로 옮기지 못하도록 함
            }
        }else if (event.getView().getTitle().equalsIgnoreCase("[ 갈치의 놀이터 ] 메뉴")) {
            event.setCancelled(true); // 이벤트 취소하여 아이템을 메뉴로 옮기지 못하도록 함
            if (inventory != null && inventory.getType() == InventoryType.PLAYER) {
                // 클릭한 인벤토리가 플레이어 인벤토리인 경우
                event.setCancelled(true); // 이벤트 취소하여 아이템을 메뉴로 옮기지 못하도록 함
            }
            if (event.getSlot() == 22){
                collectorCommand.createFoodCollectionInventory(player);
                player.sendMessage("§b[ 요리 도감 ] §f/요리도감 : 도감를 오픈합니다.");
            }
            if (event.getSlot() == 24){
                collectorCommand.createFishCollectionInventory(player);
                player.sendMessage("§6[ 물고기 도감 ] §f/물고기도감 : 도감를 오픈합니다.");
                player.sendMessage("§6[ 물고기 도감 ] §f9개, 18개, 27개 도감을 채우면 보상을 지급 합니다!");
            }
        }
    }
    private void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(
                ChatColor.BOLD + title,     // 타이틀 텍스트
                ChatColor.BOLD + subTitle,  // 타이틀 서브텍스트 (크고 굵은 텍스트로 표시됨)
                fadeIn,                    // 페이드인 시간 (틱 단위, 20틱 = 1초)
                stay,                      // 표시 시간 (틱 단위, 20틱 = 1초)
                fadeOut                    // 페이드아웃 시간 (틱 단위, 20틱 = 1초)
        );
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack resultItem = event.getRecipe().getResult();
        String foodName = resultItem.getItemMeta().getDisplayName();

        // 대소문자를 모두 소문자로 변환하고 색 코드 제거 후 공백을 모두 제거하여 처리
        String formattedFoodName = ChatColor.stripColor(foodName).toLowerCase().replaceAll("\\s+", "");

        for (String food : CollectorCommand.foodNames) {
            String[] parts = food.split(":");
            String itemName = parts[1].toLowerCase().replaceAll("\\s+", "");
            if (formattedFoodName.equals(itemName)) {
                // "collection_" 제거하여 출력
                String itemNameWithoutCollection = parts[0].substring("collection_".length());

                // 요리 정보 저장
                saveCookedFood(player, itemNameWithoutCollection);
                break;
            }
        }
    }

    private void saveCookedFood(Player player, String itemName) {
        try {
            // 플레이어의 UUID로 파일명 생성
            String fileName = player.getUniqueId().toString() + ".yml";
            File playerFile = new File(dataFolder, fileName);
            FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
            // 파일이 존재하지 않는 경우에만 기본 양식을 생성
            if (!playerFile.exists()) {
                playerData.set("플레이어이름", player.getName());
                playerData.set("플레이어UUID", player.getUniqueId().toString());
                playerData.set("잡은물고기", new ArrayList<String>());
                playerData.set("9보상", false);
                playerData.set("18보상", false);
                playerData.set("27보상", false);
                playerData.set("요리", new ArrayList<String>());
                playerData.save(playerFile);
            }
            // 요리 정보 추가
            Set<String> cookedFoodSet = new HashSet<>(playerData.getStringList("요리"));
            cookedFoodSet.add(itemName);
            playerData.set("요리", new ArrayList<>(cookedFoodSet));

            playerData.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @EventHandler
    public void onSmeltItem(FurnaceExtractEvent  event) {
        Player player = event.getPlayer();
        ItemStack resultItem = event.getPlayer().getItemOnCursor();
        String foodName = resultItem.getItemMeta().getDisplayName();

        // 대소문자를 모두 소문자로 변환하고 색 코드 제거 후 공백을 모두 제거하여 처리
        String formattedFoodName = ChatColor.stripColor(foodName).toLowerCase().replaceAll("\\s+", "");

        for (String food : CollectorCommand.foodNames) {
            String[] parts = food.split(":");
            String itemName = parts[1].toLowerCase().replaceAll("\\s+", "");
            if (formattedFoodName.equals(itemName)) {
                // "collection_" 제거하여 출력
                String itemNameWithoutCollection = parts[0].substring("collection_".length());

                // 요리 정보 저장
                saveCookedFood(player, itemNameWithoutCollection);
                break;
            }
        }
    }



}
