package LevelModule;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MemberMap {
    private Map<Member, UserLevel> memberMap = Collections.synchronizedMap(new HashMap());
    private Map<Integer, Role> levelRoleMap = Collections.synchronizedMap(new HashMap());

    public void put(Member member){
        memberMap.put(member, new UserLevel(member));
    }

    public UserLevel get(Member member){
        return memberMap.get(member);
    }

    public boolean containsKey(Member member){
        return memberMap.containsKey(member);
    }

    public void putLevelRole(Integer level, Role role){
        if (levelRoleMap.containsKey(level)) setLevelRole(level, role);
        else levelRoleMap.put(level, role);
    }

    public void setLevelRole(Integer level, Role role){
        levelRoleMap.replace(level, role);
    }

    public Role containsLevelRoleKey(Integer level){
        if (levelRoleMap.containsKey(level)) return levelRoleMap.get(level);
        else return null;
    }
}
