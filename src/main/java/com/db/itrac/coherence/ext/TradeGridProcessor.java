package com.db.itrac.coherence.ext;

import com.oracle.coherence.common.identifiers.Identifier;
import com.oracle.coherence.patterns.messaging.MessagingSession;
import com.tangosol.io.pof.reflect.PofValue;
import com.tangosol.io.pof.reflect.SimplePofPath;
import com.tangosol.util.BinaryEntry;

public class TradeGridProcessor extends AbstractBinaryProcessor {

    public TradeGridProcessor(int[] autoIdx, int[] nearIdx, MessagingSession s, Identifier id) {
        super();
        this.nearMatch = new MultiPofPath(new SimplePofPath(autoIdx), new SimplePofPath(nearIdx));
        this.autoMatch = new MultiPofPath(new SimplePofPath(autoIdx));
        this.session = s;
        this.destinationIdentifier = id;
    }

    final MultiPofPath nearMatch;
    final MultiPofPath autoMatch;
    final MessagingSession session;
    final Identifier destinationIdentifier;

    @Override
    protected Object process(BinaryEntry entry) {
        
        if (entry == null)
            return null;
        
        PofValue v = getPofValue(entry.getBinaryValue());
        PofValue pvAutoMatch = autoMatch.navigate(v);
        session.publishMessage(destinationIdentifier, pvAutoMatch.getValue());
        PofValue pvNearMatch = nearMatch.navigate(v);
        session.publishMessage(destinationIdentifier, pvNearMatch.getValue());
        return entry.getValue();
    }

}
