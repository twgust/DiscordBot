package LevelModule;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GuildMap {
    private Map<Guild, MemberMap> guildMap = Collections.synchronizedMap(new HashMap());

    public void put(Guild guild) {
        guildMap.put(guild, new MemberMap());
        LevelController.writeToDisk();
    }

    public MemberMap get(Guild guild) {
        return guildMap.get(guild);
    }

    public UserLevel getUserLevel(Guild guild, Member member) {
        return get(guild).get(member);
    }

    public boolean containsKey(Guild guild) {
        return guildMap.containsKey(guild);
    }

    public boolean containsMember(Guild guild, Member member) {
        return guildMap.get(guild).containsKey(member);
    }

    public Role checkForRoleLevel(Guild guild, Integer level) {
        return guildMap.get(guild).containsLevelRoleKey(level);
    }

    public void addLevelRole(Guild guild, Integer level, Role role) {
        guildMap.get(guild).putLevelRole(level, role);
    }
}
