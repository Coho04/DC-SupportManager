package de.goldendeveloper.supportmanager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.sentry.Sentry;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class MYSQL {

    private final HikariDataSource source;

    public MYSQL() {
        this.source = getConfig();
        try {
            Statement statement = this.source.getConnection().createStatement();
            statement.execute("CREATE DATABASE IF NOT EXISTS `GD-SupportManager`;");
            statement.execute("USE `GD-Entertainment`;");
            statement.execute("CREATE TABLE IF NOT EXISTS Guilds (id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, " +
                    "Montag VARCHAR(255) NULL," +
                    "Dienstag VARCHAR(255) NULL," +
                    "Mittwoch VARCHAR(255) NULL, " +
                    "Donnerstag VARCHAR(255) NULL, " +
                    "Freitag VARCHAR(255) NULL," +
                    "Samstag VARCHAR(255) NULL," +
                    "Sonntag VARCHAR(255) NULL, " +
                    "Guild VARCHAR(255) NULL, " +
                    "Support_channel LONG NULL);"
            );
            statement.close();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            Sentry.captureException(exception);
        }
        System.out.println("[MYSQL] Initialized MySQL!");
    }

    private static @NotNull HikariDataSource getConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + Main.getCustomConfig().getMysqlHostname() + ":" + Main.getCustomConfig().getMysqlPort());
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30));
        config.setIdleTimeout(TimeUnit.MINUTES.toMillis(10));
        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(30));
        config.setInitializationFailTimeout(0);
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(60));
        config.setUsername(Main.getCustomConfig().getMysqlUsername());
        config.setPassword(Main.getCustomConfig().getMysqlPassword());
        config.setConnectionTestQuery("SELECT 1");
        return new HikariDataSource(config);
    }

    public HikariDataSource getSource() {
        return source;
    }
}
