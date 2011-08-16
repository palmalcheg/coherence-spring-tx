package org.drools.alternative.persistence.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.runtime.Environment;

public class ThreadLocalEnvironmentImpl implements Environment {

        private static ThreadLocal<Map<String, Object>> localThreadState = new ThreadLocal<Map<String, Object>>();

        private Environment delegate;

        public void setDelegate(Environment delegate) {
            this.delegate = delegate;
        }

        public Object get(String identifier) {
            Map<String, Object> map = localThreadState.get();
            if (map == null) {
                localThreadState.set(new ConcurrentHashMap<String, Object>());
                map = localThreadState.get();
            }
            Object object = map.get(identifier);
            if (object == null && delegate != null) {
                object = this.delegate.get(identifier);
            }
            return object;
        }

        public void set(String name, Object object) {
            Map<String, Object> map = localThreadState.get();
            if (map == null) {
                localThreadState.set(new ConcurrentHashMap<String, Object>());
                map = localThreadState.get();
            }
            map.put(name, object);
        }
    }