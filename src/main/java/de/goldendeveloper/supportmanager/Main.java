package de.goldendeveloper.supportmanager;

import de.goldendeveloper.supportmanager.discord.commands.Settings;
import de.goldendeveloper.supportmanager.events.Events;
import de.goldendeveloper.supportmanager.utility.CustomConfig;
import io.github.coho04.dcbcore.DCBotBuilder;

/**
 * The main class of the application.
 */
public class Main {

    /**
     * The MySQL instance used by the application.
     */
    private static MYSQL mysql;

    /**
     * The custom configuration instance used by the application.
     */
    private static CustomConfig customConfig;

    /**
     * The main method of the application.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        // Initialize the custom configuration
        customConfig = new CustomConfig();

        // Initialize the MySQL instance
        mysql = new MYSQL();

        // Create a new bot builder
        DCBotBuilder dcBotBuilder = new DCBotBuilder(args, true);

        // Register the events
        dcBotBuilder.registerEvents(new Events());

        // Register the commands
        dcBotBuilder.registerCommands(new Settings());

        // Build the bot
        dcBotBuilder.build();
    }

    /**
     * Returns the MySQL instance used by the application.
     *
     * @return The MySQL instance.
     */
    public static MYSQL getMysql() {
        return mysql;
    }

    /**
     * Returns the custom configuration instance used by the application.
     *
     * @return The custom configuration instance.
     */
    public static CustomConfig getCustomConfig() {
        return customConfig;
    }
}