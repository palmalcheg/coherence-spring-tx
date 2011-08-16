/*
 * File: FilteringIterator.java
 * 
 * Copyright (c) 2009-2010. All Rights Reserved. Oracle Corporation.
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
package com.oracle.coherence.common.util;

import java.util.Iterator;

import com.tangosol.util.Filter;

/**
 * <p>A {@link FilteringIterator} will (lazily) filter the results of a provided
 * {@link Iterable} with a provided {@link Filter}.</p>
 * 
 * @author Brian Oliver
 *
 * @param <T> The type of values being iterated
 */
public class FilteringIterator<T> implements Iterator<T>
{

    /**
     * <p>The {@link Filter} to apply to each element of the wrapped {@link Iterator}.</p>
     */
    private Filter filter;

    /**
     * <p>The underlying {@link Iterator} being filtered.</p>
     */
    private Iterator<T> iterator;

    /**
     * <p>The next value to return from the {@link Iterator}.
     * (when <code>null</code> the iteration is over).</p>
     */
    private T next;


    /**
     * <p>Standard Constructor.</p>
     * 
     * @param filter The filter to filter out values in the {@link Iterator}.
     * @param iterator The underlying {@link Iterator} of values being filtered.
     */
    public FilteringIterator(Filter filter,
                             Iterator<T> iterator)
    {
        this.filter = filter;
        this.iterator = iterator;
        this.next = seekNext();
    }


    /**
     * <p>Attempts to find the next value in the underlying {@link Iterator}
     * that satisfies the provided {@link Filter}.</p>
     * 
     * @return The next value satisfying the {@link Filter}, or <code>null</code> if none available.
     */
    private T seekNext()
    {
        T next = null;
        while (iterator != null && iterator.hasNext() && next == null)
        {
            next = iterator.next();
            if (filter != null && !filter.evaluate(next))
                next = null;
        }
        return next;
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasNext()
    {
        return next != null;
    }


    /**
     * {@inheritDoc}
     */
    public T next()
    {
        T result = next;
        this.next = seekNext();
        return result;
    }


    /**
     * {@inheritDoc}
     */
    public void remove()
    {
        throw new UnsupportedOperationException("Can't remove() from a FilteringIterator");
    }
}
