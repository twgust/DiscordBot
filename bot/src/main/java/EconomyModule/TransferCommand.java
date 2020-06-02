package EconomyModule;

import Commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class TransferCommand extends Command {

    final EconomyController economyController;
    EmbedBuilder eb = new EmbedBuilder();
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
        if (event.getAuthor().getId().equals(mentionedUser.getId())) {
            event.getChannel().sendMessage(buildError("You can't transfer money to yourself!")).queue();
            return;
        }
        String messageRaw = event.getMessage().getContentRaw();
        String[] array = messageRaw.split(" ", 3);
        if (array.length < 3) {
            event.getChannel().sendMessage(buildError("You did not specify enough arguments! The format is: %transfer [user] [amount to transfer]")).queue();
            return;
        }
        try {
            transferAmount = Integer.parseInt(array[2]);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(buildError("You did not enter a number as your second argument!")).queue();
            return;
        }
        if (transferAmount <= 0) {
            event.getChannel().sendMessage(buildError("You can't transfer 0 or a negative amount!")).queue();
            return;
        }
        EconomyResponses response = economyController.transferToUser(event.getAuthor().getId(), mentionedUser.getId(), transferAmount);
        if (response == EconomyResponses.INSUFFICIENT_FUNDS) {
            event.getChannel().sendMessage(buildError("You do not have enough funds for this tranfer!")).queue();
        }
        else if (response == EconomyResponses.SUCCESS) {
            eb.clear();
            eb.setTitle("Succesful transfer");
            eb.addField("", "Transferred " + transferAmount +"Ⱡ to" + mentionedUser, true);
            eb.setColor(Color.MAGENTA);
            event.getChannel().sendMessage(eb.build()).queue();
        }
        else {
            event.getChannel().sendMessage(buildError("Unknown error")).queue();
        }

    }
    private MessageEmbed buildError(String errorString) {
        eb.clear();
        eb.setTitle("Error!");
        eb.addField("", errorString, true);
        eb.setFooter("DM Ugion#1917 for support.");
        eb.setColor(Color.MAGENTA);
        return eb.build();
    }
}

