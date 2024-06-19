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

/**
 * This class handles events related to voice channels in a guild.
 */
public class Events extends ListenerAdapter {

    /**
     * List of temporary channels created by the bot.
     */
    private final List<Long> tempChannels = new ArrayList<>();

    /**
     * Role ID for the role to be notified when a member joins a support channel.
     */
    private final long Role = 817662233537806367L;

    /**
     * This method is triggered when a member joins, leaves or moves between voice channels in a guild.
     *
     * @param event The event that triggered this method.
     */
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

    /**
     * This method is triggered when a member joins a voice channel.
     * It checks if the joined channel is a support channel and if so, creates a new temporary channel for the member,
     * moves the member to the new channel and sends a notification to all members with the specified role.
     *
     * @param joined The channel that the member joined.
     * @param member The member that joined the channel.
     */
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

    /**
     * This method is triggered when a member leaves a voice channel.
     * It checks if the left channel is a temporary channel and if so, deletes the channel.
     *
     * @param channel The channel that the member left.
     */
    public void onLeave(AudioChannel channel) {
        if (channel.getMembers().isEmpty() && tempChannels.contains(channel.getIdLong())) {
            tempChannels.remove(channel.getIdLong());
            channel.delete().queue();
        }
    }
}
