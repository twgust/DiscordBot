package LevelModule;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MemberMap {
    private Map<Member, UserLevel> memberMap = Collections.synchronizedMap(new HashMap());

    public void put(Member member){
        memberMap.put(member, new UserLevel(member));
    }

    public UserLevel get(Member member){
        return memberMap.get(member);
    }

    public boolean containsKey(Member member){
        return memberMap.containsKey(member);
    }
}
