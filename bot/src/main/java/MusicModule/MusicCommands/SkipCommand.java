package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
//git
public class SkipCommand extends Command {

    @Override
    public void execute(GuildMessageReceivedEvent event){


        TrackScheduler scheduler = new TrackScheduler();

        String skipMessage = event.getMessage().getContentRaw();
        if(skipMessage.equals("skip")){
            scheduler.skip();
        }
        event.getChannel().sendMessage("skipping...").queue();


    }
}
