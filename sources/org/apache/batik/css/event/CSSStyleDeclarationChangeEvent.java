/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.event;

/**
 * This class represents the events which get fired whenever a
 * CSS style declaration is changed.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSStyleDeclarationChangeEvent {
    /**
     * The source of this event.
     */
    protected Object source;

    /**
     * Creates a new CSSStyleDeclarationChangeEvent object.
     * @param source The source of this event.
     */
    protected CSSStyleDeclarationChangeEvent(Object source) {
	this.source = source;
    }

    /**
     * Returns the event source (ie. the style declaration).
     */
    public Object getSource() {
	return source;
    }
}
