package deltageek.eu4.util;

import java.awt.geom.Point2D;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MapUtilities {
    public final static Charset ISO_CHARSET = Charset.forName("ISO-8859-1");
    public final static Charset UTF_CHARSET = Charset.forName("UTF-8");

    public static List<Coordinate> getPointsBetween(Point2D source, Point2D destination){
        List<Coordinate> points = new ArrayList<>();
        int xs = (int) source.getX();
        int ys = (int) source.getY();

        int xd = (int) destination.getX();
        int yd = (int) destination.getY();

        int w = xd - xs ;
        int h = yd - ys ;
        int dx1 = 0, dy1 = 0, dxd = 0, dyd = 0 ;
        if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
        if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
        if (w<0) dxd = -1 ; else if (w>0) dxd = 1 ;
        int longest = Math.abs(w) ;
        int shortest = Math.abs(h) ;
        if (!(longest>shortest)) {
            longest = Math.abs(h) ;
            shortest = Math.abs(w) ;
            if (h<0) dyd = -1 ; else if (h>0) dyd = 1 ;
            dxd = 0 ;
        }
        int numerator = longest >> 1 ;
        for (int i=0;i<=longest;i++) {
            points.add(new Coordinate(xs, ys));
            numerator += shortest ;
            if (!(numerator<longest)) {
                numerator -= longest ;
                xs += dx1 ;
                ys += dy1 ;
            } else {
                xs += dxd ;
                ys += dyd ;
            }
        }

        return points;
    }
}
