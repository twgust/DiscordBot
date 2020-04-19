package EconomyModule;

import Commands.Command;

public class EconomyController extends Command {
    EconomyDBConnector dbConnector;
    public EconomyController() {
        dbConnector = new EconomyDBConnector();
    }

    public int getWalletTotalForUser (int id) {
        if (!dbConnector.userExists(id)) {
            dbConnector.createUser(id, 500);
        }
        return dbConnector.getRowTotal(id);
    }
}
