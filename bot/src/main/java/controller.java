import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.security.auth.login.LoginException;
import javax.swing.*;

public class controller {
    public controller() throws LoginException {


        JDA jda = new JDABuilder("Njc3ODY4NjM4OTAwMDYwMTYx.XkvVmw.atQ2blQD0B-R0lcKUEsD9tAfCx8").build();
        jda.addEventListener(new eventListener(this));
    }

    public void processMessage(GuildMessageReceivedEvent event){

    }
}
