package net.sashegdev.gribMine.airdrop;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sashegdev.gribMine.GribMine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UpdateChecker {

    private static final String GITHUB_RELEASES_URL = "https://api.github.com/repos/SashegDev/GribMinePlugin/releases";
    private static final String PLUGIN_NAME = "GribMine.jar"; // Имя файла плагина

    public static void checkForUpdates(GribMine plugin) {
        if (!plugin.getConfig().getBoolean("check-for-updates", true)) {
            return; // Автопроверка отключена
        }

        String versionType = plugin.getConfig().getString("version-type", "release").toLowerCase();
        String currentVersion = plugin.getDescription().getVersion();

        try {
            URL url = new URL(GITHUB_RELEASES_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                JsonArray releases = JsonParser.parseReader(reader).getAsJsonArray();

                for (JsonElement releaseElement : releases) {
                    JsonObject release = releaseElement.getAsJsonObject();
                    String tagName = release.get("tag_name").getAsString();
                    boolean isPrerelease = release.get("prerelease").getAsBoolean();

                    // Проверяем тип версии
                    if ((versionType.equals("release") && !isPrerelease) ||
                            (versionType.equals("beta") && isPrerelease) ||
                            (versionType.equals("nightly") && tagName.contains("nightly"))) {

                        if (isNewerVersion(tagName, currentVersion)) {
                            // Новая версия найдена
                            plugin.getLogger().info("Найдена новая версия: " + tagName);
                            downloadNewVersion(release, plugin);
                            break;
                        }
                    }
                }
            } else {
                plugin.getLogger().warning("Не удалось проверить обновления. Код ответа: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Ошибка при проверке обновлений: " + e.getMessage());
        }
    }

    private static boolean isNewerVersion(String newVersion, String currentVersion) {
        // Убираем возможные префиксы (например, "v")
        newVersion = newVersion.replace("v", "").replace("V", "");
        currentVersion = currentVersion.replace("v", "").replace("V", "");

        // Разбиваем версии на части
        String[] newParts = newVersion.split("\\.");
        String[] currentParts = currentVersion.split("\\.");

        // Сравниваем каждую часть версии
        for (int i = 0; i < Math.max(newParts.length, currentParts.length); i++) {
            int newPart = i < newParts.length ? Integer.parseInt(newParts[i]) : 0;
            int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;

            if (newPart > currentPart) {
                return true; // Новая версия больше
            } else if (newPart < currentPart) {
                return false; // Новая версия меньше
            }
        }

        return false; // Версии равны
    }

    private static void downloadNewVersion(JsonObject release, GribMine plugin) {
        try {
            JsonArray assets = release.getAsJsonArray("assets");
            for (JsonElement assetElement : assets) {
                JsonObject asset = assetElement.getAsJsonObject();
                String assetName = asset.get("name").getAsString();

                if (assetName.equals(PLUGIN_NAME)) {
                    String downloadUrl = asset.get("browser_download_url").getAsString();
                    File pluginFile = new File(plugin.getDataFolder().getParent(), PLUGIN_NAME);

                    // Скачиваем новую версию
                    URL url = new URL(downloadUrl);
                    Files.copy(url.openStream(), pluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    plugin.getLogger().info("Новая версия успешно загружена. Перезагрузите сервер для применения изменений.");
                    break;
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Ошибка при загрузке новой версии: " + e.getMessage());
        }
    }
}