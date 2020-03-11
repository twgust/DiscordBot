import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.security.auth.login.LoginException;
import javax.swing.*;

/**
 * Controller klass, JDA Buildern tar in ett token. Detta token är bottens ID.
 */
public class controller {
    public controller() throws LoginException {


        JDA jda = new JDABuilder("Njg3MjMxNTc3MDAwMTE2MjI0.XmiwqA.JfjTrbb2mAW0HBhMpTYTuh1QogE").build();
        jda.addEventListener(new eventListener(this));
    }

    /**
     * Denna metod skall söka och exekvera vilket kommando det är som är kallat på från användaren.
     * @param event
     */
    public void processMessage(GuildMessageReceivedEvent event){

    }
}
