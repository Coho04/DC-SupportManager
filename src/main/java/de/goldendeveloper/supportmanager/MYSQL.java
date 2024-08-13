package de.goldendeveloper.supportmanager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.sentry.Sentry;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for managing the MySQL database connection.
 */
public class MYSQL {

    /**
     * The HikariDataSource object that represents the pool of database connections.
     */
    private final HikariDataSource source;

    /**
     * The constructor for the MYSQL class.
     * It initializes the HikariDataSource and creates the necessary database and table if they do not exist.
     */
    public MYSQL() {
        this.source = getConfig();
        try {
            Statement statement = this.source.getConnection().createStatement();
            statement.execute("CREATE DATABASE IF NOT EXISTS `support_manager_db`;");
            statement.execute("USE `support_manager_db`;");
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

    /**
     * This method configures and returns a HikariDataSource object.
     *
     * @return A HikariDataSource object configured with the MySQL connection details.
     */
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

    /**
     * This method returns the HikariDataSource object.
     *
     * @return The HikariDataSource object that represents the pool of database connections.
     */
    public HikariDataSource getSource() {
        return source;
    }
}
