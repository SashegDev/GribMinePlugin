package net.sashegdev.gribMine;

public class TPSUtil {

    public static double getTPS() {
        try {
            // Получаем Spark через рефлексию
            Class<?> sparkClass = Class.forName("me.lucko.spark.api.SparkProvider");
            Object sparkInstance = sparkClass.getMethod("get").invoke(null);

            // Получаем TPS
            Object tpsStatistic = sparkInstance.getClass().getMethod("tps").invoke(sparkInstance);
            Object tpsValue = tpsStatistic.getClass().getMethod("poll", Class.forName("me.lucko.spark.api.statistic.StatisticWindow$TicksPerSecond"))
                    .invoke(tpsStatistic, Enum.valueOf((Class<Enum>) Class.forName("me.lucko.spark.api.statistic.StatisticWindow$TicksPerSecond"), "MINUTES_1"));

            return (double) tpsValue;
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при получении TPS через Spark: " + e.getMessage());
        }
    }
}