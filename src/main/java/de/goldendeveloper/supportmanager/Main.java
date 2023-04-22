package de.goldendeveloper.supportmanager;

import de.goldendeveloper.supportmanager.utility.Config;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

public class Main {

    private static Discord discord;
    private static MysqlConnection mysqlConnection;
    private static Config config;
    private static ServerCommunicator serverCommunicator;

    private static Boolean restart = false;
    private static Boolean deployment = true;

    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("restart")) {
            restart = true;
        }

        String device = System.getProperty("os.name").split(" ")[0];
        if (device.equalsIgnoreCase("windows") || device.equalsIgnoreCase("Mac")) {
            deployment = false;
        }
        config = new Config();
        Sentry(config.getSentryDNS());
        ITransaction transaction = Sentry.startTransaction("Application()", "task");
        try {
            Application();
        } catch (Exception e) {
            transaction.setThrowable(e);
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
        } finally {
            transaction.finish();
        }
    }

    public static void Application() {
        if (getDeployment()) {
            serverCommunicator = new ServerCommunicator(config.getServerHostname(), config.getServerPort());
        }
        discord = new Discord(config.getDiscordToken());
        mysqlConnection = new MysqlConnection(config.getMysqlHostname(), config.getMysqlUsername(), config.getMysqlPassword(), config.getMysqlPort());
    }

    public static void Sentry(String dns) {
        Sentry.init(options -> {
            options.setDsn(dns);
            options.setTracesSampleRate(1.0);
            options.setEnvironment(Main.getDeployment() ? "Production" : "localhost");
        });
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

    public static ServerCommunicator getServerCommunicator() {
        return serverCommunicator;
    }

    public static Boolean getRestart() {
        return restart;
    }

    public static Boolean getDeployment() {
        return deployment;
    }
}
