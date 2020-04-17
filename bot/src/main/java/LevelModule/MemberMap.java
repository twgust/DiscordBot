package LevelModule;

import net.dv8tion.jda.api.entities.Member;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MemberMap {
    private Map<Member, UserLevel> guildMap = Collections.synchronizedMap(new HashMap());
}
