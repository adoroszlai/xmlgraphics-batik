/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.PropertyMap;
import org.apache.batik.css.value.AbstractIdentifierFactory;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.w3c.css.sac.Parser;
import org.w3c.dom.css.CSSPrimitiveValue;


/**
 * This class provides a factory for the 'color-rendering' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ColorRenderingFactory extends AbstractIdentifierFactory {
    /**
     * The 'optimizeQuality' string.
     */
    public final static String OPTIMIZEQUALITY = "optimizequality";

    /**
     * The 'optimizeQuality' keyword.
     */
    public final static ImmutableValue OPTIMIZEQUALITY_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, OPTIMIZEQUALITY);

    /**
     * The 'optimizeSpeed' string.
     */
    public final static String OPTIMIZESPEED = "optimizespeed";

    /**
     * The 'optimizeSpeed' keyword.
     */
    public final static ImmutableValue OPTIMIZESPEED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, OPTIMIZESPEED);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(AUTO,            AUTO_VALUE);
	values.put(OPTIMIZEQUALITY, OPTIMIZEQUALITY_VALUE);
	values.put(OPTIMIZESPEED,   OPTIMIZESPEED_VALUE);
    }

    /**
     * Creates a new ColorRenderingFactory object.
     */
    public ColorRenderingFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "color-rendering";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
