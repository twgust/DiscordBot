package LevelModule;

import Commands.Command;
import ModerationModule.GetMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class ProfileCommand extends Command {
    private EmbedBuilder eb = new EmbedBuilder();

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        int startIndex = event.getMessage().getContentRaw().trim().indexOf(" ");
        if (startIndex != -1){
            member = GetMember.get(event.getMessage().getContentRaw().substring(startIndex).trim(), event.getChannel(), event.getMember());
        }
        eb.clear();
        eb.setTitle(LevelController.getUserInfo(event.getGuild(), member));
        eb.setColor(Color.YELLOW);
        event.getChannel().sendMessage(eb.build()).queue();
    }
    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("\uD83C\uDFC6 Level Module \uD83C\uDFC6", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/LevelModule/");
        eb.setDescription("Level system!");
        eb.addField("profile", "- Shows the user's level profile", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.YELLOW);
        return eb;
    }
}
