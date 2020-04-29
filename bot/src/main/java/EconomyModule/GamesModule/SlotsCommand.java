package EconomyModule.GamesModule;

import Commands.Command;
import EconomyModule.EconomyController;
import EconomyModule.EconomyResponses;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Random;

public class SlotsCommand extends Command {
    EconomyController economyController;

    public SlotsCommand(EconomyController economyController) {
        this.economyController = economyController;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String[][] emojiArray = new String[3][3];
        int[][] slotArray = new int[3][3];
        Random rd = new Random();
        int bet = Integer.parseInt(event.getMessage().getContentRaw().split(" ", 2)[1]);
        if(economyController.subtractFromUser(event.getAuthor().getId(), bet) == EconomyResponses.INSUFFICIENT_FUNDS) {
            event.getChannel().sendMessage("You don't have enough Ⱡ for this bet!").queue();
            return;
        }
        event.getChannel().sendMessage("Putting in your Ⱡ").queue(message -> {
            int iteration = 0;
            while (iteration < 3) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int row = 0; row < emojiArray.length; row++) {
                    for (int col = 0; col < emojiArray[row].length; col++) {
                        slotArray[row][col] = rd.nextInt(10);
                        emojiArray[row][col] = parseEmoji(slotArray[row][col]);
                    }
                }
                message.editMessage(String.format("%s %s %s \n%s %s %s \n%s %s %s", emojiArray[0][0], emojiArray[0][1], emojiArray[0][2], emojiArray[1][0], emojiArray[1][1], emojiArray[1][2], emojiArray[2][0], emojiArray[2][1], emojiArray[2][2])).queue();
                iteration++;
            }
            int winnings = checkWins(emojiArray, slotArray, bet);
            if (winnings == 0) {
                event.getChannel().sendMessage("You lost "+bet+"Ⱡ.").queue();
            }
            else {
                event.getChannel().sendMessage("You won "+winnings+"Ⱡ!").queue();
                economyController.addToUser(event.getAuthor().getId(), winnings);
            }
        });
    }

    private String parseEmoji(int emojiInt) {

        if (emojiInt == 1) {
            //latvian flag/BAR
            return "\uD83C\uDDF1\uD83C\uDDFB";
        } else if (emojiInt < 4) {
            //cherries
            return "\uD83C\uDF52";
        } else if (emojiInt < 8) {
            //banana
            return "\uD83C\uDF4C";
        } else if (emojiInt < 10) {
            //orange
            return "\uD83C\uDF4A";
        }
        //return a cross as a fallback
        return "❌";
    }


    private int checkWins(String[][] array, int[][] intArray, int bet) {
        int lowestWinner = checkHorizontalRows(array, intArray);
        if (lowestWinner == 10) {
            return 0;
        }
        else if (lowestWinner == 1) {
            return bet*5;
        }
        else if (lowestWinner < 4) {
            return bet * 3;
        }
        else {
            return bet * 2;
        }
    }

    private int checkHorizontalRows(String[][] array, int[][] intArray) {
        int lowestInt = 10;
        for (int row = 0; row < array.length; row++) {
            if (array[row][0].equals(array[row][1]) && array[row][0].equals(array[row][2])) {
                if (intArray[row][0] < lowestInt) {
                    lowestInt = intArray[row][0];
                }
            }
        }
        return lowestInt;
    }

}