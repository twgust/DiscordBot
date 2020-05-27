package MusicModule.MusicCommands;

import Commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Queue;

public class MusicCommand extends Command {
    private EmbedBuilder eb = new EmbedBuilder();


    @Override
    public void execute(GuildMessageReceivedEvent event) {
    }

    @Override
    public EmbedBuilder getHelp(){
        eb.clear();
        eb.setTitle("🎷 Music Module 🎷", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/MusicModule");
        eb.setDescription("Play some music!");
        eb.addField("play (song)", "- searches youtube and plays first result", true);
        eb.addField("pause", "- pauses current song", true);
        eb.addField("resume", "- resumes current song", false);
        eb.addField("queue", "- displays current song queue", true);
        eb.addField("song", "- display currently playing song", true);
        eb.addField("skip", "- skips currently playing song", true);
        eb.addField("search (song)", "- returns search results from youtube. React and play", false);
        eb.addField("Valid (song) inputs", "youtube links, search-words", false);
        eb.setFooter("DM goose#0068 if you have suggestions");
        eb.setColor(Color.getHSBColor(153,102,0));
        return eb;
    }

}
