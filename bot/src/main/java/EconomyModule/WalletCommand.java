package EconomyModule;

import Commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class WalletCommand extends Command {
    final EconomyController controller;
    private EmbedBuilder eb = new EmbedBuilder();
    public WalletCommand(EconomyController economyController) {
        this.controller = economyController;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        int total = controller.getWalletTotalForUser((event.getAuthor().getId()));
        event.getChannel().sendMessage("You have " + total + "Ⱡ in your wallet.").queue();
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.setTitle("\uD83D\uDCB0  Economy Module \uD83D\uDCB0", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/EconomyModule/");
        eb.setDescription("Server Economy!");
        eb.addField("wallet", "- Shows the content of the user's wallet", true);
        eb.setFooter("DM Ugion#1917 if you have suggestions");
        eb.setColor(Color.MAGENTA);
        return eb;
    }

}
