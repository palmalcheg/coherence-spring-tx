package com.db.itrac.coherence.ext;
import java.io.IOException;
import java.util.Arrays;

import com.tangosol.io.ByteArrayWriteBuffer;
import com.tangosol.io.ReadBuffer;
import com.tangosol.io.WriteBuffer;
import com.tangosol.io.pof.PofConstants;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.reflect.AbstractPofValue;
import com.tangosol.io.pof.reflect.PofNavigator;
import com.tangosol.io.pof.reflect.PofValue;
import com.tangosol.io.pof.reflect.PofValueParser;
import com.tangosol.util.Binary;

public class MultiPofPath implements PofNavigator, PortableObject {
 
    private PofNavigator[] paths;    
 
    public MultiPofPath(PofNavigator... paths) {
        this.paths = paths;
    }
 
    @Override
    public PofValue navigate(PofValue pofValue) {
        PofValue result = null;
 
            try {
                int size = 0;
                PofValue[] values = new PofValue[paths.length];
 
                // Iterate over the PofNavigators passed to our constructor
                for (int p=0; p<paths.length; p++) {
                    // get the value for this path
                    values[p] = paths[p].navigate(pofValue);
                    if (values[p] != null) {
                        size += ((AbstractPofValue)values[p]).getSize();
                    }
                }
 
                // Create the byte stream to hold the serialized results
                ByteArrayWriteBuffer buffer = new ByteArrayWriteBuffer(size);
                WriteBuffer.BufferOutput output = buffer.getBufferOutput();
 
                // Write the POF identifier for a collection
                output.writePackedInt(PofConstants.T_COLLECTION);
                // write the size of the collection
                output.writePackedInt(paths.length);
 
                // Now write the values
                for (PofValue value : values) {
                    ReadBuffer readBuffer = ((AbstractPofValue) value).getSerializedValue();
                    output.writeBuffer(readBuffer);
                }
 
                // Convert the byte stream's bytes to a POF value
                Binary binary = new Binary(buffer.getRawByteArray());
                result = PofValueParser.parse(binary
                                              , ((AbstractPofValue) pofValue).getPofContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
 
        return result;
    }
 
    @Override
    public void readExternal(PofReader pofReader) throws IOException {
        paths = (PofNavigator[]) pofReader.readObjectArray(100, new PofNavigator[0]);
    }
 
    @Override
    public void writeExternal(PofWriter pofWriter) throws IOException {
        pofWriter.writeObjectArray(100, paths);
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
 
        MultiPofPath that = (MultiPofPath) o;
 
        return Arrays.equals(paths, that.paths);
    }
 
    @Override
    public int hashCode() {
        return paths != null ? Arrays.hashCode(paths) : 0;
    }
 
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("MultiPofPath{");
        for (PofNavigator path : paths) {
            builder.append(path).append(" ");
        }
        builder.append("}");
        return builder.toString();
    }
}