package de.goldendeveloper.supportmanager.events;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.mysql.entities.RowBuilder;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.supportmanager.MysqlConnection;
import de.goldendeveloper.supportmanager.Discord;
import de.goldendeveloper.supportmanager.Main;
import de.goldendeveloper.supportmanager.utility.Runner;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Events extends ListenerAdapter {

    public List<Long> tempChannels = new ArrayList<>();
    public long Role = 817662233537806367L;

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        Main.getServerCommunicator().addServer(e.getGuild().getId());
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        Main.getServerCommunicator().removeServer(e.getGuild().getId());
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        String cmd = e.getName();
        if (cmd.equalsIgnoreCase(Discord.getCmdSettings)) {
            if (e.getSubcommandName() != null) {
                if (e.getSubcommandName().equalsIgnoreCase(Discord.getCmdSettingsSubChannel)) {
                    VoiceChannel voiceChannel = e.getOption("channel").getAsChannel().asVoiceChannel();
                    if (voiceChannel != null) {
                        if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
                            if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.TableGuilds)) {
                                Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.TableGuilds);
                                if (table.existsColumn(MysqlConnection.colGuild)) {
                                    if (table.getColumn(MysqlConnection.colGuild).getAll().getAsString().contains(e.getGuild().getId())) {
                                        table.getRow(table.getColumn(MysqlConnection.colGuild), e.getGuild().getId()).set(table.getColumn(MysqlConnection.colSupChannel), e.getGuild().getId());
                                        e.getInteraction().reply("Der Support Channel wurde erfolgreich gesetzt!").queue();
                                    } else {
                                        table.insert(new RowBuilder()
                                                .with(table.getColumn(MysqlConnection.colSupChannel), voiceChannel.getId())
                                                .with(table.getColumn(MysqlConnection.colGuild), e.getGuild().getId())
                                                .with(table.getColumn(MysqlConnection.colMon), "")
                                                .with(table.getColumn(MysqlConnection.colDie), "")
                                                .with(table.getColumn(MysqlConnection.colMit), "")
                                                .with(table.getColumn(MysqlConnection.colDon), "")
                                                .with(table.getColumn(MysqlConnection.colFre), "")
                                                .with(table.getColumn(MysqlConnection.colSam), "")
                                                .with(table.getColumn(MysqlConnection.colSon), "")
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
        if (Main.getDeployment()) {
            WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
            embed.setAuthor(new WebhookEmbed.EmbedAuthor(Main.getDiscord().getBot().getSelfUser().getName(), Main.getDiscord().getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
            embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "Offline"));
            embed.addField(new WebhookEmbed.EmbedField(false, "Gestoppt als", Main.getDiscord().getBot().getSelfUser().getName()));
            embed.addField(new WebhookEmbed.EmbedField(false, "Server", Integer.toString(Main.getDiscord().getBot().getGuilds().size())));
            embed.addField(new WebhookEmbed.EmbedField(false, "Status", "\uD83D\uDD34 Offline"));
            embed.addField(new WebhookEmbed.EmbedField(false, "Version", Main.getConfig().getProjektVersion()));
            embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", Main.getDiscord().getBot().getSelfUser().getAvatarUrl()));
            embed.setTimestamp(new Date().toInstant());
            embed.setColor(0xFF0000);
            new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build()).thenRun(() -> System.exit(0));
        }
    }


    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null && event.getChannelLeft() != null) { //Move Event
            onLeave(event.getChannelLeft());
            onJoin(event.getChannelJoined(), event.getEntity());
        } else if (event.getChannelJoined() != null && event.getChannelLeft() == null) {// Join Event
            if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
                if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.TableGuilds)) {
                    Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.TableGuilds);
                    if (table.existsColumn(MysqlConnection.colSupChannel)) {
                        if (table.getColumn(MysqlConnection.colSupChannel).getAll().getAsString().contains(event.getChannelJoined().getId())) {
                            onJoin(event.getChannelJoined(), event.getEntity());
                        }
                    }
                }
            }
        } else if (event.getChannelJoined() == null && event.getChannelLeft() != null) { // Leave Event
            onLeave(event.getChannelLeft());
        }
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
                VoiceChannel channel = Main.getDiscord().getBot().getGuildById(DiscordServer).getVoiceChannelById(VoiceChannel);
                if (channel != null) {
                    channel.upsertPermissionOverride(role).deny(Permission.VOICE_CONNECT).queue();
                    channel.getManager().setName("⛔Support: Geschlossen⛔").putPermissionOverride(role, null, List.of(Permission.VOICE_CONNECT)).submit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "daily-support-close");
    }

    public void onJoin(AudioChannel joined, Member member) {
        if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
            if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.TableGuilds)) {
                Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.TableGuilds);
                if (table.existsColumn(MysqlConnection.colSupChannel)) {
                    if (table.getColumn(MysqlConnection.colSupChannel).getAll().getAsString().contains(joined.getId())) {
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
