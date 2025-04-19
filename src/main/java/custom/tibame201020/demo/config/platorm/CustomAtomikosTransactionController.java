package custom.tibame201020.demo.config.platorm;


import com.atomikos.icatch.jta.UserTransactionManager;
import jakarta.transaction.TransactionManager;
import org.eclipse.persistence.transaction.JTATransactionController;

public class CustomAtomikosTransactionController extends JTATransactionController {

    private UserTransactionManager utm;

    public CustomAtomikosTransactionController() {
        utm = new UserTransactionManager();
    }
    /**
     * INTERNAL: Obtain and return the JTA TransactionManager on this platform
     */
    protected TransactionManager acquireTransactionManager() throws Exception {
        return utm;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return utm;
    }

}
