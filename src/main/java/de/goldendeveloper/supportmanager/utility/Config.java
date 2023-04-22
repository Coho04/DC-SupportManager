package de.goldendeveloper.supportmanager.utility;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private final String discordToken;
    private final String discordWebhook;
    private final String mysqlHostname;
    private final String mysqlUsername;
    private final String mysqlPassword;
    private final int mysqlPort;
    private final String serverHostname;
    private final int serverPort;
    private final String sentryDNS;

    public Config() {
        Dotenv dotenv = Dotenv.load();
        discordToken = dotenv.get("DISCORD_TOKEN");
        discordWebhook = dotenv.get("DISCORD_WEBHOOK");
        mysqlHostname = dotenv.get("MYSQL_HOSTNAME");
        mysqlPort  = Integer.parseInt(dotenv.get("MYSQL_PORT"));
        mysqlUsername = dotenv.get("MYSQL_USERNAME");
        mysqlPassword = dotenv.get("MYSQL_PASSWORD");
        serverHostname = dotenv.get("SERVER_HOSTNAME");
        serverPort = Integer.parseInt(dotenv.get("SERVER_PORT"));
        sentryDNS = dotenv.get("SENTRY_DNS");
    }

    public String getProjektVersion() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("version");
    }

    public String getProjektName() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("name");
    }

    public String getDiscordWebhook() {
        return discordWebhook;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public String getMysqlHostname() {
        return mysqlHostname;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getSentryDNS() {
        return sentryDNS;
    }

    public String getServerHostname() {
        return serverHostname;
    }
}