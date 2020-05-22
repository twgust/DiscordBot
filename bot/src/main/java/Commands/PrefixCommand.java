package Commands;

import Main.EventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class PrefixCommand extends Command {
    private EmbedBuilder eb = new EmbedBuilder();

    @Override
    public void execute(GuildMessageReceivedEvent event) {
       eb.clear();
       String receivedMessage = event.getMessage().getContentRaw().substring(8);
       Member member = event.getMember();


        if (member != null) {
            if (member.hasPermission(Permission.ADMINISTRATOR) ||  member.getUser().getId().equalsIgnoreCase("110372734118174720")) {
                if (receivedMessage.length() > 1) {
                    eb.setTitle("**Prefix has to be __1__ letter**");
                    event.getChannel().sendMessage(eb.build()).queue();
                } else if (receivedMessage.length() == 0) {
                    eb.setTitle("**You didn't enter a prefix**");
                    event.getChannel().sendMessage(eb.build()).queue();
                } else {
                    EventListener.setPrefix(receivedMessage.charAt(0));
                    eb.setTitle("**The prefix is now '" + receivedMessage + "'**");
                    event.getChannel().sendMessage(eb.build()).queue();
                }
            }
            else {
                eb.setTitle("**You do not have these privileges**");
                event.getChannel().sendMessage(eb.build()).queue();
            }
        }
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.setTitle("\uD83D\uDD11 Commands Module - Prefix \uD83D\uDD11", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/Commands/");
        eb.setDescription("Prefix changer!");
        eb.addField("prefix (character)", "- Sets the current prefix to the typed character", true);
        eb.addField("Valid inputs", "Single character, preferably a common character", false);
        eb.setFooter("wiz#8158 if you have suggestions");
        eb.setColor(Color.blue);
        return eb;
    }
}
