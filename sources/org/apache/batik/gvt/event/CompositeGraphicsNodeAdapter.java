/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import java.util.EventListener;

/**
 * An abstract adapter class for receiving composite graphics node
 * events. The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 *
 * <p>Extend this class to create a <tt>CompositeGraphicsNodeEvent</tt>
 * listener and override the methods for the events of interest. (If
 * you implement the <tt>CompositeGraphicsNodeListener</tt> interface, you
 * have to define all of the methods in it. This abstract class
 * defines null methods for them all, so you can only have to define
 * methods for events you care about.)
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class CompositeGraphicsNodeAdapter
        implements CompositeGraphicsNodeListener {

    public void graphicsNodeAdded(CompositeGraphicsNodeEvent evt) {}

    public void graphicsNodeRemoved(CompositeGraphicsNodeEvent evt) {}
}
