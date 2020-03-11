package Commands;

import java.util.ArrayList;
import java.util.Collections;

public class commandArr{
    private ArrayList<ICommand> commands = new ArrayList();

    public void add(ICommand command){
        commands.add(command);
        Collections.sort(commands);
    }
}

