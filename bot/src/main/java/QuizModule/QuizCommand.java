package QuizModule;

import Commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

//ToDo
//ToDo - Check if command is typed in the correct channel for ease of use during run
public class QuizCommand extends Command {
    private GuildMessageReceivedEvent event;
    private Quiz quiz;

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String receivedMessage = event.getMessage().getContentRaw();
        String secondCommand = receivedMessage.substring(6);

        if(secondCommand.equals("start") && quiz == null) {
            this.event = event;
            Quiz quiz = new Quiz(this);
        }
        else if(secondCommand.equals("stop") && quiz != null){
            postMessage("The Quiz session has been stopped!");
            quiz.interrupt();
        }
    }

    public void postMessage(String message) {
        event.getChannel().sendMessage(message);
    }

}
