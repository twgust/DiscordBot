package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
//git
public class SkipCommand extends Command {

    @Override
    public void execute(GuildMessageReceivedEvent event){
        Member user = event.getMessage().getMember();
        String skipMessage = event.getMessage().getContentRaw();
        TrackScheduler scheduler = new TrackScheduler();

        if(skipMessage.equals("%skip")){
            scheduler.skip(user);
        }
        event.getChannel().sendMessage("skipping...").queue();


    }
}
