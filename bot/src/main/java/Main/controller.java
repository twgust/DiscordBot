package Main;

import Commands.Command;
import Commands.CommandMap;
import Commands.HelloCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.security.auth.login.LoginException;

/**
 * Controller klass, JDA Buildern tar in ett token. Detta token är bottens ID..
 */
public class controller {
    private CommandMap cmdMap = new CommandMap();
    public controller() throws LoginException {
        addCommands();

        JDA jda = new JDABuilder("Njg3MjMxNTc3MDAwMTE2MjI0.Xmi1Qw.YWg2zrgmgaPk-hcnD1q93a3Ot1E").build();
        jda.addEventListener(new eventListener(this));
    }

    /**
     * Denna metod skall söka och exekvera vilket kommando det är som är kallat på från användaren.
     * @param event
     */
    public void processMessage(GuildMessageReceivedEvent event){
        String[] arguments = event.getMessage().getContentRaw().substring(1).split(" ");
        arguments[0] = arguments[0].substring(0,1).toUpperCase()+arguments[0].substring(1);
        ((Command)cmdMap.get(arguments[0])).execute(event);
    }

    private void addCommands(){
        cmdMap.put("Hello", new HelloCommand());
    }
}
