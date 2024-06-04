package de.goldendeveloper.supportmanager;

import de.goldendeveloper.dcbcore.DCBotBuilder;
import de.goldendeveloper.supportmanager.discord.commands.Settings;
import de.goldendeveloper.supportmanager.events.Events;
import de.goldendeveloper.supportmanager.utility.CustomConfig;

public class Main {

    private static MYSQL mysql;
    private static CustomConfig customConfig;

    public static void main(String[] args) {
        customConfig = new CustomConfig();
        mysql = new MYSQL(customConfig.getMysqlHostname(), customConfig.getMysqlUsername(), customConfig.getMysqlPassword(), customConfig.getMysqlPort());
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
