package LevelModule;

import Commands.Command;
import Main.Controller;
import ModerationModule.InfoModule.HelpCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class AddLevelRoleCommand extends Command {
    private Permission perm = Permission.ADMINISTRATOR;
    private EmbedBuilder eb = new EmbedBuilder();
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().substring(1).trim().split("\\s+");
        int level;
        try { level = Integer.parseInt(arguments[1]); }
        catch (Exception e){
            HelpCommand.dispHelp(arguments[0], event.getChannel());
            return;
        }
        if (arguments.length < 3){
            HelpCommand.dispHelp(arguments[0], event.getChannel());
            return;
        }
        String roleId = arguments[2].substring(3,arguments[2].length()-1);
        Role levelRole;
        try { levelRole = event.getGuild().getRoleById(roleId); }
        catch (Exception e){
            event.getChannel().sendMessage("I was unable to find that role").queue();
            return;
        }
        if (LevelController.addLevelRole(event.getGuild(), level, levelRole)) event.getChannel().sendMessage(
                "Role " + levelRole.getName() + " was added as the reward for reaching level " + level + ".").queue();
        else event.getChannel().sendMessage(
                "I was unable to add the role " + levelRole.getName() + " as the reward for reaching level " + level + ".").queue();
        }

    @Override
    public Permission getPerm() {
        return perm;
    }

    @Override
    public EmbedBuilder getHelp() {
        eb.setTitle("\uD83C\uDFC6 Level Module \uD83C\uDFC6", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/LevelModule/");
        eb.setDescription("Level system!");
        eb.addField("profile", "- Shows the user's level profile", true);
        eb.addField("Placeholder", "- Add special level roles", true);
        eb.setFooter("DM wiz#8158 if you have suggestions");
        eb.setColor(Color.white);
        return eb;
    }
}
