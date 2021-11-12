package de.zrazzer.discordbot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class onSupportJoin extends ListenerAdapter {

    public List<Long> tempChannels = new ArrayList<>();
    public Long SupportChannel = 826515837174415360L; //TODO: Support Channel
    public Long Role = 817662233537806367L; //TODO: Support Rolle

    //File file = new File("C:\\Users\\Nick\\Pictures\\Discord/Test.gif");

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (event.getChannelJoined().getIdLong() == SupportChannel) {
            onJoin(event.getChannelJoined(), event.getEntity(), event.getJDA());
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        onLeave(event.getChannelLeft());
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        onLeave(event.getChannelLeft());
        onJoin(event.getChannelJoined(), event.getEntity(), event.getJDA());
    }

    public void onJoin(VoiceChannel joined, Member memb, JDA jda) {
        if (joined.getIdLong() == SupportChannel) {
            Category cat = joined.getParent();
            VoiceChannel vc = cat.createVoiceChannel("⏳ | " + memb.getEffectiveName()).complete();
            vc.getManager().setUserLimit(joined.getUserLimit()).queue();
            vc.getGuild().moveVoiceMember(memb, vc).queue();

            tempChannels.add(vc.getIdLong());

            jda.getRoleById(Role).getGuild().getMembers().forEach(user -> {
                if (!user.getUser().isBot()) {
                    if (user.getRoles().contains(jda.getRoleById(Role))) {
                        user.getUser().openPrivateChannel().queue(channel -> {
                            channel.sendMessage(memb.getEffectiveName() + " Client-ID=||" + user.getId() + "|| " + "benötigt Support!").queue();
                        });
                    }
                }
            });
            memb.getUser().openPrivateChannel().queue(channel -> {
                //channel.sendFile(file).queue();
                channel.sendMessage("Ein Teammitglied wurde benachrichtigt, bitte warte einen Moment, es wird sich gleich um dich gekümmert.").queue();
            });
        }
    }

    public void onLeave(VoiceChannel channel) {
        if (channel.getMembers().size() <= 0) {
            if (tempChannels.contains(channel.getIdLong())) {
                tempChannels.remove(channel.getIdLong());
                channel.delete().queue();
            }
        }
    }
}
