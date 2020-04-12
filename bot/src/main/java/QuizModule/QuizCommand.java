package QuizModule;

import Commands.Command;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.ArrayList;
import java.util.List;

//A command to start a trivia game
public class QuizCommand extends Command {
    private final String channelName = "quiz"; //The name of the designated quiz text-channel
    private Quiz quiz;


    @Override
    public void execute(GuildMessageReceivedEvent event) {
        if (event.getChannel().getName().equals(channelName)) {

            String command = event.getMessage().getContentRaw().substring(6);

            switch(command){
                case "start":
                    quiz = new Quiz(event.getChannel());
                    quiz.start();
                    break;
                case "stop":
                    quiz.stop();
                    break;
                default:
                    System.out.println("Test - Please enter start after the quiz command to start and " +
                            "new session \n or stop to interrupt an ongoing session");
                    break;
            }
        }
    }

    //Listens to user activity in the chat "quiz"
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getChannel().getName().equals(channelName) && !event.getAuthor().isBot()) {
            Message msg = event.getMessage();
            User user = event.getAuthor();
            if(quiz.isAlive()) {
                quiz.checkAnswer(user, msg);
            }
        }
    }
}
