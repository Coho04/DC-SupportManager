package de.goldendeveloper.supportmanager;

import de.goldendeveloper.mysql.MYSQL;
import de.goldendeveloper.mysql.entities.Database;
import de.goldendeveloper.mysql.entities.Table;

public class MysqlConnection {

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
    private final MYSQL mysql;

    public MysqlConnection(String hostname, String username, String password, int port) {
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

    private void addColumn(Table table, String name) {
        if (!table.existsColumn(name)) {
            table.addColumn(name);
        }
    }

    public MYSQL getMysql() {
        return mysql;
    }
}
