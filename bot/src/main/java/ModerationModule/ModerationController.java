package ModerationModule;

import Commands.Command;
import Main.Controller;
import ModerationModule.BanKickModule.BanCommand;
import ModerationModule.BanKickModule.KickCommand;
import ModerationModule.BanKickModule.UnBanCommand;
import ModerationModule.InfoModule.HelpCommand;
import ModerationModule.InfoModule.InfoCommand;
import ModerationModule.MessageControlModule.LockCommand;
import ModerationModule.MessageControlModule.MuteCommand;
import ModerationModule.MessageControlModule.PruneCommand;
import ModerationModule.MessageControlModule.UnlockCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ModerationController {
    private static TextChannel logChannel;
    private Controller ctrl;

    public ModerationController(Controller ctrl) {
        this.ctrl = ctrl;
    }

    public void execute(GuildMessageReceivedEvent event) {
        int startIndex = event.getMessage().getContentRaw().indexOf(" ");
        String key = event.getMessage().getContentRaw().substring(1,startIndex).trim();
        TextChannel channel = event.getChannel();
        Member member = null;
        Guild guild = event.getGuild();
        String[] msgContent = event.getMessage().getContentRaw().substring(startIndex).trim().split("\\s+");
        String text = "";
        int textStartIndex = 0;
        if (key.equalsIgnoreCase("setLogChannel")) {
            setLogChannel(channel);
            channel.sendMessage("Log channel set.").queue();
            return;
        }
        int num = -1;

        try{
            num = Integer.parseInt(msgContent[0]);
            textStartIndex = 1;
        }catch (Exception e1){
            if((member = GetMember.get(msgContent[0], channel, event.getMember())) != null){

                try{
                    num = Integer.parseInt(msgContent[1]);
                    textStartIndex = 2;
                }catch (Exception e2){
                    textStartIndex = 1;
                }
            }
        }
        try{
            for (int i = textStartIndex; i < msgContent.length; i++) {
                text += msgContent[i];
            }
        }catch (Exception e){}

        ((ModCommand)ctrl.getCmdMap().get(key)).execute(channel, member, text, num);
    }

    public static TextChannel getLogChannel() {
        return logChannel;
    }

    public static void setLogChannel(TextChannel newLogChannel) {
        logChannel = newLogChannel;
    }
}
