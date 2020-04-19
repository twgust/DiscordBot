package EconomyModule;

import Commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WalletCommand extends Command {
    EconomyController controller;
    public WalletCommand(EconomyController economyController) {
        this.controller = economyController;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        int total = controller.getWalletTotalForUser((event.getAuthor().getId()));
        event.getChannel().sendMessage("You have " + total + "Ⱡ in your wallet.").queue();
    }
}
