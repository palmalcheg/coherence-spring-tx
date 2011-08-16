package com.oracle.coherence.common.ranges;

/**
* If two ContiguousRanges aren't comparable, e.g. if they are intersecting, this Runtime exception is thrown. 
* @author christer.fahlgren@oracle.com
*
*/
@SuppressWarnings("serial")
public class NotComparableRuntimeException extends RuntimeException
    {

    }
