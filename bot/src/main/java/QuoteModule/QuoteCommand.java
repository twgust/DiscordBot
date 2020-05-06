package QuoteModule;

import Commands.Command;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class QuoteCommand extends Command {
    private String helpText = "No help available for this command.";
    private TextChannel channel;
    private QuoteSQLConnector dbConnector = new QuoteSQLConnector();

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        if(event.getGuild().getOwner().getUser() == event.getAuthor()) {
            channel = event.getChannel();
            String[] subCommands = event.getMessage().getContentRaw().split(" ");
            if (subCommands.length > 1) {
                String subCommand = subCommands[1];
                switch (subCommand) {
                    case "add":
                        String userId;
                        String quote = "";
                        for (int i = 2; i < subCommands.length - 1; i++) {
                            quote += subCommands[i] + " ";
                        }
                        String[] subString = subCommands[subCommands.length - 1].split("<");
                        quote += subString[0];
                        System.out.println("Nbr of sub strings after split at '<': " + subString.length +
                                ", Hopefully a userId: " + subCommands[subCommands.length - 1]);
                        System.out.println("Quote: "+ quote);
                        break;
                    case "remove":
                        break;
                    case "list":
                        break;
                    default:
                        System.out.println(subCommand);

                }
            }
        }
    }

    @Override
    public String getHelp() {
        return helpText;
    }
}
