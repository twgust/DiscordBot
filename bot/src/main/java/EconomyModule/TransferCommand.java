package EconomyModule;

import Commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class TransferCommand extends Command {
    private EmbedBuilder eb = new EmbedBuilder();
    final EconomyController economyController;
    public TransferCommand(EconomyController economyController) {
        this.economyController = economyController;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        User mentionedUser;
        int transferAmount;
        if (event.getMessage().getMentionedUsers().isEmpty()) {
            eb.clear();
            eb.setTitle("You did not specify a user!");
            eb.setColor(Color.YELLOW);
            event.getChannel().sendMessage(eb.build()).queue();
            return;
        }
        else {
            mentionedUser = event.getMessage().getMentionedUsers().get(0);
        }
        if (event.getAuthor().getId().equals(mentionedUser.getId())) {
            eb.clear();
            eb.setTitle("You can't transfer money to yourself!");
            eb.setColor(Color.YELLOW);
            event.getChannel().sendMessage(eb.build()).queue();
            return;
        }
        String messageRaw = event.getMessage().getContentRaw();
        String[] array = messageRaw.split(" ", 3);
        if (array.length < 3) {
            eb.clear();
            eb.setTitle("You did not specify enough arguments! The format is: %transfer [user] [amount to transfer]");
            eb.setColor(Color.YELLOW);
            event.getChannel().sendMessage(eb.build()).queue();
            return;
        }
        try {
            transferAmount = Integer.parseInt(array[2]);
        } catch (NumberFormatException e) {
            eb.clear();
            eb.setTitle("You did not enter a number as your second argument!");
            eb.setColor(Color.YELLOW);
            event.getChannel().sendMessage(eb.build()).queue();
            return;
        }
        if (transferAmount <= 0) {
            eb.clear();
            eb.setTitle("You can't transfer 0 or a negative amount!");
            eb.setColor(Color.YELLOW);
            event.getChannel().sendMessage(eb.build()).queue();
            return;
        }
        EconomyResponses response = economyController.transferToUser(event.getAuthor().getId(), mentionedUser.getId(), transferAmount);
        if (response == EconomyResponses.INSUFFICIENT_FUNDS) {
            eb.clear();
            eb.setTitle("You do not have sufficient funds for this!");
            eb.setColor(Color.YELLOW);
            event.getChannel().sendMessage(eb.build()).queue();
        }
        else if (response == EconomyResponses.SUCCESS) {
            eb.clear();
            eb.setTitle("Transferred " + transferAmount + "Ⱡ to " + mentionedUser.getName());
            eb.setColor(Color.YELLOW);
            event.getChannel().sendMessage(eb.build()).queue();
        }
        else {
            eb.clear();
            eb.setTitle("Unknown error");
            eb.setColor(Color.YELLOW);
            event.getChannel().sendMessage(eb.build()).queue();
        }

    }
}

