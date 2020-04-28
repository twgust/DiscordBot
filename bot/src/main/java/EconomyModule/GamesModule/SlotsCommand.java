package EconomyModule.GamesModule;

import Commands.Command;
import EconomyModule.EconomyController;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Random;

public class SlotsCommand extends Command {
    //TODO: Balance slot randomization, calculate wins and deposit winnings, edit message to make the slots "spin"
    EconomyController economyController;
    public SlotsCommand(EconomyController economyController) { this.economyController = economyController; }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String[][] emojiArray = new String[3][3];
        int[][] slotArray = new int[3][3];
        Random rd = new Random();
        int randomInt;
        int iteration = 0;
        User user = event.getAuthor();
        RestAction<Message> action = event.getChannel().sendMessage("Putting in your Ⱡ");
        action.queue();
        while (iteration < 3) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int row = 0; row < emojiArray.length; row++) {
                for (int col = 0; col < emojiArray[row].length; col++) {
                    randomInt = rd.nextInt(5);
                    emojiArray[row][col] = parseEmoji(randomInt);
                    slotArray[row][col] = randomInt;
                }
            }
            System.out.println(String.format("%s %s %s \n%s %s %s \n%s %s %s", emojiArray[0][0],emojiArray[0][1],emojiArray[0][2],emojiArray[1][0],emojiArray[0][1],emojiArray[0][2],emojiArray[2][0],emojiArray[0][1],emojiArray[0][2]));
            iteration++;
        }
    }

    private String parseEmoji(int emojiInt) {
        switch (emojiInt) {
            case 0:
                //cherries
                return "\uD83C\uDF52";
            case 1:
                //latvian flag/BAR
                return "\uD83C\uDDF1\uD83C\uDDFB";
            case 2:
                //grapes
                return "\uD83C\uDF47";
            case 3:
                //orange
                return "\uD83C\uDF4A";
            case 4:
                //banana
                return "\uD83C\uDF4C";
        }
        //return a cross as a fallback
        return "❌";
    }
}
