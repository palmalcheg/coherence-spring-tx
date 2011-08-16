package com.db.itrac.coherence.ext;

import com.oracle.coherence.configuration.caching.CacheMapping;
import com.oracle.coherence.configuration.caching.CacheMappingRegistry;
import com.oracle.coherence.configuration.expressions.Expression;
import com.oracle.coherence.configuration.expressions.MacroParameterExpression;
import com.oracle.coherence.environment.Environment;
import com.oracle.coherence.patterns.pushreplication.ClusterPublisherScheme.ReplicationRole;
import com.oracle.coherence.patterns.pushreplication.configuration.PublisherDefinition;
import com.oracle.coherence.patterns.pushreplication.publishers.RemoteClusterPublisherScheme;
import com.oracle.coherence.patterns.pushreplication.publishers.cache.BruteForceConflictResolver;
import com.oracle.coherence.patterns.pushreplication.publishers.cache.ConflictResolver;
import com.oracle.coherence.patterns.pushreplication.publishers.cache.LocalCachePublisherScheme;
import com.oracle.coherence.schemes.ClassScheme;
import com.oracle.coherence.schemes.ParameterizedClassScheme;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.Invocable;
import com.tangosol.net.InvocationService;

public class PublisherRegistrationTask implements Invocable {
    
    private String cacheName;
    private String subscriberName;
    private boolean isHub;

    public PublisherRegistrationTask(String cacheName, String subscriberName, boolean isHub) {
        this.cacheName = cacheName;
        this.subscriberName = subscriberName;
        this.isHub = isHub;
    }

    public void init(InvocationService invocationService) {

    }

    public void run() {

        ConfigurableCacheFactory ccf = CacheFactory.getConfigurableCacheFactory();
        Environment environment = (Environment) ccf;

        CacheMappingRegistry cmr = environment.getResource(CacheMappingRegistry.class);

        CacheMapping cm = cmr.findCacheMapping(cacheName);

        PublisherDefinition pd = createPublisherDefinition();
        cm.addDecoration(PublisherDefinition.class, pd.getPublisherName(), pd);
        ccf.ensureCache(cacheName, environment.getClassLoader());

    }

    public Object getResult() {
        return null;
    }

    private PublisherDefinition createPublisherDefinition() {

        PublisherDefinition pd = new PublisherDefinition();
        Expression<String> targetCacheName = new MacroParameterExpression(String.class, cacheName);
        Expression<String> publisherName = new MacroParameterExpression(String.class, subscriberName);
        ClassScheme<ConflictResolver> conflictResolverClassScheme = new ParameterizedClassScheme<ConflictResolver>(
                BruteForceConflictResolver.class.getName());

        RemoteClusterPublisherScheme publisherScheme = new RemoteClusterPublisherScheme();
        publisherScheme.setRemoteInvocationServiceName(subscriberName);
        publisherScheme.setAutostart(true);
        if (isHub) {
            publisherScheme.setReplicationRole(ReplicationRole.HUB);
        }

        LocalCachePublisherScheme localCacheScheme = new LocalCachePublisherScheme();
        localCacheScheme.setTargetCacheName(targetCacheName);
        localCacheScheme.setConflictResolverScheme(conflictResolverClassScheme);

        publisherScheme.setRemotePublisherScheme(localCacheScheme);

        pd.setPublisherScheme(publisherScheme);
        pd.setPublisherName(publisherName);
        return pd;

    }

}
