package net.sashegdev.gribMine;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import org.bukkit.ChatColor;

import java.util.Objects;

public class TPSUtil {

    public static double getTPS() {
        Spark spark = SparkProvider.get(); // Получаем экземпляр Spark
        if (spark == null) {
            throw new IllegalStateException("Spark не найден. Убедитесь, что Spark установлен на сервере.");
        }

        // Получаем TPS за последнюю минуту
        return Objects.requireNonNull(spark.tps()).poll(StatisticWindow.TicksPerSecond.MINUTES_1);
    }

    public static double getMSPT() {
        Spark spark = SparkProvider.get(); // Получаем экземпляр Spark
        if (spark == null) {
            throw new IllegalStateException("Spark не найден. Убедитесь, что Spark установлен на сервере.");
        }

        // Получаем MSPT за последнюю минуту
        GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> msptStatistic = spark.mspt();
        assert msptStatistic != null;
        DoubleAverageInfo msptInfo = msptStatistic.poll(StatisticWindow.MillisPerTick.MINUTES_1);

        // Возвращаем среднее значение MSPT
        return msptInfo != null ? msptInfo.mean() : 0.0;
    }

    public static class UsageUtil {
        public static double[] getCPUUsage() {
            Spark spark = SparkProvider.get(); // Получаем экземпляр Spark
            if (spark == null) {
                throw new IllegalStateException("Spark не найден. Убедитесь, что Spark установлен на сервере.");
            }

            // Получаем использование CPU процессом и системой за 1, 5 и 10 минут
            DoubleStatistic<StatisticWindow.CpuUsage> process = spark.cpuProcess();
            DoubleStatistic<StatisticWindow.CpuUsage> system = spark.cpuSystem();

            double process10Sec = process.poll(StatisticWindow.CpuUsage.SECONDS_10);
            double process1Min = process.poll(StatisticWindow.CpuUsage.MINUTES_1);
            double process15Min = process.poll(StatisticWindow.CpuUsage.MINUTES_15);

            double system10Sec = system.poll(StatisticWindow.CpuUsage.SECONDS_10);
            double system1Min  = system.poll(StatisticWindow.CpuUsage.MINUTES_1);
            double system15Min = system.poll(StatisticWindow.CpuUsage.MINUTES_15);

            return new double[]{process10Sec, process1Min, process15Min, system10Sec, system1Min, system15Min};
        }
    }
    static String getColorForCpuUsage(double cpuUsage) {
        if (cpuUsage <= 50.0) {
            return ChatColor.GREEN.toString(); // Низкая нагрузка
        } else if (cpuUsage <= 75.0) {
            return ChatColor.YELLOW.toString(); // Средняя нагрузка
        } else {
            return ChatColor.RED.toString(); // Высокая нагрузка
        }
    }
}