package ModerationModule;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GetMember{
    public static Member get(String text, TextChannel channel, Member Author) {
        Member member = null;
        try {
            member = channel.getGuild().getMemberById(Long.parseLong(text));
        } catch (Exception e) {
            try {
                member = channel.getGuild().getMemberById(text.substring(3, text.length() - 1));
            } catch (Exception e2) {
                List<Member> memberList = channel.getGuild().getMembers();
                List<Member> targetList = new ArrayList<>();
                for (int i = 0; i < memberList.size(); i++) {
                    if (memberList.get(i).getEffectiveName().toLowerCase().indexOf(text.toLowerCase()) != -1) {
                        targetList.add(memberList.get(i));
                    }
                }
                if (targetList.size() > 1) {
                    String multipleMembersMsg = "Multiple members found. Please choose one of the following, or type cancel.";
                    for (int i = 0; i < targetList.size(); i++) {
                        multipleMembersMsg += "\n" + i + ". " + targetList.get(i).getUser().getName();
                    }
                    channel.sendMessage(multipleMembersMsg).complete();
                    long time = System.currentTimeMillis() + 10000;
                    int userChoice = -1;

                    while (time > System.currentTimeMillis() && userChoice == -1) {
                        Message msg = channel.getHistory().retrievePast(1).complete().get(0);
                        if (msg.getMember().equals(Author)) {
                            try {
                                userChoice = Integer.parseInt(msg.getContentRaw());
                            } catch (NumberFormatException nfe) {
                                if (msg.getContentRaw().equalsIgnoreCase("cancel")){
                                    userChoice = -2;
                                }
                            }
                        }
                    }
                    if (userChoice > -1 && userChoice < targetList.size()){
                        member = targetList.get(userChoice);
                    }else channel.sendMessage("Command cancelled").queue();
                } else if (targetList.size() == 1) {
                    member = targetList.get(0);
                }
            }
        }
        return member;
    }
}
