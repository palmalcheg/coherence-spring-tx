/*
 * File: ExtensibleConfiguration.java
 * 
 * Copyright (c) 2010. All Rights Reserved. Oracle Corporation.
 * 
 * Oracle is a registered trademark of Oracle Corporation and/or its affiliates.
 * 
 * This software is the confidential and proprietary information of Oracle
 * Corporation. You shall not disclose such confidential and proprietary
 * information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Oracle Corporation.
 * 
 * Oracle Corporation makes no representations or warranties about the
 * suitability of the software, either express or implied, including but not
 * limited to the implied warranties of merchantability, fitness for a
 * particular purpose, or non-infringement. Oracle Corporation shall not be
 * liable for any damages suffered by licensee as a result of using, modifying
 * or distributing this software or its derivatives.
 * 
 * This notice may not be removed or altered.
 */
package com.oracle.coherence.environment.extensible;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.coherence.common.events.dispatching.EventDispatcher;
import com.oracle.coherence.common.events.dispatching.SimpleEventDispatcher;
import com.oracle.coherence.common.events.lifecycle.LifecycleStartedEvent;
import com.oracle.coherence.common.events.lifecycle.LifecycleStoppedEvent;
import com.oracle.coherence.common.events.lifecycle.NamedCacheLifecycleEvent;
import com.oracle.coherence.common.events.lifecycle.NamedCacheStorageRealizedEvent;
import com.oracle.coherence.common.events.lifecycle.NamedCacheStorageReleasedEvent;
import com.oracle.coherence.common.logging.CoherenceLogHandler;
import com.oracle.coherence.common.threading.ExecutorServiceFactory;
import com.oracle.coherence.common.threading.ThreadFactories;
import com.oracle.coherence.configuration.caching.CacheMapping;
import com.oracle.coherence.configuration.caching.CacheMappingRegistry;
import com.oracle.coherence.configuration.parameters.MutableParameterScope;
import com.oracle.coherence.configuration.parameters.NestedParameterScope;
import com.oracle.coherence.configuration.parameters.Parameter;
import com.oracle.coherence.configuration.parameters.SystemPropertyParameterScope;
import com.oracle.coherence.environment.Environment;
import com.oracle.coherence.environment.ResourceResolver;
import com.oracle.coherence.environment.extensible.dependencies.DependencyTracker;
import com.oracle.coherence.environment.extensible.dependencies.DependentResource;
import com.oracle.coherence.environment.extensible.namespaces.CoherenceNamespaceContentHandler;
import com.oracle.coherence.environment.extensible.namespaces.IntroduceNamespaceContentHandler;
import com.oracle.coherence.schemes.ClassScheme;
import com.oracle.coherence.schemes.SchemeRegistry;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.net.CacheService;
import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.Service;
import com.tangosol.run.xml.XmlDocument;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlHelper;
import com.tangosol.util.ServiceEvent;
import com.tangosol.util.ServiceListener;

/**
 * <p>An {@link ExtensibleEnvironment} is a {@link com.tangosol.net.ConfigurableCacheFactory} that provides;</p>
 * <ol>
 *  <li>An implementation of an {@link Environment} to manage resources, and</li>
 *  <li>Support for user-defined custom namespaced xml elements with in a Coherence Cache Configuration file 
 *      together with associated {@link NamespaceContentHandler}s to process said xml elements.</li>
 * </ol>
 * 
 * <p>The following namespaces are implicitly defined for all Coherence Cache Configuration files.</p>
 * <p>{@link CoherenceNamespaceContentHandler} is the default namespace handler.</p>
 * <p>{@link IntroduceNamespaceContentHandler} is the namespace handler for the "introduce:" prefix 
 * (and for backwards compatibility it supports the "introduce-cache-config" element)</p> 
 * 
 * <p>To use an {@link ExtensibleEnvironment} in your application, you must set the 
 * "&lt;configurable-cache-factory-config&gt;" optional
 * override in your tangosol-coherence-override.xml file.</p>
 * 
 * @author Brian Oliver
 */
public class ExtensibleEnvironment extends DefaultConfigurableCacheFactory implements Environment
{

    /**
     * <p>The {@link Logger} for this class.</p>
     */
    private static final Logger logger = Logger.getLogger(ExtensibleEnvironment.class.getName());

    /**
     * <p>The set of {@link ResourceResolver}s that can be used to locate resources for the {@link Environment}.</p>
     */
    private ConcurrentHashMap<Class<?>, ResourceResolver<?>> resourceResolvers;

    /**
     * <p>The map of named Coherence {@link Service}s, keyed by service name, that have been "ensured" by using the
     * {@link ExtensibleEnvironment}.</p>
     * 
     * <p>We use this to keep track of {@link Service} life-cycles based on calls by
     * the {@link com.tangosol.net.DefaultCacheServer} to {@link #ensureService(XmlElement)}.</p>
     */
    private ConcurrentHashMap<String, Service> trackedServices;

    /**
     * <p>The set of {@link NamedCache} names that have been realized by this {@link ExtensibleEnvironment}.</p>
     * 
     * <p>We use this to keep track of the {@link NamedCacheLifecycleEvent}s to raise.</p>
     */
    private HashSet<String> trackedNamedCaches;

    /**
     * <p>The {@link ConfigurationContext} being used by the thread loading the configuration.</p>
     * 
     * <p>This is so that we can detect any re-entrancy when loading a configuration, and provide
     * precise information on the cause and location of the re-entrant call.</p>
     */
    private static ThreadLocal<ConfigurationContext> configurationContext = new ThreadLocal<ConfigurationContext>();


    /**
     * <p>Standard Constructor.</p>
     */
    public ExtensibleEnvironment()
    {
        super();
    }


    /**
     * <p>Standard Constructor with a specified configuration path and {@link ClassLoader}.</p>
     * 
     * @param path The path to the configuration file.
     * @param loader The {@link ClassLoader} to use to load resources.
     */
    public ExtensibleEnvironment(String path,
                                 ClassLoader loader)
    {
        super(path, loader);
    }


    /**
     * <p>Standard Constructor with a specified configuration path.</p>
     * 
     * @param path The path to the configuration file.
     */
    public ExtensibleEnvironment(String path)
    {
        super(path);
    }


    /**
     * <p>Standard Constructor with a root {@link XmlElement} document
     * as configuration.</p>
     * 
     * @param xmlConfig The {@link XmlElement} as the root of a configuration.
     */
    public ExtensibleEnvironment(XmlElement xmlConfig)
    {
        super(xmlConfig);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <R> R getResource(Class<R> clazz,
                             Object... params)
    {
        if (logger.isLoggable(Level.FINEST))
            logger.entering(this.getClass().getName(), "getResource", new Object[] { clazz, params });

        R result;
        ResourceResolver<R> resourceResolver = (ResourceResolver<R>) resourceResolvers.get(clazz);
        if (resourceResolver == null)
        {
            result = null;
        }
        else
        {
            result = resourceResolver.resolveResource(params);
        }

        if (logger.isLoggable(Level.FINEST))
            logger.exiting(this.getClass().getName(), "getResource", new Object[] { clazz, params });

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <R> void registerResourceResolver(Class<R> clazz,
                                             ResourceResolver<R> resourceResolver)
    {
        if (logger.isLoggable(Level.FINEST))
            logger.entering(this.getClass().getName(), "registerResourceResolver", new Object[] { clazz,
                    resourceResolver });

        ResourceResolver<R> existingResourceResolver = (ResourceResolver<R>) resourceResolvers.putIfAbsent(clazz,
            resourceResolver);

        if (existingResourceResolver == null)
        {
            //register the ResourceResolver itself as resource using it's class name (so that it may be accessed itself)
            registerResource((Class<ResourceResolver<?>>) resourceResolver.getClass(), resourceResolver);
        }
        else
        {
            logger.warning(String.format(
                "ResourceResolver for [%s] is already registered as [%s].  Skipping requested registration.", clazz,
                existingResourceResolver));
        }

        if (logger.isLoggable(Level.FINEST))
            logger.exiting(this.getClass().getName(), "registerResourceResolver", new Object[] { clazz,
                    resourceResolver });
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <R> void registerResource(Class<R> clazz,
                                     Object resource)
    {
        if (logger.isLoggable(Level.FINEST))
            logger.entering(this.getClass().getName(), "registerResource", new Object[] { clazz, resource });

        ResourceResolver<?> existingResourceResolver = (ResourceResolver<?>) resourceResolvers.putIfAbsent(clazz,
            new SingletonResourceResolver<R>((R) resource));

        if (existingResourceResolver == null)
        {
            //ensure the resource has a dependency tracker if it has dependencies
            if (resource instanceof DependentResource)
            {
                getResource(EventDispatcher.class).registerEventProcessor(LifecycleEventFilter.INSTANCE,
                    new DependencyTracker(this, (DependentResource) resource));
            }
        }
        else
        {
            logger.warning(String.format(
                "Resource for [%s] is already registered as [%s].  Skipping requested registration.", clazz,
                existingResourceResolver));
        }

        if (logger.isLoggable(Level.FINEST))
            logger.exiting(this.getClass().getName(), "registerResource", new Object[] { clazz, resource });
    }


    /**
     * {@inheritDoc}
     */
    public ClassLoader getClassLoader()
    {
        return getConfigClassLoader();
    }


    /**
     * <p>An internal method to setup the {@link Environment}.</p>
     */
    private void startup()
    {
        // let everyone know that they are using the Extensible Environment for Coherence cache configuration
        System.err.println();
        System.err.printf("Using the Incubator Extensible Environment for Coherence Cache Configuration\n");
        System.err.printf("Copyright (c) 2010, Oracle Corporation. All Rights Reserved.\n");
        System.err.println();

        // create the new set of ResourceResolvers
        this.resourceResolvers = new ConcurrentHashMap<Class<?>, ResourceResolver<?>>();

        // create the new map of services we're going to track
        this.trackedServices = new ConcurrentHashMap<String, Service>();

        // create the new map of named caches we're going to track
        this.trackedNamedCaches = new HashSet<String>();

        // add a SchemeRegistry resource - we'll use this to track Schemes
        registerResource(SchemeRegistry.class, new SchemeRegistry());

        // register a event dispatcher as a standard resource
        // (we'll need to access it from a variety of places)
        registerResource(EventDispatcher.class, new SimpleEventDispatcher(this));

        //TODO: replace the following with the initialization of the ExecutionServiceManager
        //(the following is just temporary until we have an ExecutionServiceManager)
        registerResource(ExecutorService.class, ExecutorServiceFactory.newSingleThreadExecutor(ThreadFactories
            .newThreadFactory(true, "Environment.Background.Executor", null)));
    }


    /**
     * <p>An internal method to gracefully shutdown an {@link Environment}.</p>
     */
    private void shutdown()
    {
        if (logger.isLoggable(Level.FINEST))
            logger.entering(this.getClass().getName(), "shutdown");

        // we're going to need the event dispatcher to send a bunch of
        // end-of-life life-cycle events
        EventDispatcher eventDispatcher = getResource(EventDispatcher.class);

        // send life-cycle events to release all of the named caches
        for (String cacheName : trackedNamedCaches)
        {
            eventDispatcher.dispatchEvent(new NamedCacheStorageReleasedEvent(cacheName));
        }

        // send life-cycle stopped events for all of the currently tracked services
        for (Service service : trackedServices.values())
        {
            eventDispatcher.dispatchEvent(new LifecycleStoppedEvent<Service>(service));
        }

        // let everyone know that the current environment has stopped
        eventDispatcher.dispatchEvent(new LifecycleStoppedEvent<Environment>(this));

        //TODO: replace the following with the shutdown of the ExecutionServiceManager
        //(the following is just temporary until we have an ExecutionServiceManager)
        getResource(ExecutorService.class).shutdown();

        if (logger.isLoggable(Level.FINEST))
            logger.exiting(this.getClass().getName(), "shutdown");
    }


    /**
     * <p>Creates a String representation of the current stack trace of the specified {@link Thread}.</p>
     * 
     * @param thread The thread from which we shall create a stack trace.
     * 
     * @return A string
     */
    private String fullStackTraceFor(Thread thread)
    {
        int dropStackTraceElements = 2;
        StringBuilder result = new StringBuilder();
        result.append("Stack Trace\n");
        for (StackTraceElement stackTraceElement : thread.getStackTrace())
        {
            if (dropStackTraceElements == 0)
            {
                result.append(stackTraceElement);
                result.append("\n");
            }
            else
            {
                dropStackTraceElements--;
            }
        }
        return result.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfig(XmlElement xmlConfig)
    {
        //detect if we're making a re-entrant attempt to load a configuration.  re-entrancy is not allowed.
        ConfigurationContext context;
        if (configurationContext.get() == null)
        {
            // construct the configuration context we're going to use to build our configuration
            context = new DefaultConfigurationContext(this);
            configurationContext.set(context);
        }
        else
        {
            String message = "An attempt to recursively load and process a Coherence Cache Configuration has occurred. "
                    + "This is usually caused by accessing a NamedCache or Service (through the CacheFactory) "
                    + "from a NamespaceContentHandler, " + "ElementContentHandler and/or AttributeContentHandler.";

            logger.severe(message);
            logger.severe(fullStackTraceFor(Thread.currentThread()));
            logger.severe(configurationContext.get().toString());

            throw new RuntimeException(message);
        }

        CoherenceLogHandler.initializeIncubatorLogging();

        if (logger.isLoggable(Level.FINEST))
            logger.entering(this.getClass().getName(), "setConfig", xmlConfig);

        // are we changing the configuration? if so we need to shutdown the previous one!
        if (this.resourceResolvers != null)
        {
            if (logger.isLoggable(Level.INFO))
                logger.info("Extensible Environment XML configuration has been reset.  Will now restart it.");

            shutdown();
        }

        //setup and initialize the environment
        startup();

        // automatically register the default namespace handler for coherence and the "introduce:" namespace
        CoherenceNamespaceContentHandler coherenceNamespaceContentHandler;
        IntroduceNamespaceContentHandler introduceNamespaceContentHandler;
        try
        {
            coherenceNamespaceContentHandler = (CoherenceNamespaceContentHandler) context
                .ensureNamespaceContentHandler("",
                    new URI(String.format("class:%s", CoherenceNamespaceContentHandler.class.getName())));

            introduceNamespaceContentHandler = (IntroduceNamespaceContentHandler) context
                .ensureNamespaceContentHandler("introduce",
                    new URI(String.format("class:%s", IntroduceNamespaceContentHandler.class.getName())));
        }
        catch (URISyntaxException uriSyntaxException)
        {
            // ERROR: this means that our own URI is broken! we need to throw an exception
            throw new RuntimeException(
                "FATAL ERROR: The internal URI created by the ExtensisbleEnvironment is invalid.", uriSyntaxException);
        }

        //attempt to process the coherence cache configuration we've been provided with the configuration context
        try
        {
            context.processDocument(xmlConfig);

            // clean up the manually registered NamespaceContentHandlers
            coherenceNamespaceContentHandler.onEndScope(context, "", context.getNamespaceURI(""));
            introduceNamespaceContentHandler.onEndScope(context, "introduce", context.getNamespaceURI("introduce"));

            // delegate responsibility for configuring Coherence to the DefaultConfigurableCacheFactory
            // (using the CoherenceNamespaceContentHandler to provide an appropriate "standard" coherence configuration)
            StringBuilder builder = new StringBuilder();
            coherenceNamespaceContentHandler.build(builder);
            XmlDocument xmlDocument = XmlHelper.loadXml(builder.toString());

            super.setConfig(xmlDocument);
        }
        catch (ConfigurationException configurationException)
        {
            logger.log(Level.SEVERE, configurationException.toString());

            throw ensureRuntimeException(configurationException);
        }
        finally
        {
            // we're done configuring now
            configurationContext.set(null);
        }

        //let everyone know that the Environment has now available (has started)
        //NOTE: we do asynchronously to ensure that there is no re-entrant calls caused by the
        //EventProcessors waiting for this event as it's highly likely they'll use Coherence directly and calls
        //made from this (Coherence) thread still believe configuration is still in progress.
        getResource(EventDispatcher.class).dispatchEventLater(
            new LifecycleStartedEvent<Environment>(ExtensibleEnvironment.this));

        if (logger.isLoggable(Level.FINEST))
            logger.exiting(this.getClass().getName(), "setConfig");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Service ensureService(XmlElement element)
    {
        // grab the event dispatcher... we're going to use it to notify registered
        // EventProcessors about service life-cycles.
        final EventDispatcher eventDispatcher = this.getResource(EventDispatcher.class);

        // resolve any inherited/referenced scheme elements for the provided element
        // (we need to do this to find the scheme name of the service when using
        // scheme-refs)
        XmlElement serviceSchemeElement = resolveScheme(element, null, false, false);

        // determine if the service is already known to us
        final String serviceName = serviceSchemeElement.getSafeElement("service-name").getString();
        boolean isTrackedService = trackedServices.containsKey(serviceName);

        // delegate the actual "ensuring" the service to the super class
        // (as we don't know how to start it here)
        Service service = super.ensureService(element);
        String serviceType = service.getInfo().getServiceType();

        // now determine what happened
        // (we only care if we started/restarted a real named service)
        if (serviceName.length() > 0 && !serviceType.equals("LocalCache"))
        {
            if (isTrackedService)
            {
                // determine if we actually restarted (a new instance) of an existing service
                Service previousService = trackedServices.get(serviceName);
                if (service != previousService)
                {
                    // notify everyone that the previous service has stopped
                    // (as it's no longer good for anyone to use)
                    eventDispatcher.dispatchEvent(new LifecycleStoppedEvent<Service>(previousService));

                    trackedServices.put(serviceName, service);

                    // now notify everyone that the new service has started
                    eventDispatcher.dispatchEvent(new LifecycleStartedEvent<Service>(service));
                }
            }
            else
            {
                trackedServices.put(serviceName, service);

                // notify everyone that the new service has started
                eventDispatcher.dispatchEvent(new LifecycleStartedEvent<Service>(service));

                // add a listener to the service to determine if it is programmatically shutdown
                // (so we can clean up)
                service.addServiceListener(new ServiceListener()
                {

                    public void serviceStopping(ServiceEvent serviceEvent)
                    {
                        // SKIP: we only care when the service is started or stopped
                    }


                    public void serviceStopped(ServiceEvent serviceEvent)
                    {
                        if (serviceEvent.getService() instanceof Service)
                        {
                            eventDispatcher.dispatchEvent(new LifecycleStoppedEvent<Service>((Service) serviceEvent
                                .getService()));
                            trackedServices.remove(serviceName);
                        }
                    }


                    /**
                     * {@inheritDoc}
                     */
                    public void serviceStarting(ServiceEvent serviceEvent)
                    {
                        // SKIP: we only care when the service is started or stopped
                    }


                    /**
                     * {@inheritDoc}
                     */
                    public void serviceStarted(ServiceEvent serviceEvent)
                    {
                        if (serviceEvent.getService() instanceof Service)
                        {
                            eventDispatcher.dispatchEvent(new LifecycleStartedEvent<Service>((Service) serviceEvent
                                .getService()));
                            trackedServices.put(serviceName, (Service) serviceEvent.getService());
                        }
                    }
                });

            }
        }

        return service;
    }


    /**
     * <p>Tracks if the specified named cache has been seen before.  If it hasn't, it raises
     * a {@link NamedCacheStorageRealizedEvent}.</p>
     *   
     * @param cacheName The named cache
     */
    protected void trackNamedCacheStorage(String cacheName)
    {
        boolean raiseEvent = false;
        synchronized (trackedNamedCaches)
        {
            raiseEvent = !trackedNamedCaches.contains(cacheName);

            if (raiseEvent)
            {
                trackedNamedCaches.add(cacheName);
            }
        }

        if (raiseEvent)
        {
            this.getResource(EventDispatcher.class).dispatchEvent(new NamedCacheStorageRealizedEvent(cacheName));
        }
    }


    /**
     * <p>Stops tracking the specified named cache.  If the cache has been tracked, it raises
     * a {@link NamedCacheStorageReleasedEvent}.</p>
     *   
     * @param cacheName The named cache
     */
    protected void untrackNamedCacheStorage(String cacheName)
    {
        boolean raiseEvent = false;
        synchronized (trackedNamedCaches)
        {
            raiseEvent = trackedNamedCaches.contains(cacheName);

            if (raiseEvent)
            {
                trackedNamedCaches.remove(cacheName);
            }
        }

        if (raiseEvent)
        {
            this.getResource(EventDispatcher.class).dispatchEvent(new NamedCacheStorageReleasedEvent(cacheName));
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void register(CacheService cacheService,
                            String cacheName,
                            String context,
                            Map map)
    {
        super.register(cacheService, cacheName, context, map);

        //now track that cache - the cache storage must now exist
        trackNamedCacheStorage(cacheName);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void unregister(CacheService cacheService,
                              String cacheName)
    {
        super.unregister(cacheService, cacheName);

        //now untrack the cache - the cache storage must have been released
        untrackNamedCacheStorage(cacheName);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void unregister(String cacheName,
                              String context)
    {
        super.unregister(cacheName, context);

        //now untrack the cache - the cache storage must have been released
        untrackNamedCacheStorage(cacheName);
    }


    /**
     * {@inheritDoc}
     */
    public Object instantiateAny(CacheInfo info,
                                 XmlElement xmlClass,
                                 BackingMapManagerContext context,
                                 ClassLoader loader)
    {
        //use a registered Scheme to produce the instance (if required)
        if (xmlClass.getName().equals("class-scheme") && xmlClass.getAttributeMap().containsKey("use-scheme"))
        {
            //determine the schemeId for the Scheme to use
            String schemeId = xmlClass.getAttribute("use-scheme").getString();

            try
            {
                //locate the scheme
                ClassScheme<?> classScheme = (ClassScheme<?>) getResource(SchemeRegistry.class).getScheme(schemeId);

                //locate the cache mapping
                CacheMapping cacheMapping = getResource(CacheMappingRegistry.class).findCacheMapping(
                    info.getCacheName());

                //determine the parameter scope for the cache mapping
                MutableParameterScope parameterScope = new NestedParameterScope(cacheMapping == null
                        ? SystemPropertyParameterScope.INSTANCE : cacheMapping.getParameterScope());

                //add the standard coherence parameters to the parameter resolver
                parameterScope.addParameter(new Parameter("cache-name", info.getCacheName()));
                parameterScope.addParameter(new Parameter("class-loader", loader));
                parameterScope.addParameter(new Parameter("manager-context", context));

                //realize an instance of the scheme with the resolver
                return classScheme.realize(this, loader, parameterScope);
            }
            catch (ClassCastException classCastException)
            {
                throw new RuntimeException(String.format(
                    "Cound not instantiate %s as the namespace did not return a ClassScheme.", xmlClass),
                    classCastException);
            }
        }
        else
        {
            return super.instantiateAny(info, xmlClass, context, loader);
        }
    }


    /**
     * <p>A {@link SingletonResourceResolver} is a {@link ResourceResolver} that will always resolve
     * to a single resource instance (specified when constructing the {@link SingletonResourceResolver}).</p>
     */
    private static class SingletonResourceResolver<R> implements ResourceResolver<R>
    {

        /**
         * <p>The resource that will be resolved when this {@link ResourceResolver} is used.</p>
         */
        private R resource;


        /**
         * <p>Standard Constructor.</p>
         * 
         * @param resource The resource that will be resolved when this {@link ResourceResolver} is used.
         */
        public SingletonResourceResolver(R resource)
        {
            this.resource = resource;
        }


        /**
         * {@inheritDoc}
         */
        public R resolveResource(Object... params)
        {
            if (params.length == 0)
            {
                return resource;

            }
            else
            {
                return null;
            }
        }
    }
}
