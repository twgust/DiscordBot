package ModerationModule.BanKickModule;

import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import static ModerationModule.ModerationController.getLogChannel;

public class KickCommand extends ModCommand {
    private Permission perm = Permission.KICK_MEMBERS;
    public KickCommand(ModerationController modCTRL) {
        super(modCTRL);
    }

    @Override
    public void execute(TextChannel channel, Member member, String text, int num){
        channel.sendMessage("User " + member.getUser().getName() + "was kicked");
        if (getLogChannel() != null) getLogChannel().sendMessage("User " + member.getUser().getName() + "was kicked");
        member.kick(text);
    }

    public Permission getPerm() {
        return perm;
    }
}
