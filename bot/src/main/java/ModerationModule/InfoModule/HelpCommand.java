package ModerationModule.InfoModule;

import Commands.Command;
import Main.Controller;
import Main.EventListener;
import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class HelpCommand extends ModCommand {
    private static Controller ctrl;
    private Permission perm = Permission.MESSAGE_WRITE;
    private EmbedBuilder eb = new EmbedBuilder();

    public HelpCommand(ModerationController modCTRL, Controller ctrl) {
        super(modCTRL);
        this.ctrl = ctrl;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");

        event.getChannel().sendMessage(((Command)ctrl.getCmdMap().get(arguments[1].toLowerCase())).getHelp().build()).queue();
    }

    public static void dispHelp(String key, TextChannel channel){
        if (ctrl == null) {
            channel.sendMessage("Impossible error has occurred. Which can only mean one thing... END OF THE WORLD AS WE KNOW IT").queue();
            return;
        }
        if (ctrl.getCmdMap().containsKey(key) && ctrl.getCmdMap().get(key) instanceof Command ) {
            channel.sendMessage(((Command) ctrl.getCmdMap().get(key)).getHelp().build()).queue();
            return;
        }
    }

    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("Moderation Module - Help \uD83E\uDDAE", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/ModerationModule/InfoModule");
        eb.setDescription("List of all commands! \n" +
                "Type %<command> to get more help for selected command!\n" +
                "Example: %weather"+
                "\nModeration:\n"+
                "`ban`\n" +
                "`unban`\n" +
                "`kick`\n"+
                "`prune`\n" +
                "`mute`\n"+
                "`lock`\n"+
                "`unlock`\n"+
                "`info`\n"+
                "`addlevelrole`\n"+
                "`prefix`"+
                "\nEntertainment:\n"+
                "`music`\n"+
                "`fm`\n"+
                "`quiz`" +
                "\nInformation\n"+
                "`weather`\n"+
                "`profile`\n");
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.YELLOW);
        return eb;
    }
}
