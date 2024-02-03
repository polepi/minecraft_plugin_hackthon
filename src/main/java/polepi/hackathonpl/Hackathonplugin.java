package polepi.hackathonpl;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Score;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class Hackathonplugin extends JavaPlugin implements Listener {
    Map<String, Integer> kit_ids = new HashMap<>();
    private int secondsRemaining = 15;
    int sett_health = 20;
    int isRoundActive = 0;
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

        kit_ids.put("Rifleman", 0);
        kit_ids.put("Scout", 1);
        kit_ids.put("Grenadier", 2);
        kit_ids.put("Automatic Rifleman", 3);
        kit_ids.put("Combat Medic", 4);
        kit_ids.put("Marksman", 5);
        kit_ids.put("Rocketeer", 6);

        createTeams("Attacker", ChatColor.RED);
        createTeams("Defender", ChatColor.BLUE);
        createTeams("Spectator", ChatColor.GRAY);
        Objective objective = sb.registerNewObjective("selected_kit", "dummy");
        getLogger().info("Plugin enabled!");
    }

    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

    private void createTeams(String teamName, ChatColor colour) {
        if (getServer().getScoreboardManager().getMainScoreboard().getTeam(teamName) != null) {
            return;
        }
        org.bukkit.scoreboard.Team team = getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(teamName);
        team.setDisplayName(teamName);
        team.setPrefix(ChatColor.GRAY + "[" + colour + teamName + ChatColor.GRAY + "] ");
        team.setSuffix(ChatColor.RESET.toString());
        team.setAllowFriendlyFire(false);
        team.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.FOR_OTHER_TEAMS);
    }

    private boolean isButtonAtLocation(Block block, int x, int y, int z) {
        getLogger().info("block at " + x + " " + y + " " + z);
        return block.getX() == x && block.getY() == y && block.getZ() == z;
    }

    private void teleportAttacker(Player player) {
        giveItem(player, Material.TNT, 1, "C4 Explosive");
        int randNum = new Random().nextInt(3) + 1;
        Location destination = new Location(player.getWorld(), 389, 56, -394);
        if (randNum == 2)
            destination = new Location(player.getWorld(), 387, 56, -434);
        if (randNum == 3)
            destination = new Location(player.getWorld(), 397, 56, -386);
        player.teleport(destination);
    }
    private void teleportDefender(Player player) {
        int randNum = new Random().nextInt(3) + 1;
        Location destination = new Location(player.getWorld(), 285, 69, -474);
        if (randNum == 2)
            destination = new Location(player.getWorld(), 265, 74, -370);
        if (randNum == 3)
            destination = new Location(player.getWorld(), 274, 74, -444);
        player.teleport(destination);
    }

    private void joinTeam(Player player, String teamName) {
        org.bukkit.scoreboard.Scoreboard scoreboard = getServer().getScoreboardManager().getMainScoreboard();
        org.bukkit.scoreboard.Team team = scoreboard.getTeam(teamName);
        for (org.bukkit.scoreboard.Team otherTeam : scoreboard.getTeams()) {
            if (otherTeam.hasEntry(player.getName())) {
                otherTeam.removeEntry(player.getName());
            }
        }
        team.addEntry(player.getName());
        player.sendActionBar("You joined the "+ teamName + "'s team!");
    }
    private void giveItem(Player player, Material item, int size, String ItemName) {
        ItemStack itemStack = new ItemStack(item, size);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (ItemName != "") {
            itemMeta.setDisplayName(ItemName);
            itemStack.setItemMeta(itemMeta);
        }

        player.getInventory().addItem(itemStack);
    }

    private static void createCustomPotion(Player player, PotionType potType, int lev, String itName) {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(potType));
        potion.setItemMeta(meta);
        ItemMeta itemMeta = potion.getItemMeta();
        itemMeta.setDisplayName(itName);
        potion.setItemMeta(itemMeta);
        player.getInventory().addItem(potion);
    }

    private static void giveFireworks(Player player, int amount, String itName, int pow) {
        ItemStack fireworkItem = new ItemStack(Material.FIREWORK_ROCKET, amount);
        FireworkMeta fireworkMeta = (FireworkMeta) fireworkItem.getItemMeta();
        FireworkEffect.Builder effectBuilder = FireworkEffect.builder();
        effectBuilder.withColor(Color.ORANGE);
        effectBuilder.withColor(Color.YELLOW);
        effectBuilder.with(FireworkEffect.Type.BURST);
        effectBuilder.withTrail();
        fireworkMeta.addEffect(effectBuilder.build());
        fireworkMeta.setPower(pow);
        fireworkMeta.setDisplayName(itName);
        fireworkItem.setItemMeta(fireworkMeta);
        player.getInventory().addItem(fireworkItem);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().setResourcePack("https://www.dropbox.com/scl/fi/pdru4xp6sfljlw2ugkioa/SMO.zip?rlkey=vnhaencveb6os2g3mo6k21yse&dl=1");
    }

    private void getItemsFromKit(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("selected_kit");
        Score score = objective.getScore(player.getName());
        int sel_kit_id = score.getScore();

        switch (sel_kit_id) {
            case 1:
                giveItem(player, Material.BOW, 1, "MK18");
                giveItem(player, Material.STONE_SWORD, 1, "Combat Knife");
                giveItem(player, Material.BREAD, 10, "MRE");
                giveItem(player, Material.ARROW, 24, "");
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true, false));
                ItemStack helmet1 = new ItemStack(Material.LEATHER_HELMET);
                ItemStack chestplate1 = new ItemStack(Material.LEATHER_CHESTPLATE);
                ItemStack leggings1 = new ItemStack(Material.LEATHER_LEGGINGS);
                ItemStack boots1 = new ItemStack(Material.LEATHER_BOOTS);
                player.getInventory().setArmorContents(new ItemStack[]{boots1, leggings1, chestplate1, helmet1});
                break;
            case 2:
                giveItem(player, Material.CROSSBOW, 1, "M4A1 w/ M230");
                giveItem(player, Material.WOODEN_SWORD, 1, "Knife");
                giveItem(player, Material.BREAD, 10, "MRE");
                giveItem(player, Material.ARROW, 20, "");
                giveFireworks(player, 3, "40mm Impact Grenade", 1);
                ItemStack helmet2 = new ItemStack(Material.IRON_HELMET);
                ItemStack chestplate2 = new ItemStack(Material.IRON_CHESTPLATE);
                ItemStack leggings2 = new ItemStack(Material.IRON_LEGGINGS);
                ItemStack boots2 = new ItemStack(Material.IRON_BOOTS);
                player.getInventory().setArmorContents(new ItemStack[]{boots2, leggings2, chestplate2, helmet2});
                break;
            case 3:
                ItemStack bow = new ItemStack(Material.CROSSBOW);
                bow.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 3);
                ItemMeta itemMeta = bow.getItemMeta();
                itemMeta.setDisplayName("M249 SAW");
                player.getInventory().addItem(bow);
                giveItem(player, Material.WOODEN_SWORD, 1, "Knife");
                giveItem(player, Material.BREAD, 10, "MRE");
                giveItem(player, Material.ARROW, 64, "");
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, true, false));
                ItemStack helmet3 = new ItemStack(Material.IRON_HELMET);
                ItemStack chestplate3 = new ItemStack(Material.IRON_CHESTPLATE);
                ItemStack leggings3 = new ItemStack(Material.IRON_LEGGINGS);
                ItemStack boots3 = new ItemStack(Material.IRON_BOOTS);
                player.getInventory().setArmorContents(new ItemStack[]{boots3, leggings3, chestplate3, helmet3});
                break;
            case 4:
                giveItem(player, Material.CROSSBOW, 1, "MP5 SF");
                giveItem(player, Material.WOODEN_SWORD, 1, "Knife");
                giveItem(player, Material.BREAD, 10, "MRE");
                giveItem(player, Material.ARROW, 24, "");
                createCustomPotion(player, PotionType.INSTANT_HEAL, 1, "FAK");
                createCustomPotion(player, PotionType.INSTANT_HEAL, 1, "FAK");
                createCustomPotion(player, PotionType.REGEN, 1, "Adrenaline");
                ItemStack helmet4 = new ItemStack(Material.GOLDEN_HELMET);
                ItemStack chestplate4 = new ItemStack(Material.LEATHER_CHESTPLATE);
                ItemStack leggings4 = new ItemStack(Material.LEATHER_LEGGINGS);
                ItemStack boots4 = new ItemStack(Material.LEATHER_BOOTS);
                player.getInventory().setArmorContents(new ItemStack[]{boots4, leggings4, chestplate4, helmet4});
                break;
            case 5:
                ItemStack bow5 = new ItemStack(Material.BOW);
                bow5.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
                ItemMeta itemMeta2 = bow5.getItemMeta();
                itemMeta2.setDisplayName("M24");
                player.getInventory().addItem(bow5);
                giveItem(player, Material.WOODEN_SWORD, 1, "Knife");
                giveItem(player, Material.BREAD, 10, "MRE");
                giveItem(player, Material.ARROW, 24, "");
                ItemStack helmet5 = new ItemStack(Material.LEATHER_HELMET);
                ItemStack chestplate5 = new ItemStack(Material.LEATHER_CHESTPLATE);
                ItemStack leggings5 = new ItemStack(Material.LEATHER_LEGGINGS);
                ItemStack boots5 = new ItemStack(Material.LEATHER_BOOTS);
                player.getInventory().setArmorContents(new ItemStack[]{boots5, leggings5, chestplate5, helmet5});
                break;
            case 6:
                giveItem(player, Material.CROSSBOW, 1, "M4A1");
                giveItem(player, Material.WOODEN_SWORD, 1, "Knife");
                giveItem(player, Material.BREAD, 10, "MRE");
                giveItem(player, Material.ARROW, 30, "");
                giveFireworks(player, 2, "84mm Missile", 1);
                ItemStack helmet6 = new ItemStack(Material.IRON_HELMET);
                ItemStack chestplate6 = new ItemStack(Material.IRON_CHESTPLATE);
                ItemStack leggings6 = new ItemStack(Material.IRON_LEGGINGS);
                ItemStack boots6 = new ItemStack(Material.IRON_BOOTS);
                player.getInventory().setArmorContents(new ItemStack[]{boots6, leggings6, chestplate6, helmet6});
                break;
            default:
            case 0:
                giveItem(player, Material.BOW, 1, "MK18");
                giveItem(player, Material.WOODEN_SWORD, 1, "Knife");
                giveItem(player, Material.BREAD, 12, "MRE");
                giveItem(player, Material.ARROW, 32, "");
                createCustomPotion(player, PotionType.INSTANT_DAMAGE, 1, "Frag Grenade");
                ItemStack helmet = new ItemStack(Material.IRON_HELMET);
                ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
                ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
                ItemStack boots = new ItemStack(Material.IRON_BOOTS);
                player.getInventory().setArmorContents(new ItemStack[]{boots, leggings, chestplate, helmet});
                break;
        }
    }

    private void spawnPosPlayer(Player player, int startRound) {
        if (isRoundActive == 0)
            return;
        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam("Attacker");
        if (team != null && team.hasEntry(player.getName())) {
            if (startRound == 1) {
                Location destination = new Location(player.getWorld(), 477, 62, -518);
                player.teleport(destination);
            } else {
                teleportAttacker(player);
            }
        } else if (startRound != 2) {
            team = scoreboard.getTeam("Defender");
            if (team != null && team.hasEntry(player.getName())) {
                teleportDefender(player);
            } else {
                Location destination = new Location(player.getWorld(), 265, 74, -370);
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    private void setupPlayer(Player player, int startRound) {
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.setMaxHealth(sett_health);
        player.setHealth(sett_health);
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
        player.setFireTicks(0);
        player.setFoodLevel(20);
        getItemsFromKit(player);
        spawnPosPlayer(player, startRound);
    }

    private void startTimer() {
        new BukkitRunnable() {
            int secondsLeft = 200;
            @Override
            public void run() {
                if (secondsLeft > 0 && isRoundActive == 1) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendActionBar("Round ends in " + secondsLeft);
                    }
                    secondsLeft--;
                } else {
                    if (isRoundActive == 1) {
                        isRoundActive = 0;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.setGameMode(GameMode.SURVIVAL);
                            player.getInventory().clear();
                            for (PotionEffect effect : player.getActivePotionEffects())
                                player.removePotionEffect(effect.getType());
                            player.setMaxHealth(20);
                            player.setHealth(20);
                            player.setFireTicks(0);
                            player.setFoodLevel(20);
                            player.sendTitle("Round over!", "Defensive force wins", 10, 120, 10);
                            Location destination = new Location(player.getWorld(), 485, 15, -388);
                            player.teleport(destination);

                        }
                    }
                    cancel();
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }
    private void startRound(Player p) {
        if (isRoundActive == 1) {
            Location destination = new Location(p.getWorld(), 265, 74, -370);
            p.teleport(destination);
            p.setGameMode(GameMode.SPECTATOR);
            return;
        }
        isRoundActive = 1;
        getLogger().info("Start Round");
        for (Player player : Bukkit.getOnlinePlayers()) {
            setupPlayer(player, 1);
        }

        new BukkitRunnable() {
            int secondsLeft = 13;
            @Override
            public void run() {
                if (secondsLeft > 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendActionBar("Round starts in " + secondsLeft + "...");
                    }
                    secondsLeft--;
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle("Go!", "", 10, 40, 10);
                        spawnPosPlayer(player, 2);
                    }
                    startTimer();
                    cancel();
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        event.getDrops().removeIf(item -> {
            Material itemType = item.getType();
            return itemType != Material.ARROW;
        });
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Scoreboard scoreboard = player.getScoreboard();
        Team attackerTeam = scoreboard.getTeam("Attacker");
        Team defTeam = scoreboard.getTeam("Defender");
        getLogger().info("Respawned");

        if (isRoundActive == 1) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                getLogger().info("isRoundActive");
                setupPlayer(player, 0);
            }, 5L);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        if (placedBlock.getType() == Material.TNT) {
            Block blockBelow = placedBlock.getLocation().subtract(0, 1, 0).getBlock();
            if (blockBelow.getType() == Material.GOLD_BLOCK) {
                isRoundActive = 0;
                placedBlock.setType(Material.AIR);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.getInventory().clear();
                    for (PotionEffect effect : player.getActivePotionEffects())
                        player.removePotionEffect(effect.getType());
                    player.setMaxHealth(20);
                    player.setHealth(20);
                    player.setFireTicks(0);
                    player.setFoodLevel(20);
                    player.sendTitle("Round over!", "Offensive force wins", 10, 120, 10);
                    Location destination = new Location(player.getWorld(), 485, 15, -388);
                    player.teleport(destination);

                }
            } else {
                event.setCancelled(true);
                event.getPlayer().sendActionBar("The Explosive can only be placed on top of the gold block below the radio tower.");
            }
        }
    }

    private void change_kit(Player player, String kitName) {
        int kit_id = kit_ids.getOrDefault(kitName, -1);
        getLogger().info("Selected id: "+kit_id);
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("selected_kit");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("selected_kit", "dummy", "Selected Kit");
        }
        Score score = objective.getScore(player.getName());
        score.setScore(kit_id);
        player.sendActionBar("You are now a "+kitName);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (sign.getLine(0).equalsIgnoreCase("Become a") || sign.getLine(0).equalsIgnoreCase("Become an")) {
                    joinTeam(player, sign.getLine(1));
                    return;
                }
                if (sign.getLine(0).equalsIgnoreCase("Select Class")) {
                    change_kit(player, sign.getLine(1));
                    return;
                }
                if (sign.getLine(1).equalsIgnoreCase("Start Round!")) {
                    startRound(player);
                    return;
                }
            }
        }
    }
}
