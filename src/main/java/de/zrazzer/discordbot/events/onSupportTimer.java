package de.zrazzer.discordbot.events;

import de.zrazzer.discordbot.Main;
import de.zrazzer.discordbot.Runner;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;

public class onSupportTimer {

    public static long VoiceChannel = 2123123123123123L;        //TODO: Support Channel
    public static Role role = Main.Bot.getRoleById("<ID>");     //TODO: Role
    public static Long DiscordServer = 123456789L;              //TODO: Discord Server ID

    public static void run() {
        Calendar timeOfDay = Calendar.getInstance();
        timeOfDay.set(Calendar.HOUR_OF_DAY, 15);    //TODO: STUNDE
        timeOfDay.set(Calendar.MINUTE, 11);         //TODO: MINUTE
        timeOfDay.set(Calendar.SECOND, 0);          //TODO: SEKUNDEN

        //Open
        new Runner(timeOfDay, new Runnable() {
            @Override
            public void run() {
                try {
                    net.dv8tion.jda.api.entities.VoiceChannel channel = Main.Bot.getVoiceChannelById(VoiceChannel);
                    channel.upsertPermissionOverride(role).setAllow(Permission.VOICE_CONNECT).queue();
                    channel.getManager().setName("✅Support: Geöffnet✅").putPermissionOverride(role, Arrays.asList(Permission.VOICE_CONNECT), null).submit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "daily-support-open");

        timeOfDay.set(Calendar.HOUR_OF_DAY, 15); //TODO: STUNDE
        timeOfDay.set(Calendar.MINUTE, 12);      //TODO: MINUTE
        timeOfDay.set(Calendar.SECOND, 0);      //TODO: SEKUNDEN
        //Close
        new Runner(timeOfDay, new Runnable() {
            @Override
            public void run() {
                try {
                    VoiceChannel channel = Main.Bot.getGuildById(DiscordServer).getVoiceChannelById(VoiceChannel);
                    channel.upsertPermissionOverride(role).deny(Permission.VOICE_CONNECT).queue();
                    channel.getManager().setName("⛔Support: Geschlossen⛔").putPermissionOverride(role, null, Arrays.asList(Permission.VOICE_CONNECT)).submit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "daily-support-close");
    }
}
