package com.db.itrac.coherence.ext;

import java.util.StringTokenizer;

import com.oracle.coherence.configuration.Configurator;
import com.oracle.coherence.environment.Environment;
import com.oracle.coherence.patterns.pushreplication.PublishingServiceManager;
import com.oracle.coherence.patterns.pushreplication.PushReplicationProvider;
import com.oracle.coherence.patterns.pushreplication.configuration.PushReplicationNamespaceContentHandler;
import com.oracle.coherence.patterns.pushreplication.providers.coherence.CoherencePushReplicationProvider;
import com.oracle.coherence.patterns.pushreplication.publishers.supervision.ResourceSupervisors;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.ConfigurableCacheFactory;
import com.tangosol.net.Invocable;
import com.tangosol.net.InvocationService;
import com.tangosol.net.management.Registry;
import com.tangosol.run.xml.SimpleElement;
import com.tangosol.run.xml.XmlDocument;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlHelper;

public class SubscriberTask implements Invocable {

    private String hostPort, subscriber;

    @Override
    public Object getResult() {
        return null;
    }

    public SubscriberTask(String hostPort, String subscriber) {
        super();
        this.hostPort = hostPort;
        this.subscriber = subscriber;
    }

    @Override
    public void init(InvocationService is) {
    }

    @Override
    public void run() {

        ConfigurableCacheFactory factory = CacheFactory.getConfigurableCacheFactory();

        XmlElement root = factory.getConfig();
        root.addAttribute("xmlns:sync").setString("class:" + PushReplicationNamespaceContentHandler.class.getName());

        XmlElement cS = root.findElement("caching-schemes");
        XmlElement riS = null;
        XmlElement rA = null;
        
        riS = cS.findElement("remote-invocation-scheme/service-name");
        if (riS != null && riS.getString().equals(subscriber)) {
            rA = riS.getParent().findElement("initiator-config/tcp-initiator/remote-addresses");
            XmlHelper.removeElement(rA, "socket-address");
        } else {
            riS = cS.addElement("remote-invocation-scheme");
            riS.addElement("scheme-name").setString(subscriber);
            riS.addElement("service-name").setString(subscriber);
            XmlElement iC = riS.addElement("initiator-config");
            XmlElement tI = iC.addElement("tcp-initiator");
            rA = tI.addElement("remote-addresses");
        }
        
        StringTokenizer tokens = new StringTokenizer(hostPort, ",:", false);
        while (tokens.hasMoreTokens()) {
            XmlElement sA = rA.addElement("socket-address");
            sA.addElement("address").setString(tokens.nextToken());
            sA.addElement("port").setInt(Integer.parseInt(tokens.nextToken()));
        }
        
        factory.setConfig(root);
    }

}