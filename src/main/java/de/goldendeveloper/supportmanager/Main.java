package de.goldendeveloper.supportmanager;

import de.goldendeveloper.supportmanager.discord.commands.Settings;
import de.goldendeveloper.supportmanager.events.Events;
import de.goldendeveloper.supportmanager.utility.CustomConfig;
import io.github.coho04.dcbcore.DCBotBuilder;

public class Main {

    private static MYSQL mysql;
    private static CustomConfig customConfig;

    public static void main(String[] args) {
        customConfig = new CustomConfig();
        mysql = new MYSQL();
        DCBotBuilder dcBotBuilder = new DCBotBuilder(args, true);
        dcBotBuilder.registerEvents(new Events());
        dcBotBuilder.registerCommands(new Settings());
        dcBotBuilder.build();
    }

    public static MYSQL getMysql() {
        return mysql;
    }

    public static CustomConfig getCustomConfig() {
        return customConfig;
    }
}
