package QuizModule;

import Commands.Command;
import Main.EventListener;
import QuizModule.QuizMulti.QuizMulti;
import QuizModule.QuizSingle.QuizSingle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class QuizCommand extends Command {
    private QuizSingle quizS = new QuizSingle();
    private QuizMulti quizM = new QuizMulti();
    private TextChannel channel;
    private EmbedBuilder eb = new EmbedBuilder();
    private String helpText = "To play quiz, you can choose between either Single-answers or Multi-answers\n" +
            "-To start playing a Single-answers game, type **"+EventListener.prefix+"quiz start single**\n" +
            "-To stop playing a Single-answers game, type  **"+EventListener.prefix+"quiz stop single**\n" +
            "-To skip a question in a Single-answers game, type **"+EventListener.prefix+"quiz skip**\n" +
            "-To start playing a Multi-answers game, type **"+EventListener.prefix+"quiz start multi**\n" +
            "-To stop playing a Multi-answers game, type  **"+EventListener.prefix+"quiz stop multi**\n";

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        channel = event.getChannel();
        quizS.setTextChannel(channel);
        quizM.setTextChannel(channel);
        String subCommand = event.getMessage().getContentRaw().substring(6);

        switch(subCommand){
            case "start single":
                if(quizM.isAlive()){
                    eb.setTitle("A session of Multi-answer Quiz is currently running\n" +
                            "Please wait for it to finish before starting an new session of Single-answer Quiz");
                    eb.setDescription("");
                    event.getChannel().sendMessage(eb.build()).queue();
                    break;
                }
                else {
                    quizS.start(event.getAuthor());
                    break;
                }
            case "stop single":
                quizS.stop(event.getAuthor());
                break;
            case "skip":
                quizS.skip(event.getAuthor());
                break;
            case "start multi":
                if(quizS.isAlive()){
                    eb.setTitle("A session of Single-answer Quiz is currently running\n" +
                            "Please wait for it to finish before starting an new session of Multi-answer Quiz");
                    eb.setDescription("");
                    event.getChannel().sendMessage(eb.build()).queue();
                    break;
                }
                else {
                    quizM.start(event.getAuthor());
                    break;
                }
            case "stop multi":
                quizM.stop(event.getAuthor());
                break;
        }

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getChannel().equals(channel) && !event.getAuthor().isBot()) {
            Message message = event.getMessage();
            User user = event.getAuthor();
            if(quizS != null){
                if(quizS.isAlive()) {
                    quizS.checkAnswer(user, message);
                }
            }
            if(quizM != null){
                if(quizM.isAlive()){
                    quizM.checkAnswer(user, message);
                }
            }
        }
    }

    @Override
    public String getHelp() {
        return helpText;
    }
}
