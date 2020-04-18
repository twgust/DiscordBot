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
    private String helpText = "```\nkick [user] [reason]\n```";
    public KickCommand(ModerationController modCTRL) {
        super(modCTRL);
    }

    @Override
    public void execute(TextChannel channel, Member member, String text, int num){
        channel.sendMessage("User " + member.getUser().getName() + " was kicked").queue();
        if (getLogChannel() != null) getLogChannel().sendMessage("User " + member.getUser().getName() + " was kicked").queue();
        member.kick(text).queue();
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
