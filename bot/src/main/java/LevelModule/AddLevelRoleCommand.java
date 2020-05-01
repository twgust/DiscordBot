package LevelModule;

import Commands.Command;
import Main.Controller;
import ModerationModule.InfoModule.HelpCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AddLevelRoleCommand extends Command {
    private Permission perm = Permission.ADMINISTRATOR;
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
}
