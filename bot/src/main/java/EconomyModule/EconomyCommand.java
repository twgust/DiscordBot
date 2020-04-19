package EconomyModule;

import Commands.Command;

public class EconomyCommand extends Command {
    EconomyDBConnector dbConnector;
    public EconomyCommand() {
        dbConnector = new EconomyDBConnector();
    }
}
