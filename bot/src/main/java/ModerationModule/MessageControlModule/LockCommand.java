package ModerationModule.MessageControlModule;

import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LockCommand extends ModCommand {
    private Permission perm = Permission.MANAGE_CHANNEL;
    public LockCommand(ModerationController modCTRL) {
        super(modCTRL);
    }
    private String helpText = "```\nlock\n```";

    @Override
    public void execute(TextChannel channel, Member member, String text, int num) {
        channel.getManager().putPermissionOverride(channel.getGuild().getRoles().get(channel.getGuild().getRoles().size()-1), 0, 2048).queue();
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
