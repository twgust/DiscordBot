package ModerationModule;

import Commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;


public class ModCommand extends Command {
    private ModerationController modCTRL;
    private EmbedBuilder eb = new EmbedBuilder();
    public ModCommand(ModerationController modCTRL){
        this.modCTRL = modCTRL;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        modCTRL.execute(event);
    }

    public void execute(TextChannel channel, Member member, String text, int num) {

    }

    @Override
    public EmbedBuilder getHelp(){
        eb.clear();
        eb.setTitle("Moderation Command \uD83D\uDC6E");
        eb.setDescription("Moderation Module!");
        eb.addField("Ban", "- Shows help for the Ban Command", false);
        eb.addField("Kick", "- Shows help for the Kick Command", false);
        eb.addField("UnBan", "- Shows help for the UnBan Command", false);
        eb.addField("Help", "- <PlaceHolder>", true);
        eb.addField("Info", "- Shows help for the Info Command", false);
        eb.addField("Lock", "- Shows help for the Lock Command", false);
        eb.addField("unlock", "- Shows help for the Unlock Command", false);
        eb.addField("Mute", "- Shows help for the Mute Command", false);
        eb.addField("Prune", "- Shows help for the Prune Command", false);
        eb.setFooter("wiz#8158 if you have suggestions");
        eb.setColor(Color.BLACK);
        return eb;
    }
}
