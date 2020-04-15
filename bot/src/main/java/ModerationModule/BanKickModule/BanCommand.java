package ModerationModule.BanKickModule;

import ModerationModule.ModCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BanCommand extends ModCommand {

    @Override
    public void execute(TextChannel channel, Member member, String text, int num){

        channel.sendMessage("Member " + member.getUser().getName() + " was banned.").queue();

        member.ban(num, text).queue();

    }
}
