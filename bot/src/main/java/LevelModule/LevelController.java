package LevelModule;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class LevelController {
    private static GuildMap guildMap = new GuildMap();

    public static void addGuild(Guild guild){
        guildMap.put(guild);
    }

    public static void addExp(Guild guild, Member member, TextChannel channel){
        if (!guildMap.containsKey(guild)){
            addGuild(guild);
            guildMap.get(guild).put(member);
        }
        if (!guildMap.containsMember(guild, member)){
            guildMap.get(guild).put(member);
        }

        guildMap.getUserLevel(guild, member).addExp(channel);
    }

    public static String getUserInfo(Guild guild, Member member){
        String info = "```\nLevel: " + guildMap.getUserLevel(guild, member).getLevel() +
                "\nCurrent exp:" + guildMap.getUserLevel(guild, member).getCurrentExp() +
                "\nExp for next level: " + guildMap.getUserLevel(guild, member).getNextLevelExp() + "\n```";
        return info;
    }
}
