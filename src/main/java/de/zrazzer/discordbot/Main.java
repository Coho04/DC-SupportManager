package de.zrazzer.discordbot;

import de.zrazzer.discordbot.events.onSupportJoin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import de.zrazzer.discordbot.events.onSupportTimer;

import javax.security.auth.login.LoginException;

public class Main {

    public static JDA Bot;
    public static String BotToken = "ODU0NjUyNDgxMjc0OTA0NTc3.YMnDJg.Cu-JLDvFbHNMhDDaWsU_s6uEweo";
    public static String activity = "<BOT-Activity>";

    public static void main(String[] args) {
        botCreate();
    }

    public static void botCreate() {
        try {
            Bot = JDABuilder.createDefault(BotToken)
                    .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS,
                            GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_BANS, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_INVITES, GatewayIntent.DIRECT_MESSAGE_TYPING,
                            GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGE_TYPING)
                    .setActivity(Activity.watching(activity))
                    .addEventListeners(new onSupportJoin())
                    .setAutoReconnect(true)
                    .build();

            //Start des Support Open / Schließ Timers
            onSupportTimer.run();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
