package LevelModule;

import Commands.Command;
import ModerationModule.InfoModule.HelpCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RemoveLevelRoleCommand extends Command {
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
        LevelController.removeLevelRole(event.getGuild(), level);
    }

    @Override
    public Permission getPerm() {
        return perm;
    }
}
