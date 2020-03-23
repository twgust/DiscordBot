package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.Music;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PlayCommand extends Command {
    @Override
    public void execute(GuildMessageReceivedEvent event){
        Guild server = event.getGuild();
        Member user = event.getMessage().getMember();
        String inDataFromUser = event.getMessage().getContentRaw();

        String array[] = inDataFromUser.split(" ", 2);
        Music music = new Music(server);
        music.loadMusic(array[1], user);



    }
}
