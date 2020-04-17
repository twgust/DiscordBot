package ModerationModule.MessageControlModule;

import Commands.Command;
import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UnlockCommand extends ModCommand {
    private Permission perm = Permission.MANAGE_CHANNEL;
    public UnlockCommand(ModerationController modCTRL) {
        super(modCTRL);
    }
    private String helpText = "```\nunlock\n```";

    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        channel.getManager().putPermissionOverride(channel.getGuild().getRoles().get(channel.getGuild().getRoles().size()-1), 2048, 0).queue();
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