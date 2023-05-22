package de.goldendeveloper.supportmanager;

import de.goldendeveloper.dcbcore.DCBotBuilder;
import de.goldendeveloper.supportmanager.discord.commands.Settings;
import de.goldendeveloper.supportmanager.events.Events;
import de.goldendeveloper.supportmanager.utility.CustomConfig;

public class Main {

    private static MysqlConnection mysqlConnection;

    public static void main(String[] args) {
        CustomConfig config = new CustomConfig();
        mysqlConnection = new MysqlConnection(config.getMysqlHostname(), config.getMysqlUsername(), config.getMysqlPassword(), config.getMysqlPort());
        DCBotBuilder dcBotBuilder = new DCBotBuilder(args, true);
        dcBotBuilder.registerEvents(new Events());
        dcBotBuilder.registerCommands(new Settings());
        dcBotBuilder.build();
    }
    public static MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }
}
