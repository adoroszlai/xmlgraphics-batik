/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

/**
 * This is a typesafe enumeration of the standard Composite rules for
 * the CompositeRable operation. (over, in, out, atop, xor, arith)
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public final class CompositeRule implements java.io.Serializable {
      /** Porter-Duff src over rule. */
    public static final int RULE_OVER = 1;

      /** Porter-Duff src in rule. */
    public static final int RULE_IN = 2;

      /** Porter-Duff src out rule. */
    public static final int RULE_OUT = 3;

      /** Porter-Duff src atop rule. */
    public static final int RULE_ATOP = 4;

      /** Porter-Duff src xor rule. */
    public static final int RULE_XOR = 5;

      /** Arithmatic rule 'out = k1*i1*i2 + k2*i1 + k3*i2 + k4'. */
    public static final int RULE_ARITHMATIC = 6;

      /**
       * Porter-Duff Source Over Destination rule. The source is
       * composited over the destination.<pre>
       *
       *  Fs = 1 and Fd = (1-As), thus:
       *
       *        Cd = Cs + Cd*(1-As)
       *        Ad = As + Ad*(1-As)</pre>
       */
    public static final CompositeRule OVER = new CompositeRule(RULE_OVER);

      /**
       * Porter-Duff Source In Destination rule. The part of the
       * source lying inside of the destination replaces the destination.<pre>
       *
       *  Fs = Ad and Fd = 0, thus:
       *
       *        Cd = Cs*Ad
       *        Ad = As*Ad
       */
    public static final CompositeRule IN = new CompositeRule(RULE_IN);

      /**
       * Porter-Duff Source Out Destination rule. The part of the
       * source lying outside of the destination replaces the destination.<pre>
       *
       *  Fs = (1-Ad) and Fd = 0, thus:
       *
       *        Cd = Cs*(1-Ad)
       *        Ad = As*(1-Ad)
       */
    public static final CompositeRule OUT = new CompositeRule(RULE_OUT);

      /**
       * Porter-Duff Source Atop Destination rule. The part of the
       * source lying inside of the destination replaces the destination,
       * destination remains outside of source.<pre>
       *
       *  Fs = Ad and Fd = (1-As), thus:
       *
       *        Cd = Cs*Ad + Cd*(1-As)
       *        Ad = As*Ad + Ad*(1-As)
       * NOTE: May not be right!!!!
       */
    public static final CompositeRule ATOP = new CompositeRule(RULE_ATOP);

      /**
       * Xor rule. The source and destination are Xor'ed togeather.<pre>
       * NOTE: Need to spec this out...
       */
    public static final CompositeRule XOR = new CompositeRule(RULE_XOR);

      /**
       * Factory to create artithmatic CompositeRules.
       * 'out = k1*i1*i2 + k2*i1 + k3*i2 + k4'
       * Note that arithmatic CompositeRules are not singletons.
       */
    public static CompositeRule ARITHMATIC
        (float k1, float k2, float k3, float k4) {
        if      (k1 < 0)   k1 = 0;
        else if (k1 > 1.0) k1 = 1.0f;

        if      (k2 < 0)   k2 = 0;
        else if (k2 > 1.0) k2 = 1.0f;

        if      (k3 < 0)   k3 = 0;
        else if (k3 > 1.0) k3 = 1.0f;

        if      (k4 < 0)   k4 = 0;
        else if (k4 > 1.0) k4 = 1.0f;

        return new CompositeRule(k1, k2, k3, k4);
    }

    /**
     * Returns the type of this composite rule
     */
    public int getRule() {
        return rule;
    }

      /**
       * The composite rule for this object.
       */
    private int rule;

      /* Arithmatic constants, only used for RULE_ARITHMATIC */
    private float k1, k2, k3, k4;

    private CompositeRule(int rule) {
        this.rule = rule;
    }

    private CompositeRule(float k1, float k2, float k3, float k4) {
        rule = RULE_ARITHMATIC;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.k4 = k4;
    }

    /**
     * This is called by the serialization code before it returns
     * an unserialized object. To provide for unicity of
     * instances, the instance that was read is replaced by its
     * static equivalent. See the serialiazation specification for
     * further details on this method's logic.
     */
    private Object readResolve() throws java.io.ObjectStreamException {
        switch(rule){
        case RULE_OVER:
            return OVER;
        case RULE_IN:
            return IN;
        case RULE_OUT:
            return OUT;
        case RULE_ATOP:
            return ATOP;
        case RULE_XOR:
            return XOR;
        case RULE_ARITHMATIC:
            return this;
        default:
            throw new Error("Unknown Composite Rule type");
        }
    }

}
