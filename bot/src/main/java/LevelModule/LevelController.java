package LevelModule;

import Main.Controller;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class LevelController {
    private static GuildMap guildMap = new GuildMap();

    public static void addGuild(Guild guild){
        guildMap.put(guild);
    }

    public static void addExp(Guild guild, Member member, TextChannel channel){
        checkMember(guild, member);
        guildMap.getUserLevel(guild, member).addExp(channel);
    }

    public static String getUserInfo(Guild guild, Member member){
        String info = "```\nLevel: " + guildMap.getUserLevel(guild, member).getLevel() +
                "\nCurrent exp:" + guildMap.getUserLevel(guild, member).getCurrentExp() +
                "\nExp for next level: " + guildMap.getUserLevel(guild, member).getNextLevelExp() + "\n```";
        return info;
    }

    public static void checkMember(Guild guild, Member member){
        if (!guildMap.containsKey(guild)){
            addGuild(guild);
            guildMap.get(guild).put(member);
        }
        if (!guildMap.containsMember(guild, member)){
            guildMap.get(guild).put(member);
        }
    }

    public static void checkForRoleLevel(UserLevel userLevel){
        Role levelRole = guildMap.checkForRoleLevel(userLevel.getMember().getGuild(), userLevel.getLevel());
        if (levelRole != null) userLevel.getMember().getGuild().addRoleToMember(userLevel.getMember(), levelRole).queue();
    }

    public static boolean addLevelRole(Guild guild, Integer level, Role role){
        System.out.println("2hi");
        guildMap.addLevelRole(guild, level, role);
        System.out.println("2hii");
        return guildMap.checkForRoleLevel(guild, level) != null;
    }
}
