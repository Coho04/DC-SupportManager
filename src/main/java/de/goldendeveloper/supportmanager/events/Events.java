package de.goldendeveloper.supportmanager.events;

import de.goldendeveloper.supportmanager.Main;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            try (Connection connection = Main.getMysql().getSource().getConnection()) {
                String selectQuery = "SELECT count(*) FROM Guilds WHERE Support_channel = ?;";
                PreparedStatement statement = connection.prepareStatement(selectQuery);
                statement.execute("USE `GD-SupportManager`");
                statement.setLong(1, event.getChannelJoined().getIdLong());
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        onJoin(event.getChannelJoined(), event.getEntity());
                    }
                }
            } catch (SQLException exception) {
                System.out.println(exception.getMessage());
                Sentry.captureException(exception);
            }
        } else if (event.getChannelJoined() == null && event.getChannelLeft() != null) { // Leave Event
            onLeave(event.getChannelLeft());
        }
    }


    public void onJoin(AudioChannel joined, Member member) {
        try (Connection connection = Main.getMysql().getSource().getConnection()) {
            String selectQuery = "SELECT count(*) FROM Guilds WHERE Support_channel = ?;";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.execute("USE `GD-SupportManager`");
            statement.setLong(1, joined.getIdLong());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
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
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            Sentry.captureException(exception);
        }
    }

    public void onLeave(AudioChannel channel) {
        if (channel.getMembers().isEmpty() && tempChannels.contains(channel.getIdLong())) {
            tempChannels.remove(channel.getIdLong());
            channel.delete().queue();
        }
    }
}
