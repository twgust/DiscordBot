package ModerationModule.BanKickModule;

import ModerationModule.ModCommand;
import ModerationModule.ModerationController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ModerationModule.ModerationController.getLogChannel;

public class UnBanCommand extends ModCommand {
    private Permission perm = Permission.BAN_MEMBERS;
    private EmbedBuilder eb = new EmbedBuilder();

    public UnBanCommand(ModerationController modCTRL) {
        super(modCTRL);
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        List<Guild.Ban> bannedUsers = event.getGuild().retrieveBanList().complete();
        String userName = event.getMessage().getContentRaw().substring(7);
        List<User> unBanUser = new ArrayList<>();
        for (int i = 0; i < bannedUsers.size(); i++) {
            try {
                if (bannedUsers.get(i).getUser().getIdLong() == Long.parseLong(userName)) {
                    unBanUser.add(bannedUsers.get(i).getUser());
                    break;
                }
            } catch (Exception e) {
                if (bannedUsers.get(i).getUser().getName().toLowerCase().indexOf(userName.toLowerCase()) != -1) {
                    unBanUser.add(bannedUsers.get(i).getUser());
                }
            }
        }
        if (unBanUser.size() == 0){
            event.getChannel().sendMessage("User not found. Command cancelled.").queue();
            return;
        } else if (unBanUser.size() != 1)  {
            String multipleMembersMsg = "Multiple members found. Please choose one of the following, or type cancel.";
            for (int i = 0; i < unBanUser.size(); i++) {
                multipleMembersMsg += "\n" + i + ". " + unBanUser.get(i).getName();
            }

            event.getChannel().sendMessage(multipleMembersMsg).complete();
            long time = System.currentTimeMillis() + 10000;
            int userChoice = -1;
            while (time > System.currentTimeMillis() && userChoice == -1) {
                Message msg = event.getChannel().getHistory().retrievePast(1).complete().get(0);
                if (msg.getMember().equals(event.getMember())) {
                    try {
                        userChoice = Integer.parseInt(msg.getContentRaw());
                    } catch (NumberFormatException nfe) {
                        if (msg.getContentRaw().equalsIgnoreCase("cancel")){
                            userChoice = -2;
                        }
                    }
                }
            }
            if (userChoice > -1 && userChoice < unBanUser.size()){
                unBanUser.add(0, unBanUser.get(userChoice));
            }else {
                event.getChannel().sendMessage("Command cancelled.").queue();
                return;
            }
        }
        event.getChannel().sendMessage("User " + unBanUser.get(0).getName() + " was unbanned.").queue();
        if (getLogChannel() != null)
            getLogChannel().sendMessage("User " + unBanUser.get(0).getName() + " was unbanned.").queue();
        event.getGuild().unban(unBanUser.get(0)).queue();
    }
    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("\uD83D\uDE07 Moderation Module - UnBan \uD83D\uDE07", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/ModerationModule/BanKickModule");
        eb.setDescription("UnBan Users!");
        eb.addField("UnBan [user]", "- UnBans user from the server", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.getHSBColor(102,0,153));
        return eb;
    }
}
