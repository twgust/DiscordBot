package ModerationModule.BanKickModule;

import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BanCommand extends ModCommand {
    private Permission perm = Permission.BAN_MEMBERS;
    private String helpText = "ban [user] reason";

    public BanCommand(ModerationController modCTRL) {
        super(modCTRL);
    }

    @Override
    public void execute(TextChannel channel, Member member, String text, int num){


        channel.sendMessage("Member " + member.getUser().getName() + " was banned.").queue();

        member.ban(num, text).queue();

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
