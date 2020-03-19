package TestModule;

import Commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import javax.annotation.Nonnull;

public class TestClass extends Command {


    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        if(event.getReactionEmote().getName().equals("➡") && !event.getMember().getUser().equals(event.getJDA().getSelfUser()) && !event.getMember().getUser().isBot() && event.getChannel().retrieveMessageById(event.getMessageId()).complete().getAuthor().getId().equals("678037870531051531")) {
            event.getReaction().removeReaction(event.getUser()).queue();
            System.out.println("testclass");
            //System.out.println(getPages()[0]);

            //event.getChannel().retrieveMessageById(event.getMessageId()).complete().editMessage(nextPage.build()).queue();
            //nextPageOfTopTracks();
        }
    }
}
