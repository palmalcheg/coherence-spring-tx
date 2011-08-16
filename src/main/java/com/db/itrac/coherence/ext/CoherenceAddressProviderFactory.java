package com.db.itrac.coherence.ext;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.tangosol.net.AddressProvider;

public abstract class CoherenceAddressProviderFactory {
    
    private static UpdateableAddressProvider subs = new AddressProviderImpl();
    private static UpdateableAddressProvider pubs = new AddressProviderImpl();

    public interface UpdateableAddressProvider extends AddressProvider {

        void addAddress(String host, int port);

        void removeAddress(String host, int port);

    }

    public static UpdateableAddressProvider subscribers() {
        return subs;
    };

    public static UpdateableAddressProvider publishers() {
        return pubs;
    };

    private static class AddressProviderImpl implements UpdateableAddressProvider {

        private Set<InetSocketAddress> addresses = new CopyOnWriteArraySet<InetSocketAddress>();

        private Iterator<InetSocketAddress> iterator = addresses.iterator() ;

        @Override
        public void accept() {
        }

        @Override
        public InetSocketAddress getNextAddress() {
            return iterator.next();
        }

        @Override
        public void reject(Throwable arg0) {
        }

        @Override
        public void addAddress(String host, int port) {
            addresses.add(new InetSocketAddress(host, port));
            iterator = addresses.iterator();
        }

        @Override
        public void removeAddress(String host, int port) {
            for (InetSocketAddress a : addresses) {
                if (a.getHostName().equals(host) && a.getPort() == port) {
                    addresses.remove(a);
                }
            }
            iterator = addresses.iterator();

        }

    }

}
