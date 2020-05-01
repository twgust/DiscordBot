package ModerationModule.InfoModule;

import Commands.Command;
import Main.Controller;
import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HelpCommand extends ModCommand {
    private static Controller ctrl;
    private Permission perm = Permission.MESSAGE_WRITE;
    private String helpText;
    public HelpCommand(ModerationController modCTRL, Controller ctrl) {
        super(modCTRL);
        this.ctrl = ctrl;
        helpText = "```\n" + ctrl.getCmdMap().getNameList() + "\n```";
    }
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");

        event.getChannel().sendMessage(((Command)ctrl.getCmdMap().get(arguments[1].toLowerCase())).getHelp()).queue();
    }

    public static void dispHelp(String key, TextChannel channel){
        if (ctrl == null) {
            channel.sendMessage("Impossible error has occurred. Which can only mean one thing... END OF THE WORLD AS WE KNOW IT").queue();
            return;
        }
        if (ctrl.getCmdMap().containsKey(key) && ctrl.getCmdMap().get(key) instanceof Command ) {
            channel.sendMessage(((Command) ctrl.getCmdMap().get(key)).getHelp()).queue();
            return;
        }
    }

    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public String getHelp() {
        return helpText;
    }
}
