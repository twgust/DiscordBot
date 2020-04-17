package LevelModule;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class UserLevel {
    private int level = 0;
    private int currentExp = 0;
    private int nextLevelExp = 20;
    private Member member;

    public UserLevel(Member member){
        this.member = member;
    }

    public void addExp(TextChannel channel){
        currentExp++;
        if (levelUp()) channel.sendMessage("Oi c*nt! Yeah you " + member.getAsMention() + ". You just leveled up. Hope you are happy being level " + level + " now!").queue();
    }

    private boolean levelUp(){
        boolean levelUp = currentExp > nextLevelExp;
        if (levelUp){
            level++;
            nextLevelExp += level * 20;
            currentExp = 0;
        }
        return levelUp;
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public int getNextLevelExp() {
        return nextLevelExp;
    }
}
