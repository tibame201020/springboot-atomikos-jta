package custom.tibame201020.demo.config.platorm;

import com.atomikos.util.Atomikos;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

public class CustomAtomikosPlatform extends ServerPlatformBase {

    public CustomAtomikosPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        disableRuntimeServices();
    }

    @Override
    public Class<CustomAtomikosTransactionController> getExternalTransactionControllerClass() {
        return CustomAtomikosTransactionController.class;
    }

    @Override
    protected void initializeServerNameAndVersion() {
        this.serverNameAndVersion="Atomikos: "+ Atomikos.VERSION;
    }
}
