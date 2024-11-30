
package com.document.render.office.thirdpart.emf;

import com.document.render.office.thirdpart.emf.data.AbortPath;
import com.document.render.office.thirdpart.emf.data.AlphaBlend;
import com.document.render.office.thirdpart.emf.data.AngleArc;
import com.document.render.office.thirdpart.emf.data.Arc;
import com.document.render.office.thirdpart.emf.data.ArcTo;
import com.document.render.office.thirdpart.emf.data.BeginPath;
import com.document.render.office.thirdpart.emf.data.BitBlt;
import com.document.render.office.thirdpart.emf.data.Chord;
import com.document.render.office.thirdpart.emf.data.CloseFigure;
import com.document.render.office.thirdpart.emf.data.CreateBrushIndirect;
import com.document.render.office.thirdpart.emf.data.CreateDIBPatternBrushPt;
import com.document.render.office.thirdpart.emf.data.CreatePen;
import com.document.render.office.thirdpart.emf.data.DeleteObject;
import com.document.render.office.thirdpart.emf.data.EMFPolygon;
import com.document.render.office.thirdpart.emf.data.EMFRectangle;
import com.document.render.office.thirdpart.emf.data.EOF;
import com.document.render.office.thirdpart.emf.data.Ellipse;
import com.document.render.office.thirdpart.emf.data.EndPath;
import com.document.render.office.thirdpart.emf.data.ExcludeClipRect;
import com.document.render.office.thirdpart.emf.data.ExtCreateFontIndirectW;
import com.document.render.office.thirdpart.emf.data.ExtCreatePen;
import com.document.render.office.thirdpart.emf.data.ExtFloodFill;
import com.document.render.office.thirdpart.emf.data.ExtSelectClipRgn;
import com.document.render.office.thirdpart.emf.data.ExtTextOutA;
import com.document.render.office.thirdpart.emf.data.ExtTextOutW;
import com.document.render.office.thirdpart.emf.data.FillPath;
import com.document.render.office.thirdpart.emf.data.FlattenPath;
import com.document.render.office.thirdpart.emf.data.GDIComment;
import com.document.render.office.thirdpart.emf.data.GradientFill;
import com.document.render.office.thirdpart.emf.data.IntersectClipRect;
import com.document.render.office.thirdpart.emf.data.LineTo;
import com.document.render.office.thirdpart.emf.data.ModifyWorldTransform;
import com.document.render.office.thirdpart.emf.data.MoveToEx;
import com.document.render.office.thirdpart.emf.data.OffsetClipRgn;
import com.document.render.office.thirdpart.emf.data.Pie;
import com.document.render.office.thirdpart.emf.data.PolyBezier;
import com.document.render.office.thirdpart.emf.data.PolyBezier16;
import com.document.render.office.thirdpart.emf.data.PolyBezierTo;
import com.document.render.office.thirdpart.emf.data.PolyBezierTo16;
import com.document.render.office.thirdpart.emf.data.PolyDraw;
import com.document.render.office.thirdpart.emf.data.PolyDraw16;
import com.document.render.office.thirdpart.emf.data.PolyPolygon;
import com.document.render.office.thirdpart.emf.data.PolyPolygon16;
import com.document.render.office.thirdpart.emf.data.PolyPolyline;
import com.document.render.office.thirdpart.emf.data.PolyPolyline16;
import com.document.render.office.thirdpart.emf.data.Polygon16;
import com.document.render.office.thirdpart.emf.data.Polyline;
import com.document.render.office.thirdpart.emf.data.Polyline16;
import com.document.render.office.thirdpart.emf.data.PolylineTo;
import com.document.render.office.thirdpart.emf.data.PolylineTo16;
import com.document.render.office.thirdpart.emf.data.RealizePalette;
import com.document.render.office.thirdpart.emf.data.ResizePalette;
import com.document.render.office.thirdpart.emf.data.RestoreDC;
import com.document.render.office.thirdpart.emf.data.RoundRect;
import com.document.render.office.thirdpart.emf.data.SaveDC;
import com.document.render.office.thirdpart.emf.data.ScaleViewportExtEx;
import com.document.render.office.thirdpart.emf.data.ScaleWindowExtEx;
import com.document.render.office.thirdpart.emf.data.SelectClipPath;
import com.document.render.office.thirdpart.emf.data.SelectObject;
import com.document.render.office.thirdpart.emf.data.SelectPalette;
import com.document.render.office.thirdpart.emf.data.SetArcDirection;
import com.document.render.office.thirdpart.emf.data.SetBkColor;
import com.document.render.office.thirdpart.emf.data.SetBkMode;
import com.document.render.office.thirdpart.emf.data.SetBrushOrgEx;
import com.document.render.office.thirdpart.emf.data.SetICMMode;
import com.document.render.office.thirdpart.emf.data.SetMapMode;
import com.document.render.office.thirdpart.emf.data.SetMapperFlags;
import com.document.render.office.thirdpart.emf.data.SetMetaRgn;
import com.document.render.office.thirdpart.emf.data.SetMiterLimit;
import com.document.render.office.thirdpart.emf.data.SetPixelV;
import com.document.render.office.thirdpart.emf.data.SetPolyFillMode;
import com.document.render.office.thirdpart.emf.data.SetROP2;
import com.document.render.office.thirdpart.emf.data.SetStretchBltMode;
import com.document.render.office.thirdpart.emf.data.SetTextAlign;
import com.document.render.office.thirdpart.emf.data.SetTextColor;
import com.document.render.office.thirdpart.emf.data.SetViewportExtEx;
import com.document.render.office.thirdpart.emf.data.SetViewportOrgEx;
import com.document.render.office.thirdpart.emf.data.SetWindowExtEx;
import com.document.render.office.thirdpart.emf.data.SetWindowOrgEx;
import com.document.render.office.thirdpart.emf.data.SetWorldTransform;
import com.document.render.office.thirdpart.emf.data.StretchDIBits;
import com.document.render.office.thirdpart.emf.data.StrokeAndFillPath;
import com.document.render.office.thirdpart.emf.data.StrokePath;
import com.document.render.office.thirdpart.emf.data.WidenPath;
import com.document.render.office.thirdpart.emf.io.TagSet;


public class EMFTagSet extends TagSet {

    public EMFTagSet(int version) {
        if (version >= 1) {

            addTag(new PolyBezier());
            addTag(new EMFPolygon());
            addTag(new Polyline());
            addTag(new PolyBezierTo());
            addTag(new PolylineTo());
            addTag(new PolyPolyline());
            addTag(new PolyPolygon());
            addTag(new SetWindowExtEx());
            addTag(new SetWindowOrgEx());
            addTag(new SetViewportExtEx());
            addTag(new SetViewportOrgEx());
            addTag(new SetBrushOrgEx());
            addTag(new EOF());
            addTag(new SetPixelV());
            addTag(new SetMapperFlags());
            addTag(new SetMapMode());
            addTag(new SetBkMode());
            addTag(new SetPolyFillMode());
            addTag(new SetROP2());
            addTag(new SetStretchBltMode());
            addTag(new SetTextAlign());

            addTag(new SetTextColor());
            addTag(new SetBkColor());
            addTag(new OffsetClipRgn());
            addTag(new MoveToEx());
            addTag(new SetMetaRgn());
            addTag(new ExcludeClipRect());
            addTag(new IntersectClipRect());
            addTag(new ScaleViewportExtEx());
            addTag(new ScaleWindowExtEx());
            addTag(new SaveDC());
            addTag(new RestoreDC());
            addTag(new SetWorldTransform());
            addTag(new ModifyWorldTransform());
            addTag(new SelectObject());
            addTag(new CreatePen());
            addTag(new CreateBrushIndirect());
            addTag(new DeleteObject());
            addTag(new AngleArc());
            addTag(new Ellipse());
            addTag(new EMFRectangle());
            addTag(new RoundRect());
            addTag(new Arc());
            addTag(new Chord());
            addTag(new Pie());
            addTag(new SelectPalette());


            addTag(new ResizePalette());
            addTag(new RealizePalette());
            addTag(new ExtFloodFill());
            addTag(new LineTo());
            addTag(new ArcTo());
            addTag(new PolyDraw());
            addTag(new SetArcDirection());
            addTag(new SetMiterLimit());
            addTag(new BeginPath());
            addTag(new EndPath());
            addTag(new CloseFigure());
            addTag(new FillPath());
            addTag(new StrokeAndFillPath());
            addTag(new StrokePath());
            addTag(new FlattenPath());
            addTag(new WidenPath());
            addTag(new SelectClipPath());
            addTag(new AbortPath());

            addTag(new GDIComment());




            addTag(new ExtSelectClipRgn());
            addTag(new BitBlt());




            addTag(new StretchDIBits());
            addTag(new ExtCreateFontIndirectW());
            addTag(new ExtTextOutA());
            addTag(new ExtTextOutW());
            addTag(new PolyBezier16());
            addTag(new Polygon16());
            addTag(new Polyline16());
            addTag(new PolyBezierTo16());
            addTag(new PolylineTo16());
            addTag(new PolyPolyline16());
            addTag(new PolyPolygon16());
            addTag(new PolyDraw16());

            addTag(new CreateDIBPatternBrushPt());
            addTag(new ExtCreatePen());




            addTag(new SetICMMode());

















            addTag(new AlphaBlend());



            addTag(new GradientFill());


        }
    }
}
