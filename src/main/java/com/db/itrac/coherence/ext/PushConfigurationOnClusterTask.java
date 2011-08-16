package com.db.itrac.coherence.ext;

import java.util.Set;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.Cluster;
import com.tangosol.net.Invocable;
import com.tangosol.net.InvocationObserver;
import com.tangosol.net.InvocationService;
import com.tangosol.net.Member;
import com.tangosol.run.xml.XmlDocument;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlHelper;

public class PushConfigurationOnClusterTask implements Invocable {

    private String configXml;
    private boolean broadcast;

    public PushConfigurationOnClusterTask(String configXml, boolean broadcastLocally) {
        super();
        this.configXml = configXml;
        this.broadcast = broadcastLocally;
    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public void init(InvocationService is) {
    }

    @Override
    public void run() {

        if (broadcast) {
            CacheFactory.setConfigurableCacheFactoryConfig(XmlHelper.loadXml(configXml));
            return;
        }

        XmlElement xmlConfig = CacheFactory.getCacheFactoryBuilderConfig();
        XmlElement el = xmlConfig.findElement("invocation-scheme/service-name");
        Cluster cluster = CacheFactory.ensureCluster();
        Set<Member> memberSet = cluster.getMemberSet();
        InvocationService local = (InvocationService) cluster.getService(el.getString());
        local.execute(new PushConfigurationOnClusterTask(configXml, true), memberSet, null);
    }

}
