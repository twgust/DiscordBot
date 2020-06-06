package LevelModule;

import Main.Controller;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.*;

public class LevelController {
    private static LevelDBConnector levelDB = new LevelDBConnector();
    private static EmbedBuilder eb = new EmbedBuilder();

    public static void addGuild(Guild guild) {
        levelDB.newGuildTable("G"+guild.getId());
    }

    public static void addExp(Guild guild, Member member, TextChannel channel) {
        addGuild(guild);
        if (!levelDB.userExist("G"+guild.getId(), member.getIdLong())) {
            levelDB.createUser("G"+guild.getId(), member.getIdLong());
        }
        levelDB.addUserExp("G" + guild.getId(), member.getIdLong(), channel);
    }

    public static String getUserInfo(Guild guild, Member member) {
        String data[] = levelDB.getUserInfo("G"+guild.getId(),member.getIdLong());
        String info = "```\nLevel: " + data[2]
                + "\nCurrent Exp: " + data[0]
                + "\nExp to next level: " + data[1]
                + "\n```";
        return info;
    }

    public static void levelUP(long memberID, int level, TextChannel channel){
        Guild guild = channel.getGuild();
        Member member = guild.getMemberById(memberID);
        eb.clear();
        eb.setTitle("Oi c*nt! Yeah you " + member.getNickname() +
                ". You just leveled up. Hope you are happy being level " + level + " now!");
        eb.setDescription("<@"+member.getUser().getId()+">");
        eb.setColor(Color.YELLOW);
        channel.sendMessage(eb.build()).queue();
    }

    public static boolean addLevelRole(Guild guild, Integer level, Role role) {
        return true;
    }
}