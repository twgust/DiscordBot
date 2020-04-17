package LevelModule;

import net.dv8tion.jda.api.entities.Guild;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GuildMap {
    private Map<Guild, MemberMap> guildMap = Collections.synchronizedMap(new HashMap());
}
