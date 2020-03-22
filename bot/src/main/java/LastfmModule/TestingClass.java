package LastfmModule;

import Commands.Command;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TestingClass extends Command {

    private EventWaiter waiter;
    private EmbedBuilder embedBuilder;
    private final LastfmModule.Paginator.Builder pbuilder;


    public TestingClass(EventWaiter waiter){
        this.waiter = waiter;
        pbuilder = new Paginator.Builder()

                .setColumns(1)
                .setBulkSkipNumber(2000)
                .setItemsPerPage(1)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(false)
                .setFinalAction(m ->{
                    try {
                        m.clearReactions().queue();
                    }catch (PermissionException ex){
                        m.delete().queue();
                    }

                })
                .setEventWaiter(waiter)
                .setTimeout(2, TimeUnit.MINUTES);
    }
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        int page = 1;
        String [] array = event.getMessage().getContentRaw().split(" ");
        if(array[0].equalsIgnoreCase("%test")){
            String [] pages = new String[2];
            int pageXD = 0;
            for(int i = 0; i < 20; i++){
                if(i == 0){
                    pages[pageXD] = "";
                }



                pages[pageXD] += i+1 + " Foo Fighters - [Stranger Things Have Happened](https://www.last.fm/music/Foo+Fighters/_/Stranger+Things+Have+Happened) (2 plays) \n";
                if(pages[pageXD].length() > 1800){
                    pageXD++;
                    pages[pageXD] = "";
                }


            }

            pbuilder.clearItems();
            pbuilder.addItems(pages);
            pbuilder.setAuthorText("test");
            pbuilder.setAuthorURL("https://www.last.fm/user/robi874");
            pbuilder.setThumbnail("https://ih1.redbubble.net/image.411594717.6096/ap,550x550,12x16,1,transparent,t.u1.png");

            LastfmModule.Paginator p = pbuilder.setColor(Color.RED)
                    .setText("")
                    .build();
            p.paginate(event.getChannel(), page);



        }
    }
}
