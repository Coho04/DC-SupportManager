package de.goldendeveloper.supportmanager.events;

import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.supportmanager.MysqlConnection;
import de.goldendeveloper.supportmanager.Main;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class Events extends ListenerAdapter {

    public List<Long> tempChannels = new ArrayList<>();
    public long Role = 817662233537806367L;

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null && event.getChannelLeft() != null) { //Move Event
            onLeave(event.getChannelLeft());
            onJoin(event.getChannelJoined(), event.getEntity());
        } else if (event.getChannelJoined() != null && event.getChannelLeft() == null) {// Join Event
            if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
                if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.TableGuilds)) {
                    Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.TableGuilds);
                    if (table.existsColumn(MysqlConnection.colSupChannel) && table.getColumn(MysqlConnection.colSupChannel).getAll().getAsString().contains(event.getChannelJoined().getId())) {
                        onJoin(event.getChannelJoined(), event.getEntity());
                    }
                }
            }
        } else if (event.getChannelJoined() == null && event.getChannelLeft() != null) { // Leave Event
            onLeave(event.getChannelLeft());
        }
    }

//    public static long voiceChannel = 2123123123123123L;
//    public static Role role = Main.getDiscord().getBot().getRoleById("<ID>");
//    public static long discordServer = 123456789L;

/*    public static void run() {
        Calendar timeOfDay = Calendar.getInstance();
        timeOfDay.set(Calendar.HOUR_OF_DAY, 15);
        timeOfDay.set(Calendar.MINUTE, 11);
        timeOfDay.set(Calendar.SECOND, 0);

        new Runner(timeOfDay, () -> {
            try {
                VoiceChannel channel = Main.getDiscord().getBot().getVoiceChannelById(voiceChannel);
                if (channel != null) {
                    channel.upsertPermissionOverride(role).setAllowed(Permission.VOICE_CONNECT).queue();
                    channel.getManager().setName("✅Support: Geöffnet✅").putPermissionOverride(role, List.of(Permission.VOICE_CONNECT), null).submit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "daily-support-open");

        timeOfDay.set(Calendar.HOUR_OF_DAY, 15);
        timeOfDay.set(Calendar.MINUTE, 12);
        timeOfDay.set(Calendar.SECOND, 0);

        new Runner(timeOfDay, () -> {
            try {
                VoiceChannel channel = Main.getDiscord().getBot().getGuildById(discordServer).getVoiceChannelById(voiceChannel);
                if (channel != null) {
                    channel.upsertPermissionOverride(role).deny(Permission.VOICE_CONNECT).queue();
                    channel.getManager().setName("⛔Support: Geschlossen⛔").putPermissionOverride(role, null, List.of(Permission.VOICE_CONNECT)).submit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "daily-support-close");
    }*/

    public void onJoin(AudioChannel joined, Member member) {
        if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
            if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.TableGuilds)) {
                Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.TableGuilds);
                if (table.existsColumn(MysqlConnection.colSupChannel) && table.getColumn(MysqlConnection.colSupChannel).getAll().getAsString().contains(joined.getId())) {
                    VoiceChannel ch = joined.getJDA().getVoiceChannelById(joined.getId());
                    if (ch != null) {
                        Category cat = ch.getParentCategory();
                        if (cat != null) {
                            VoiceChannel vc = cat.createVoiceChannel("⏳ | " + member.getEffectiveName()).complete();
                            vc.getManager().setUserLimit(ch.getUserLimit()).queue();
                            vc.getGuild().moveVoiceMember(member, vc).queue();
                            tempChannels.add(vc.getIdLong());
                            member.getJDA().getRoleById(Role).getGuild().getMembers().forEach(user -> {
                                if (!user.getUser().isBot() && user.getRoles().contains(member.getJDA().getRoleById(Role))) {
                                    user.getUser().openPrivateChannel().queue(channel -> {
                                        channel.sendMessage(member.getEffectiveName() + " Client-ID=||" + user.getId() + "|| " + "benötigt Support!").queue();
                                    });
                                }
                            });
                            member.getUser().openPrivateChannel().queue(channel -> {
                                channel.sendMessage("Ein Teammitglied wurde benachrichtigt, bitte warte einen Moment, es wird sich gleich um dich gekümmert.").queue();
                            });
                        }
                    }
                }
            }
        }
    }

    public void onLeave(AudioChannel channel) {
        if (channel.getMembers().size() == 0 && tempChannels.contains(channel.getIdLong())) {
            tempChannels.remove(channel.getIdLong());
            channel.delete().queue();
        }
    }
}
