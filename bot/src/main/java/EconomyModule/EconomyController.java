package EconomyModule;

import Commands.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EconomyController extends Command {
    EconomyDBConnector dbConnector;
    public EconomyController() throws IOException {
        if (Files.notExists(Paths.get("db"))) {
            Files.createDirectory(Paths.get("db"));
        }
        dbConnector = new EconomyDBConnector();
    }

    public int getWalletTotalForUser (String id) {
        if (!dbConnector.userExists(id)) {
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
