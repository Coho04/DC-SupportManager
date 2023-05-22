package de.goldendeveloper.supportmanager.discord.commands;

import de.goldendeveloper.dcbcore.DCBot;
import de.goldendeveloper.dcbcore.interfaces.CommandInterface;
import de.goldendeveloper.mysql.entities.RowBuilder;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.supportmanager.Main;
import de.goldendeveloper.supportmanager.MysqlConnection;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Settings implements CommandInterface {

    public static String getCmdSettings = "settings";
    public static String getCmdSettingsSubChannel = "support-voicechannel";

    @Override
    public CommandData commandData() {
        return Commands.slash("settings", "Legt die Einstellungen für dem SupportManager fest")
                .addSubcommands(
                        new SubcommandData(getCmdSettingsSubChannel, "Setzt den Support Channel für den Discord Server").addOption(OptionType.CHANNEL, "channel", "Support Audio Channel")
                        );
    }

    @Override
    public void runSlashCommand(SlashCommandInteractionEvent e, DCBot dcBot) {
        if (e.getSubcommandName() != null) {
            if (e.getSubcommandName().equalsIgnoreCase(getCmdSettingsSubChannel)) {
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
            }
        }
    }
}
