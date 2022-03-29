package de.goldendeveloper.supportmanager;

import de.goldendeveloper.mysql.MYSQL;
import de.goldendeveloper.mysql.entities.Database;
import de.goldendeveloper.mysql.entities.MysqlTypes;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.supportmanager.utility.Config;

public class Main {

    public static String dbName = "GD-Support-Manager";
    public static String TableGuilds = "Guilds";

    public static String colGuild = "Guild";
    public static String colSupChannel = "Guild";

    public static String colMon = "Montag";
    public static String colDie = "Dienstag";
    public static String colMit = "Mittwoch";
    public static String colDon = "Donnerstag";
    public static String colFre = "Freitag";
    public static String colSam = "Samstag";
    public static String colSon = "Sonntag";

    private static Discord discord;
    private static MYSQL mysql;
    private static Config config;

    public static void main(String[] args) {
        config = new Config();
        discord = new Discord(config.getDiscordToken());
        createMysql(config.getMysqlHostname(), config.getMysqlUsername(), config.getMysqlPassword(), config.getMysqlPort());
    }

    public static Discord getDiscord() {
        return discord;
    }

    private static void createMysql(String hostname, String username, String password, int port) {
        mysql = new MYSQL(hostname, username, password, port);
        if (!mysql.existsDatabase(dbName)) {
            mysql.createDatabase(dbName);
        }
        Database db = mysql.getDatabase(dbName);
        if (!db.existsTable(TableGuilds)) {
            db.createTable(TableGuilds);
        }
        Table table = db.getTable(TableGuilds);
        addColumn(table, colMon);
        addColumn(table, colDie);
        addColumn(table, colMit);
        addColumn(table, colDon);
        addColumn(table, colFre);
        addColumn(table, colSam);
        addColumn(table, colSon);

        addColumn(table, colGuild);
        addColumn(table, colSupChannel);
    }

    public static Config getConfig() {
        return config;
    }

    public static MYSQL getMysql() {
        return mysql;
    }

    private static void addColumn(Table table, String name) {
        if (!table.existsColumn(name)) {
            table.addColumn(name, MysqlTypes.VARCHAR, 80);
        }
    }
}
