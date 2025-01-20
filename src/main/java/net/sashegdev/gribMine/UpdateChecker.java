package net.sashegdev.gribMine;

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

    public static void checkForUpdates(GribMine plugin) {
        if (!plugin.getConfig().getBoolean("check-for-updates", true)) {
            DebugLogger.log("Автопроверка обновлений отключена.", DebugLogger.LogLevel.INFO);
            return;
        }

        String versionType = plugin.getConfig().getString("version-type", "release").toLowerCase();
        String currentVersion = plugin.getDescription().getVersion();
        DebugLogger.log("Текущая версия: " + currentVersion, DebugLogger.LogLevel.INFO);
        DebugLogger.log("Тип версии для проверки: " + versionType, DebugLogger.LogLevel.INFO);

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

                    DebugLogger.log("Найдена версия: " + tagName + " (prerelease: " + isPrerelease + ")", DebugLogger.LogLevel.INFO);

                    // Проверяем тип версии
                    boolean isVersionMatch = false;

                    if (versionType.equals("release")) {
                        // Для release версий игнорируем prerelease
                        isVersionMatch = !isPrerelease;
                    } else if (versionType.equals("beta") || versionType.equals("nightly")) {
                        // Для beta и nightly игнорируем параметр prerelease
                        isVersionMatch = true;
                    }

                    // Если версия подходит, проверяем, является ли она новой
                    if (isVersionMatch && isNewerVersion(tagName, currentVersion)) {
                        DebugLogger.log("Найдена новая версия: " + tagName, DebugLogger.LogLevel.INFO);
                        downloadNewVersion(release, plugin);
                        break;
                    } else {
                        DebugLogger.log("Версия " + tagName + " не является новой или не подходит по типу.", DebugLogger.LogLevel.INFO);
                    }
                }
            } else {
                DebugLogger.log("Не удалось проверить обновления. Код ответа: " + connection.getResponseCode(), DebugLogger.LogLevel.WARNING);
            }
        } catch (IOException e) {
            DebugLogger.log("Ошибка при проверке обновлений: " + e.getMessage(), DebugLogger.LogLevel.ERROR);
        }
    }

    private static boolean isNewerVersion(String newVersion, String currentVersion) {
        // Убираем возможные префиксы (например, "v", "beta-", "alpha-")
        newVersion = newVersion.replaceAll("[^0-9.]", ""); // Удаляем все нечисловые символы, кроме точек
        currentVersion = currentVersion.replaceAll("[^0-9.]", ""); // Удаляем все нечисловые символы, кроме точек

        DebugLogger.log("Очищенная новая версия: " + newVersion, DebugLogger.LogLevel.INFO);
        DebugLogger.log("Очищенная текущая версия: " + currentVersion, DebugLogger.LogLevel.INFO);

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
            String tagName = release.get("tag_name").getAsString(); // Получаем версию из тега
            String expectedFileName = "gribmineplugin-" + tagName + ".jar"; // Формируем имя файла

            // Удаляем все старые версии плагина
            File pluginsFolder = plugin.getDataFolder().getParentFile();
            File[] pluginFiles = pluginsFolder.listFiles((dir, name) -> name.startsWith("gribmineplugin-") && name.endsWith(".jar"));

            if (pluginFiles != null) {
                for (File oldPluginFile : pluginFiles) {
                    if (oldPluginFile.delete()) {
                        DebugLogger.log("Старая версия плагина удалена: " + oldPluginFile.getName(), DebugLogger.LogLevel.INFO);
                    } else {
                        DebugLogger.log("Не удалось удалить старую версию плагина: " + oldPluginFile.getName(), DebugLogger.LogLevel.WARNING);
                    }
                }
            }

            for (JsonElement assetElement : assets) {
                JsonObject asset = assetElement.getAsJsonObject();
                String assetName = asset.get("name").getAsString();

                // Проверяем, соответствует ли имя файла ожидаемому
                if (assetName.equalsIgnoreCase(expectedFileName)) {
                    String downloadUrl = asset.get("browser_download_url").getAsString();
                    File pluginFile = new File(pluginsFolder, expectedFileName);

                    // Скачиваем новую версию
                    URL url = new URL(downloadUrl);
                    Files.copy(url.openStream(), pluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    DebugLogger.log("Новая версия успешно загружена: " + expectedFileName, DebugLogger.LogLevel.INFO);
                    DebugLogger.log("Перезагрузите сервер для применения изменений.", DebugLogger.LogLevel.INFO);
                    return; // Выходим из метода после успешной загрузки
                }
            }

            // Если файл не найден
            DebugLogger.log("Файл " + expectedFileName + " не найден в ассетах.", DebugLogger.LogLevel.WARNING);
        } catch (IOException e) {
            DebugLogger.log("Ошибка при загрузке новой версии: " + e.getMessage(), DebugLogger.LogLevel.ERROR);
        }
    }
}