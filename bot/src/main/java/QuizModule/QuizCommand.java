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
    private Quiz quiz = new Quiz(this);
    private TextChannel quizChannel;
    private final String channelName = "quiz";

    //Executes the user command, starting or stopping a quiz session
    @Override
    public void execute(GuildMessageReceivedEvent event) {

        if (event.getChannel().getName().equals(channelName)) {
            quizChannel = event.getChannel();
            String receivedMessage = event.getMessage().getContentRaw();
            String secondCommand = receivedMessage.substring(6);


            if (secondCommand.equals("start") && !quiz.isAlive()) {
                quiz.start();
            } else if (secondCommand.equals("stop") && quiz.isAlive()) { //Not working
                postMessage("The Quiz session has been stopped!");
                quiz.interrupt();
            }
        }
    }

    //Posts messages in the chat "quiz"
    public void postMessage(String message) {
        quizChannel.sendMessage(message).queue();
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

    //Deletes messages in the chat "quiz"
    public void deleteMessages(){
        List<Message> messages = quizChannel.getHistory().retrievePast(100).complete();
        if(!messages.isEmpty() && messages.size() > 2){
            quizChannel.deleteMessages(messages).complete();
        }

    }

    //ToDo
    public void lockQuizChannel(){

    }

    //ToDo
    public void unlockQuizChannel(){

    }

    //ToDo
    public void limitChat(int limit){
        quizChannel.getManager().setSlowmode(limit); //Not working
    }

}
