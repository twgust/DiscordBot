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
    private Controller ctrl;
    private Permission perm = Permission.MESSAGE_WRITE;
    public HelpCommand(ModerationController modCTRL, Controller ctrl) {
        super(modCTRL);
        this.ctrl = ctrl;
    }

    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        channel.sendMessage(((Command)ctrl.getCmdMap().get(text.toLowerCase())).getHelp()).queue();
    }
    @Override
    public Permission getPerm() {
        return perm;
    }
}
