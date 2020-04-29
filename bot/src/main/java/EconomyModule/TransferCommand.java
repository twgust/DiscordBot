package EconomyModule;

import Commands.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class TransferCommand extends Command {

    final EconomyController economyController;
    public TransferCommand(EconomyController economyController) {
        this.economyController = economyController;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        User mentionedUser;
        int transferAmount;
        if (event.getMessage().getMentionedUsers().isEmpty()) {
            event.getChannel().sendMessage("You did not specify a user!").queue();
            return;
        }
        else {
            mentionedUser = event.getMessage().getMentionedUsers().get(0);
        }
        String messageRaw = event.getMessage().getContentRaw();
        String[] array = messageRaw.split(" ", 3);
        if (array.length < 3) {
            event.getChannel().sendMessage("You did not specify enough arguments! The format is: %transfer [user] [amount to transfer]").queue();
            return;
        }
        try {
            transferAmount = Integer.parseInt(array[2]);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("You did not enter a number as your second argument!").queue();
            return;
        }
        EconomyResponses response = economyController.transferToUser(event.getAuthor().getId(), mentionedUser.getId(), transferAmount);
        if (response == EconomyResponses.INSUFFICIENT_FUNDS) {
            event.getChannel().sendMessage("You do not have sufficient funds for this!").queue();
        }
        else if (response == EconomyResponses.SUCCESS) {
            event.getChannel().sendMessage("Transferred " + transferAmount + "Ⱡ to " + mentionedUser.getName()).queue();
        }
        else {
            event.getChannel().sendMessage("Unknown error").queue();
        }

    }
}

