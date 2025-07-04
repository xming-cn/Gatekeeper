package com.xming.gatekeeper;

import com.xming.gatekeeper.api.ApiGateway;
import com.xming.gatekeeper.api.route.ApiResponse;
import com.xming.gatekeeper.api.ws.WebSocketHandler;
import com.xming.gatekeeper.api.ws.WsHub;
import com.xming.gatekeeper.api.ws.WsSession;
import com.xming.gatekeeper.web.ApiGatewayImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GatekeeperRoutes {
    static public void registerPluginRoutes(Plugin plugin, ApiGateway gateway) {
        gateway.registerPluginRoutes(plugin, builder -> {
            builder.get(
                    "/ping",
                    request -> ApiResponse.json(200, Map.of("message", "pong"))
            );
            builder.get(
                    "/online-players",
                    request -> {
                        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                        List<Map<String, String>> playerList = onlinePlayers.stream()
                                .map(player -> Map.of(
                                        "id", player.getUniqueId().toString(),
                                        "name", player.getName()
                                )).toList();
                        return ApiResponse.json(200, Map.of(
                                "count", onlinePlayers.size(),
                                "players", playerList
                        ));
                    }
            );
            builder.get(
                    "/player/{id}",
                    request -> {
                        String playerId = request.getPathParam("id");
                        Player player = getPlayer(playerId);
                        if (player == null) {
                            return ApiResponse.json(404, Map.of("error", "Player not found"));
                        }
                        return ApiResponse.json(200, Map.of(
                                "id", player.getUniqueId().toString(),
                                "name", player.getName(),
                                "health", player.getHealth(),
                                "level", player.getLevel(),
                                "exp", player.getExp(),
                                "ping", player.getPing(),
                                "gameMode", player.getGameMode().name(),
                                "isOp", player.isOp(),
                                "isOnline", player.isOnline(),
                                "location", Map.of(
                                        "world", player.getWorld().getName(),
                                        "x", player.getLocation().getX(),
                                        "y", player.getLocation().getY(),
                                        "z", player.getLocation().getZ()
                                )
                        ));
                    }
            );
            builder.get("/player/{id}/inventory", request -> {
                        String playerId = request.getPathParam("id");
                        Player player = getPlayer(playerId);
                        if (player == null) {
                            return ApiResponse.json(404, Map.of("error", "Player not found"));
                        }
                        List<Map<String, Object>> inventory = new ArrayList<>();
                        player.getInventory().forEach(item -> {
                            if (item != null) {
                                inventory.add(Map.of(
                                        "type", item.getType().name(),
                                        "amount", item.getAmount(),
                                        "displayName", item.getItemMeta() != null ? item.getItemMeta().getDisplayName() : ""
                                ));
                            }
                        });
                        return ApiResponse.json(200, Map.of("inventory", inventory));
                    }
            );
            builder.post(
                    "/player/{id}/kick",
                    request -> {
                        String playerId = request.getPathParam("id");
                        Player player = getPlayer(playerId);
                        if (player == null) {
                            return ApiResponse.json(404, Map.of("error", "Player not found"));
                        }
                        String reason = request.getJsonBody().getOrDefault("reason", "You have been kicked by an admin.").toString();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.kickPlayer(reason);
                            }
                        }.runTask(plugin);
                        return ApiResponse.json(200, Map.of("status", "success"));
                    }
            );
            builder.post(
                    "/player/{id}/send-message",
                    request -> {
                        String playerId = request.getPathParam("id");
                        Player player = getPlayer(playerId);
                        if (player == null) {
                            return ApiResponse.json(404, Map.of("error", "Player not found"));
                        }
                        Map<String, Object> data = request.getJsonBody();
                        String message = (String) data.get("message");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.sendMessage(message);
                            }
                        }.runTask(plugin);
                        return ApiResponse.json(200, Map.of("status", "success"));
                    }
            );
            builder.post(
                    "/player/{id}/teleport",
                    request -> {
                        String playerId = request.getPathParam("id");
                        Player player = getPlayer(playerId);
                        if (player == null) {
                            return ApiResponse.json(404, Map.of("error", "Player not found"));
                        }
                        Map<String, Object> data = request.getJsonBody();
                        String worldName = (String) data.get("world");
                        double x = ((Number) data.get("x")).doubleValue();
                        double y = ((Number) data.get("y")).doubleValue();
                        double z = ((Number) data.get("z")).doubleValue();

                        if (worldName == null) {
                            return ApiResponse.json(400, Map.of("error", "Invalid teleport data"));
                        }

                        var world = Bukkit.getWorld(worldName);
                        if (world == null) {
                            return ApiResponse.json(404, Map.of("error", "World not found"));
                        }

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.teleport(new Location(world, x, y, z));
                            }
                        }.runTask(plugin);
                        return ApiResponse.json(200, Map.of("status", "success"));
                    }
            );
            builder.post(
                    "/player/{id}/message",
                    request -> {
                        String playerId = request.getPathParam("id");
                        Player player = getPlayer(playerId);
                        if (player == null) {
                            return ApiResponse.json(404, Map.of("error", "Player not found"));
                        }
                        Map<String, Object> data = request.getJsonBody();
                        String message = (String) data.get("message");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.sendMessage(message);
                            }
                        }.runTask(plugin);
                        return ApiResponse.json(200, Map.of("status", "success"));
                    }
            );
            builder.post(
                    "/broadcast",
                    request -> {
                        Map<String, Object> data = request.getJsonBody();
                        String message = (String) data.get("message");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getServer().broadcastMessage(message);
                            }
                        }.runTask(plugin);
                        return ApiResponse.json(200, Map.of("status", "success"));
                    }
            );
            builder.post(
                    "/execute-command",
                    request -> {
                        Map<String, Object> data = request.getJsonBody();
                        String command = (String) data.get("command");
                        if (command == null || command.isEmpty()) {
                            return ApiResponse.json(400, Map.of("error", "Command cannot be empty"));
                        }
                        Bukkit.getConsoleSender().sendMessage("Gatekeeper> " + command);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            }
                        }.runTask(plugin);
                        return ApiResponse.json(200, Map.of("status", "success"));
                    }
            );
        });
        gateway.registerWebSocketRoutes(plugin, builder -> {
            builder.on("/logger", new WebSocketHandler() {
                @Override
                public void onConnect(WsSession session) {

                }

                @Override
                public void onMessage(WsSession session, String message) {

                }

                @Override
                public void onClose(WsSession session, int status, String reason) {

                }

                @Override
                public void onError(WsSession session, Throwable error) {

                }
            });
        });

        WsHub wsHub = gateway.getWsHub();
        registerLog4jAppender(wsHub);
    }

    static public void registerLog4jAppender(WsHub hub) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        WebSocketAppender appender = new WebSocketAppender(
                "WebSocketAppender", null, PatternLayout.createDefaultLayout(),
                false, hub
        );
        appender.start();

        config.addAppender(appender);

        // Attach appender to root logger
        LoggerConfig rootLogger = config.getRootLogger();
        rootLogger.addAppender(appender, null, null);

        context.updateLoggers(); // 重要：刷新配置
    }

    // get player by name or UUID
    static private Player getPlayer(String identifier) {
        Player player = Bukkit.getPlayer(identifier);
        if (player == null) {
            try {
                UUID uuid = UUID.fromString(identifier);
                player = Bukkit.getPlayer(uuid);
            } catch (IllegalArgumentException e) {
                List<Player> players = Bukkit.matchPlayer(identifier);
                if (!players.isEmpty()) {
                    player = players.getFirst();
                }
            }
        }
        return player;
    }

    public static void unregisterPluginRoutes(Gatekeeper gatekeeper, ApiGatewayImpl gateway) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        config.getRootLogger().removeAppender("WebSocketAppender");
        context.updateLoggers();
    }
}
