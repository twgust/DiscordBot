package ModerationModule;

import Commands.Command;
import ModerationModule.BanKickModule.BanCommand;
import ModerationModule.BanKickModule.KickCommand;
import ModerationModule.BanKickModule.UnBanCommand;
import ModerationModule.InfoModule.HelpCommand;
import ModerationModule.InfoModule.InfoCommand;
import ModerationModule.MessageControlModule.LockCommand;
import ModerationModule.MessageControlModule.MuteCommand;
import ModerationModule.MessageControlModule.PruneCommand;
import ModerationModule.MessageControlModule.UnlockCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ModerationController extends Command {
    private static TextChannel logChannel;
    private ModCommandMap modCmdMap = new ModCommandMap();

    public ModerationController(){
        addCommands();
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        int startIndex = event.getMessage().getContentRaw().indexOf(" ");
        if (startIndex == -1) {
            modCmdMap.get("help").execute(event);
            return;
        }
        String key = event.getMessage().getContentRaw().substring(1,startIndex).trim();
        String[] msgContent = event.getMessage().getContentRaw().substring(startIndex).trim().split("\\s+");
        String text = "";
        int textStartIndex = 0;

        TextChannel channel = event.getChannel();
        Member member = null;
        Guild guild = event.getGuild();

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

        if (key.equalsIgnoreCase("unban"))modCmdMap.get(key).execute(event);
        else modCmdMap.get(key).execute(channel, member, text, num);
    }

    private void addCommands() {
        modCmdMap.put("ban", new BanCommand());
        modCmdMap.put("info", new InfoCommand());
        modCmdMap.put("help", new HelpCommand());
        modCmdMap.put("kick", new KickCommand());
        modCmdMap.put("lock", new LockCommand());
        modCmdMap.put("mute", new MuteCommand());
        modCmdMap.put("prune", new PruneCommand());
        modCmdMap.put("unban", new UnBanCommand());
        modCmdMap.put("unlock", new UnlockCommand());
    }

    public static TextChannel getLogChannel() {
        return logChannel;
    }

    public static void setLogChannel(TextChannel newLogChannel) {
        logChannel = newLogChannel;
    }

    public ModCommandMap getModCmdMap() {
        return modCmdMap;
    }
}
