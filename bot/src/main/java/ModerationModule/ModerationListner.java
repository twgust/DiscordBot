package ModerationModule;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

import static ModerationModule.ModerationController.getLogChannel;

public class ModerationListner extends ListenerAdapter {
    public void onGuildJoin(GuildJoinEvent joinEvent){
        joinEvent.getGuild().createRole().setName("%BotMuted").queue();
    }

    public void onRoleCreate(RoleCreateEvent roleCreateEvent){
        try {
            getLogChannel().sendMessage("Role " + roleCreateEvent.getRole().getName() + " has been created.").queue();
        } catch (Exception e) {
        }
        if (roleCreateEvent.getRole().getName().equals("%BotMuted")) roleCreateEvent.getRole().getManager().
                revokePermissions(Permission.MESSAGE_WRITE, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK).queue();
    }

    @Override
    public void onGuildBan(@Nonnull GuildBanEvent event) {
        if (getLogChannel() != null) getLogChannel().sendMessage("User " + event.getUser().getName() + " was banned.").queue();
    }
}
