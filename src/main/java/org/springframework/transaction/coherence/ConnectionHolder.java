package org.springframework.transaction.coherence;


import org.springframework.transaction.support.ResourceHolderSupport;

import com.tangosol.coherence.transaction.Connection;

public class ConnectionHolder extends ResourceHolderSupport {

    public ConnectionHolder(Connection connection) {
        this.connection = connection;
    }

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void setTimeoutInSeconds(int seconds) {
        connection.setTransactionTimeout(seconds);
    }

}
