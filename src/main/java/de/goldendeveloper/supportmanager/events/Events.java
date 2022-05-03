package de.goldendeveloper.supportmanager.events;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.mysql.entities.RowBuilder;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.supportmanager.CreateMysql;
import de.goldendeveloper.supportmanager.Discord;
import de.goldendeveloper.supportmanager.Main;
import de.goldendeveloper.supportmanager.utility.Runner;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Events extends ListenerAdapter {

    public List<Long> tempChannels = new ArrayList<>();
    public long Role = 817662233537806367L;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        String cmd = e.getName();
        if (cmd.equalsIgnoreCase(Discord.getCmdSettings)) {
            if (e.getSubcommandName() != null) {
                if (e.getSubcommandName().equalsIgnoreCase(Discord.getCmdSettingsSubChannel)) {
                    VoiceChannel voiceChannel = e.getOption("channel").getAsVoiceChannel();
                    if (voiceChannel != null) {
                        if (Main.getCreateMysql().getMysql().existsDatabase(CreateMysql.dbName)) {
                            if (Main.getCreateMysql().getMysql().getDatabase(CreateMysql.dbName).existsTable(CreateMysql.TableGuilds)) {
                                Table table = Main.getCreateMysql().getMysql().getDatabase(CreateMysql.dbName).getTable(CreateMysql.TableGuilds);
                                if (table.existsColumn(CreateMysql.colGuild)) {
                                    if (table.getColumn(CreateMysql.colGuild).getAll().contains(e.getGuild().getId())) {
                                        table.getRow(table.getColumn(CreateMysql.colGuild), e.getGuild().getId()).set(table.getColumn(CreateMysql.colSupChannel), e.getGuild().getId());
                                        e.getInteraction().reply("Der Support Channel wurde erfolgreich gesetzt!").queue();
                                    } else {
                                        table.insert(new RowBuilder()
                                                .with(table.getColumn(CreateMysql.colSupChannel), voiceChannel.getId())
                                                .with(table.getColumn(CreateMysql.colGuild), e.getGuild().getId())
                                                .with(table.getColumn(CreateMysql.colMon), "")
                                                .with(table.getColumn(CreateMysql.colDie), "")
                                                .with(table.getColumn(CreateMysql.colMit), "")
                                                .with(table.getColumn(CreateMysql.colDon), "")
                                                .with(table.getColumn(CreateMysql.colFre), "")
                                                .with(table.getColumn(CreateMysql.colSam), "")
                                                .with(table.getColumn(CreateMysql.colSon), "")
                                                .build()
                                        );
                                        e.getInteraction().reply("Der Support Channel wurde erfolgreich gesetzt!").queue();
                                    }
                                }
                            }
                        }
                    }
                } else if (e.getSubcommandName().equalsIgnoreCase(Discord.getCmdSettingsSubShutdown)) {
                    User user = e.getUser();
                    User coho = e.getJDA().getUserById("513306244371447828");
                    User zrazzer = e.getJDA().getUserById("428811057700536331");
                    if (!user.isBot() && !user.isSystem()) {
                        if (user == coho || user == zrazzer) {
                            Main.getDiscord().getBot().shutdown();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent e) {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
        embed.setAuthor(new WebhookEmbed.EmbedAuthor(Main.getDiscord().getBot().getSelfUser().getName(), Main.getDiscord().getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
        embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "OFFLINE"));
        embed.setColor(0xFF0000);
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", Main.getDiscord().getBot().getSelfUser().getAvatarUrl()));
        new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build());
    }


    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (Main.getCreateMysql().getMysql().existsDatabase(CreateMysql.dbName)) {
            if (Main.getCreateMysql().getMysql().getDatabase(CreateMysql.dbName).existsTable(CreateMysql.TableGuilds)) {
                Table table = Main.getCreateMysql().getMysql().getDatabase(CreateMysql.dbName).getTable(CreateMysql.TableGuilds);
                if (table.existsColumn(CreateMysql.colSupChannel)) {
                    if (table.getColumn(CreateMysql.colSupChannel).getAll().contains(event.getChannelJoined().getId())) {
                        onJoin(event.getChannelJoined(), event.getEntity());
                    }
                }
            }
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        onLeave(event.getChannelLeft());
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        onLeave(event.getChannelLeft());
        onJoin(event.getChannelJoined(), event.getEntity());
    }


    public static long VoiceChannel = 2123123123123123L;
    public static Role role = Main.getDiscord().getBot().getRoleById("<ID>");
    public static long DiscordServer = 123456789L;

    public static void run() {
        Calendar timeOfDay = Calendar.getInstance();
        timeOfDay.set(Calendar.HOUR_OF_DAY, 15);
        timeOfDay.set(Calendar.MINUTE, 11);
        timeOfDay.set(Calendar.SECOND, 0);

        new Runner(timeOfDay, () -> {
            try {
                VoiceChannel channel = Main.getDiscord().getBot().getVoiceChannelById(VoiceChannel);
                if (channel != null) {
                    channel.upsertPermissionOverride(role).setAllow(Permission.VOICE_CONNECT).queue();
                    channel.getManager().setName("✅Support: Geöffnet✅").putPermissionOverride(role, Arrays.asList(Permission.VOICE_CONNECT), null).submit();
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
                VoiceChannel channel = Main.getDiscord().getBot().getGuildById(DiscordServer).getVoiceChannelById(VoiceChannel);
                if (channel != null) {
                    channel.upsertPermissionOverride(role).deny(Permission.VOICE_CONNECT).queue();
                    channel.getManager().setName("⛔Support: Geschlossen⛔").putPermissionOverride(role, null, Arrays.asList(Permission.VOICE_CONNECT)).submit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "daily-support-close");
    }

    public void onJoin(AudioChannel joined, Member member) {
        if (Main.getCreateMysql().getMysql().existsDatabase(CreateMysql.dbName)) {
            if (Main.getCreateMysql().getMysql().getDatabase(CreateMysql.dbName).existsTable(CreateMysql.TableGuilds)) {
                Table table = Main.getCreateMysql().getMysql().getDatabase(CreateMysql.dbName).getTable(CreateMysql.TableGuilds);
                if (table.existsColumn(CreateMysql.colSupChannel)) {
                    if (table.getColumn(CreateMysql.colSupChannel).getAll().contains(joined.getId())) {
                        VoiceChannel ch = joined.getJDA().getVoiceChannelById(joined.getId());
                        if (ch != null) {
                            Category cat = ch.getParentCategory();
                            if (cat != null) {
                                VoiceChannel vc = cat.createVoiceChannel("⏳ | " + member.getEffectiveName()).complete();
                                vc.getManager().setUserLimit(ch.getUserLimit()).queue();
                                vc.getGuild().moveVoiceMember(member, vc).queue();
                                tempChannels.add(vc.getIdLong());
                                member.getJDA().getRoleById(Role).getGuild().getMembers().forEach(user -> {
                                    if (!user.getUser().isBot()) {
                                        if (user.getRoles().contains(member.getJDA().getRoleById(Role))) {
                                            user.getUser().openPrivateChannel().queue(channel -> {
                                                channel.sendMessage(member.getEffectiveName() + " Client-ID=||" + user.getId() + "|| " + "benötigt Support!").queue();
                                            });
                                        }
                                    }
                                });
                                member.getUser().openPrivateChannel().queue(channel -> {
                                    //channel.sendFile(new File("C:\\Users\\Nick\\Pictures\\Discord/Test.gif")).queue();
                                    channel.sendMessage("Ein Teammitglied wurde benachrichtigt, bitte warte einen Moment, es wird sich gleich um dich gekümmert.").queue();
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    public void onLeave(AudioChannel channel) {
        if (channel.getMembers().size() <= 0) {
            if (tempChannels.contains(channel.getIdLong())) {
                tempChannels.remove(channel.getIdLong());
                channel.delete().queue();
            }
        }
    }
}
