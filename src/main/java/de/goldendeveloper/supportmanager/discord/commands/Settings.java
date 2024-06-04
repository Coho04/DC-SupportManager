package de.goldendeveloper.supportmanager.discord.commands;

import de.goldendeveloper.dcbcore.DCBot;
import de.goldendeveloper.dcbcore.interfaces.CommandInterface;
import de.goldendeveloper.supportmanager.Main;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Settings implements CommandInterface {

    public static String getCmdSettingsSubChannel = "support-voicechannel";

    @Override
    public CommandData commandData() {
        return Commands.slash("settings", "Legt die Einstellungen für dem SupportManager fest").addSubcommands(new SubcommandData(getCmdSettingsSubChannel, "Setzt den Support Channel für den Discord Server").addOption(OptionType.CHANNEL, "channel", "Support Audio Channel"));
    }

    @Override
    public void runSlashCommand(SlashCommandInteractionEvent e, DCBot dcBot) {
        if (e.getSubcommandName() != null) {
            if (e.getSubcommandName().equalsIgnoreCase(getCmdSettingsSubChannel)) {
                VoiceChannel voiceChannel = e.getOption("channel").getAsChannel().asVoiceChannel();
                if (voiceChannel != null) {
                    try (Connection connection = Main.getMysql().getSource().getConnection()) {
                        String selectQuery = "SELECT count(*) FROM Guilds WHERE Guild = ?;";
                        PreparedStatement statement = connection.prepareStatement(selectQuery);
                        statement.execute("USE `GD-SupportManager`");
                        statement.setLong(1, e.getGuild().getIdLong());
                        try (ResultSet rs = statement.executeQuery()) {
                            if (rs.next()) {
                                String updateQuery = "UPDATE Guilds SET Support_channel = ? where Guild = ?";
                                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                                updateStatement.setLong(1, voiceChannel.getIdLong());
                                updateStatement.setLong(2, e.getGuild().getIdLong());
                                updateStatement.execute();
                                e.getInteraction().reply("Der Support Channel wurde erfolgreich gesetzt!").queue();
                            } else {

                                String insertQuery = "INSERT INTO Guilds (Montag, Dienstag, Mittwoch, Donnerstag, Freitag, Samstag, Sonntag, Guild, Support_channel) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
                                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                                insertStatement.setString(1, "");//Montag
                                insertStatement.setString(2, "");//Dienstag
                                insertStatement.setString(3, "");//Mittwoch
                                insertStatement.setString(4, "");//Donnerstag
                                insertStatement.setString(5, "");//Freitag
                                insertStatement.setString(6, "");//Samstag
                                insertStatement.setString(7, "");//Sonntag
                                insertStatement.setLong(8, e.getGuild().getIdLong());//Guild
                                insertStatement.setLong(9, voiceChannel.getIdLong());//Support Channel
                                insertStatement.execute();
                                e.getInteraction().reply("Der Support Channel wurde erfolgreich gesetzt!").queue();
                            }
                        }
                    } catch (SQLException exception) {
                        System.out.println(exception.getMessage());
                        Sentry.captureException(exception);
                    }
                }
            }
        }
    }
}
