package EconomyModule;

import Commands.Command;

public class EconomyController extends Command {
    EconomyDBConnector dbConnector;
    public EconomyController() {
        dbConnector = new EconomyDBConnector();
    }

    public int getWalletTotalForUser (String id) {
        if (dbConnector.userExists(id)) {
            dbConnector.createUser(id, 500);
        }
        return dbConnector.getRowTotal(id);
    }

    public void activityReward (String id) {
        dbConnector.addToTotal(id, 5);
    }

    public EconomyResponses transferToUser (String sender, String receiver, int transferAmount) {
        if (getWalletTotalForUser(sender) - transferAmount < 0) {
            return EconomyResponses.INSUFFICIENT_FUNDS;
        }
        else {
            dbConnector.transferToUser(sender, receiver, transferAmount);
            return EconomyResponses.SUCCESS;
        }
    }
}
