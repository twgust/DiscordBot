package LastfmModule;

import Commands.Command;
import Main.EventListener;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestEventWaiter extends Command {

    private static final String emoji = "\u0033\uFE0F\u20E3";
    private EventWaiter waiter;
    private boolean active = true;

    public TestEventWaiter(EventWaiter waiter) {
        this.waiter = waiter;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        active = true;
        if (event.getMessage().getContentRaw().equalsIgnoreCase("%test")) {
            handle(event);
        }
    }

    public void handle(GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        long channelId = channel.getIdLong();

        event.getChannel().sendMessage("React").queue(message -> {
            message.addReaction("\u0033\uFE0F\u20E3").queue();
            initWaiter(message.getIdLong(), channelId, channel);
        });

    }

    private void initWaiter(long messageId, long channelId, MessageChannel channel) {

        waiter.waitForEvent(MessageReactionAddEvent.class, event -> {
                    System.out.println("test");
                    MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
                    System.out.println(reactionEmote.getName());
                    User user = event.getUser();
                    System.out.println(!user.isBot());
                    System.out.println(event.getMessageIdLong() == messageId);
                    System.out.println(emoji.equals(reactionEmote.getName()));
                    boolean xd = !user.isBot() && event.getMessageIdLong() == messageId && emoji.equals(reactionEmote.getName());
                    System.out.println(xd);
                    return xd;
                },
                (event) -> {

                    User user = event.getUser();
                    channel.sendMessage(user.getName() + " was first").queue();
                    event.getReaction().removeReaction(user).queue();
                    initWaiter(messageId, channelId, channel);

                }, 10, TimeUnit.SECONDS, () -> {
                    channel.sendMessage("stopped listening dab xd").queue();
                    active = false;
                });

    }

    public void renderNewMessage(){

    }
}
