/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.w3c.dom.Element;
import org.apache.batik.gvt.Mask;

/**
 * Factory class for vending <tt>Mask</tt> objects.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface MaskBridge extends Bridge {

    /**
     * Creates a <tt>Mask</tt> using the specified context and element.
     * @param ctx the context to use
     * @param element the Element with the 'mask' attribute
     */
    Mask createMask(BridgeContext ctx, Element element);

    /**
     * Updates an Element coresponding to the specified BridgeMutationEvent.
     * @param evt the event that describes the modification to perform
     */
    void update(BridgeMutationEvent evt);

}
