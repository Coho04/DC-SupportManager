package de.goldendeveloper.supportmanager;

import de.goldendeveloper.supportmanager.utility.Config;

public class Main {

    private static Discord discord;
    private static MysqlConnection mysqlConnection;
    private static Config config;

    public static void main(String[] args) {
        config = new Config();
        discord = new Discord(config.getDiscordToken());
        mysqlConnection = new MysqlConnection(config.getMysqlHostname(), config.getMysqlUsername(), config.getMysqlPassword(), config.getMysqlPort());
    }

    public static Discord getDiscord() {
        return discord;
    }
    public static Config getConfig() {
        return config;
    }
    public static MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }
}
