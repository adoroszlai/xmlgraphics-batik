/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import java.net.URL;

import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.ValueManager;

import org.apache.batik.css.engine.value.css2.*;
import org.apache.batik.css.engine.value.svg.*;

import org.apache.batik.css.parser.ExtendedParser;

import org.apache.batik.util.CSSConstants;

import org.w3c.dom.Document;

/**
 * This class provides a CSS engine initialized for SVG.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGCSSEngine extends CSSEngine {
    
    /**
     * Creates a new SVGCSSEngine.
     * @param doc The associated document.
     * @param uri The document URI.
     * @param p The CSS parser to use.
     * @param ctx The CSS context.
     */
    public SVGCSSEngine(Document doc,
                        URL uri,
                        ExtendedParser p,
                        CSSContext ctx) {
        super(doc, uri, p,
              SVG_VALUE_MANAGERS,
              SVG_SHORTHAND_MANAGERS,
              null,
              null,
              "style",
              true,
              null,
              ctx);
        // SVG defines line-height to be font-size.
        lineHeightIndex = fontSizeIndex;
    }

    /**
     * Creates a new SVGCSSEngine.
     * @param doc The associated document.
     * @param uri The document URI.
     * @param p The CSS parser to use.
     * @param vms Extension value managers.
     * @param sms Extension shorthand managers.
     * @param ctx The CSS context.
     */
    public SVGCSSEngine(Document doc,
                        URL uri,
                        ExtendedParser p,
                        ValueManager[] vms,
                        ShorthandManager[] sms,
                        CSSContext ctx) {
        super(doc, uri, p,
              mergeArrays(SVG_VALUE_MANAGERS, vms),
              mergeArrays(SVG_SHORTHAND_MANAGERS, sms),
              null,
              null,
              "style",
              true,
              null,
              ctx);
        // SVG defines line-height to be font-size.
        lineHeightIndex = fontSizeIndex;
    }

    /**
     * Merges the given arrays.
     */
    private static ValueManager[] mergeArrays(ValueManager[] a1,
                                              ValueManager[] a2) {
        ValueManager[] result = new ValueManager[a1.length + a2.length];
        for (int i = 0; i < a1.length; i++) {
            result[i] = a1[i];
        }
        for (int i = 0; i < a2.length; i++) {
            result[i + a1.length] = a2[i];
        }
        return result;
    }

    /**
     * Merges the given arrays.
     */
    private static ShorthandManager[] mergeArrays(ShorthandManager[] a1,
                                                  ShorthandManager[] a2) {
        ShorthandManager[] result =
            new ShorthandManager[a1.length + a2.length];
        for (int i = 0; i < a1.length; i++) {
            result[i] = a1[i];
        }
        for (int i = 0; i < a2.length; i++) {
            result[i + a1.length] = a2[i];
        }
        return result;
    }

    /**
     * The value managers for SVG.
     */
    public final static ValueManager[] SVG_VALUE_MANAGERS = {
        new AlignmentBaselineManager(),
        new BaselineShiftManager(),
        new ClipManager(),
        new ClipPathManager(),
        new ClipRuleManager(),

        new ColorManager(),
        new ColorInterpolationManager(),
        new ColorInterpolationFiltersManager(),
        new ColorProfileManager(),
        new ColorRenderingManager(),

        new CursorManager(),
        new DirectionManager(),
        new DisplayManager(),
        new DominantBaselineManager(),
        new EnableBackgroundManager(),

        new SVGPaintManager(CSSConstants.CSS_FILL_PROPERTY),
        new OpacityManager(CSSConstants.CSS_FILL_OPACITY_PROPERTY, true),
        new FillRuleManager(),
        new FilterManager(),
        new SVGColorManager(CSSConstants.CSS_FLOOD_COLOR_PROPERTY),

        new OpacityManager(CSSConstants.CSS_FLOOD_OPACITY_PROPERTY, false),
        new FontFamilyManager(),
        new FontSizeManager(),
        new FontSizeAdjustManager(),
        new FontStretchManager(),

        new FontStyleManager(),
        new FontVariantManager(),
        new FontWeightManager(),
        new GlyphOrientationHorizontalManager(),
        new GlyphOrientationVerticalManager(),

        new ImageRenderingManager(),
        new KerningManager(),
        new SpacingManager(CSSConstants.CSS_LETTER_SPACING_PROPERTY),
        new SVGColorManager(CSSConstants.CSS_LIGHTING_COLOR_PROPERTY,
                            ValueConstants.WHITE_RGB_VALUE),
        new MarkerManager(CSSConstants.CSS_MARKER_END_PROPERTY),

        new MarkerManager(CSSConstants.CSS_MARKER_MID_PROPERTY),
        new MarkerManager(CSSConstants.CSS_MARKER_START_PROPERTY),
        new MaskManager(),
        new OpacityManager(CSSConstants.CSS_OPACITY_PROPERTY, false),
        new OverflowManager(),

        new PointerEventsManager(),
        new ShapeRenderingManager(),
        new SVGColorManager(CSSConstants.CSS_STOP_COLOR_PROPERTY),
        new OpacityManager(CSSConstants.CSS_STOP_OPACITY_PROPERTY, false),
        new SVGPaintManager(CSSConstants.CSS_STROKE_PROPERTY,
                            ValueConstants.NONE_VALUE),

        new StrokeDasharrayManager(),
        new StrokeDashoffsetManager(),
        new StrokeLinecapManager(),
        new StrokeLinejoinManager(),
        new StrokeMiterlimitManager(),

        new OpacityManager(CSSConstants.CSS_STROKE_OPACITY_PROPERTY, true),
        new StrokeWidthManager(),
        new TextAnchorManager(),
        new TextDecorationManager(),
        new TextRenderingManager(),

        new UnicodeBidiManager(),
        new VisibilityManager(),
        new SpacingManager(CSSConstants.CSS_WORD_SPACING_PROPERTY),
        new WritingModeManager(),
    };
    
    /**
     * The shorthand managers for SVG.
     */
    public final static ShorthandManager[] SVG_SHORTHAND_MANAGERS = {
        new MarkerShorthandManager(),
    };

    //
    // The property indexes.
    //
    public final static int ALIGNMENT_BASELINE_INDEX = 0;
    public final static int BASELINE_SHIFT_INDEX =
        ALIGNMENT_BASELINE_INDEX + 1;
    public final static int CLIP_INDEX = BASELINE_SHIFT_INDEX + 1;
    public final static int CLIP_PATH_INDEX = CLIP_INDEX +1;
    public final static int CLIP_RULE_INDEX = CLIP_PATH_INDEX + 1;


    public final static int COLOR_INDEX = CLIP_RULE_INDEX + 1;
    public final static int COLOR_INTERPOLATION_INDEX = COLOR_INDEX + 1;
    public final static int COLOR_INTERPOLATION_FILTERS_INDEX =
        COLOR_INTERPOLATION_INDEX + 1;
    public final static int COLOR_PROFILE_INDEX =
        COLOR_INTERPOLATION_FILTERS_INDEX + 1;
    public final static int COLOR_RENDERING_INDEX = COLOR_PROFILE_INDEX + 1;


    public final static int CURSOR_INDEX = COLOR_RENDERING_INDEX + 1;
    public final static int DIRECTION_INDEX = CURSOR_INDEX + 1;
    public final static int DISPLAY_INDEX = DIRECTION_INDEX + 1;
    public final static int DOMINANT_BASELINE_INDEX = DISPLAY_INDEX + 1;
    public final static int ENABLE_BACKGROUND_INDEX =
        DOMINANT_BASELINE_INDEX + 1;


    public final static int FILL_INDEX = ENABLE_BACKGROUND_INDEX + 1;
    public final static int FILL_OPACITY_INDEX = FILL_INDEX + 1;
    public final static int FILL_RULE_INDEX = FILL_OPACITY_INDEX + 1;
    public final static int FILTER_INDEX = FILL_RULE_INDEX + 1;
    public final static int FLOOD_COLOR_INDEX = FILTER_INDEX + 1;

    public final static int FLOOD_OPACITY_INDEX = FLOOD_COLOR_INDEX + 1;
    public final static int FONT_FAMILY_INDEX = FLOOD_OPACITY_INDEX + 1;
    public final static int FONT_SIZE_INDEX = FONT_FAMILY_INDEX + 1;
    public final static int FONT_SIZE_ADJUST_INDEX = FONT_SIZE_INDEX + 1;
    public final static int FONT_STRETCH_INDEX = FONT_SIZE_ADJUST_INDEX + 1;

    public final static int FONT_STYLE_INDEX = FONT_STRETCH_INDEX + 1;
    public final static int FONT_VARIANT_INDEX = FONT_STYLE_INDEX + 1;
    public final static int FONT_WEIGHT_INDEX = FONT_VARIANT_INDEX + 1;
    public final static int GLYPH_ORIENTATION_HORIZONTAL_INDEX =
        FONT_WEIGHT_INDEX + 1;
    public final static int GLYPH_ORIENTATION_VERTICAL_INDEX =
        GLYPH_ORIENTATION_HORIZONTAL_INDEX + 1;


    public final static int IMAGE_RENDERING_INDEX =
        GLYPH_ORIENTATION_VERTICAL_INDEX + 1;
    public final static int KERNING_INDEX = IMAGE_RENDERING_INDEX + 1;
    public final static int LETTER_SPACING_INDEX = KERNING_INDEX + 1;
    public final static int LIGHTING_COLOR_INDEX = LETTER_SPACING_INDEX + 1;
    public final static int MARKER_END_INDEX = LIGHTING_COLOR_INDEX + 1;


    public final static int MARKER_MID_INDEX = MARKER_END_INDEX + 1;
    public final static int MARKER_START_INDEX = MARKER_MID_INDEX + 1;
    public final static int MASK_INDEX = MARKER_START_INDEX + 1;
    public final static int OPACITY_INDEX = MASK_INDEX + 1;
    public final static int OVERFLOW_INDEX = OPACITY_INDEX + 1;
    

    public final static int POINTER_EVENTS_INDEX = OVERFLOW_INDEX + 1;
    public final static int SHAPE_RENDERING_INDEX = POINTER_EVENTS_INDEX + 1;
    public final static int STOP_COLOR_INDEX = SHAPE_RENDERING_INDEX + 1;
    public final static int STOP_OPACITY_INDEX = STOP_COLOR_INDEX + 1;
    public final static int STROKE_INDEX = STOP_OPACITY_INDEX + 1;


    public final static int STROKE_DASHARRAY_INDEX = STROKE_INDEX + 1;
    public final static int STROKE_DASHOFFSET_INDEX =
        STROKE_DASHARRAY_INDEX + 1;
    public final static int STROKE_LINECAP_INDEX = STROKE_DASHOFFSET_INDEX + 1;
    public final static int STROKE_LINEJOIN_INDEX = STROKE_LINECAP_INDEX + 1;
    public final static int STROKE_MITERLIMIT_INDEX =
        STROKE_LINEJOIN_INDEX + 1;
    

    public final static int STROKE_OPACITY_INDEX = STROKE_MITERLIMIT_INDEX + 1;
    public final static int STROKE_WIDTH_INDEX = STROKE_OPACITY_INDEX + 1;
    public final static int TEXT_ANCHOR_INDEX = STROKE_WIDTH_INDEX + 1;
    public final static int TEXT_DECORATION_INDEX = TEXT_ANCHOR_INDEX + 1;
    public final static int TEXT_RENDERING_INDEX = TEXT_DECORATION_INDEX + 1;


    public final static int UNICODE_BIDI_INDEX = TEXT_RENDERING_INDEX + 1;
    public final static int VISIBILITY_INDEX = UNICODE_BIDI_INDEX + 1;
    public final static int WORD_SPACING_INDEX = VISIBILITY_INDEX + 1;
    public final static int WRITING_MODE_INDEX = WORD_SPACING_INDEX + 1;

}