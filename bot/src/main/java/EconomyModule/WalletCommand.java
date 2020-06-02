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
        eb.clear();
        eb.setAuthor(event.getAuthor().getName() + "'s wallet", "https://github.com/twgust/DiscordBot", event.getAuthor().getAvatarUrl());
        eb.addField("Total",Integer.toString(total) + "Ⱡ", true);
        eb.setColor(Color.YELLOW);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("Economy Module \uD83D\uDCB0", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/EconomyModule/");
        eb.setDescription("Server Economy!");
        eb.addField("wallet", "- Shows the content of the user's wallet", true);
        eb.setFooter("DM Ugion#1917 if you have suggestions");
        eb.setColor(Color.MAGENTA);
        return eb;
    }

}
