package edu.cs895.interest;

// code taken from http://answers.google.com/answers/threadview/id/785251.html
// written by leapinglizard

// auxiliary class to store coordinates of each vertex
class Coordinate {
    // state data for a coordinate pair (x, y)
    private double x; //latitude
    private double y; //longitude

    // constructs a point from specified coordinate values
    public Coordinate(double inX, double inY) {
        x = inX;
        y = inY;
    }

    // copy constructor
    public Coordinate(Coordinate toBeCopied) {
        x = toBeCopied.getX();
        y = toBeCopied.getY();
    }

    // access x value
    public double getX() {
        return x;
    }

    // access y value
    public double getY() {
        return y;
    }

    // calculates distance between target and this point
    public double distanceTo(Coordinate target) {
        double deltaX = x - target.getX();
        double deltaY = y - target.getY();
        double hypotSquared = (deltaX * deltaX) + (deltaY * deltaY);
        return (Math.sqrt(hypotSquared));
    }

    // pretty-prints the coordinate pair
    public String toString() {
        return ("(" + x + ", " + y + ")");
    }
}


// auxiliary class to help with circumcircle and incircle calculations
class Circle {
    // state data for a circle of given radius centered on a given origin
    private Coordinate origin;
    private double radius;

    public Coordinate getOrigin() {
        return origin;
    }

    public double getRadius() {
        return radius;
    }

    // copy constructor
    public Circle(Coordinate newOrigin, double newRadius) {
        origin = newOrigin;
        radius = newRadius;
    }

}
    

public class LLTriangle {
    // state data for a triangle with vertices a, b, c
    private Coordinate a, b, c;

    // constructs a triangle with the given vertices
    public LLTriangle(Coordinate newA, Coordinate newB, Coordinate newC) {
        a = newA;
        b = newB;
        c = newC;
    }

    // access each vertex
    public Coordinate getVertexA() {
        return a;
    }
    public Coordinate getVertexB() {
        return b;
    }
    public Coordinate getVertexC() {
        return c; 
    }

    // calculate the length of the side opposite vertex a 
    public double getSideA() {
        // get the distance between vertices b and c
        double length = b.distanceTo(c);
        return length;
    }

    // calculate the length of the side opposite vertex b
    public double getSideB() {
        // get the distance between vertices a and c
        double length = a.distanceTo(c);
        return length;
    } 
    
    // calculate the length of the side opposite vertex c
    public double getSideC() {
        // get the distance between vertices a and b
        double length = a.distanceTo(b);
        return length;
    }

    // calculate the angle at vertex a
    public double getAngleA() {
        // get the length of each side
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        // apply the law of cosines
        double angle = Math.acos((bs*bs + cs*cs - as*as) / (2*bs*cs));
        return angle;
    }
    
    // calculate the angle at vertex b
    public double getAngleB() {
        // get the length of each side
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        // apply the law of cosines
        double angle = Math.acos((as*as + cs*cs - bs*bs) / (2*as*cs));
        return angle;
    }

    // calculate the angle at vertex c
    public double getAngleC() {
        // get the length of each side
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        // apply the law of cosines
        double angle = Math.acos((as*as + bs*bs - cs*cs) / (2*as*bs));
        return angle;
    }

    // check whether no side is longer than the other two sides put together
    public boolean isValid() {
        // get the length of each side
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        if (as > (bs + cs))
            return false;
        if (bs > (as + cs))
            return false;
        if (cs > (as + bs))
            return false;
        return true;
    }

    // check whether all three sides differ in length
    public boolean isScalene() {
        // get the length of each side
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        if (as == bs)
            return false;  // not scalene if two sides are equal in length
        if (as == cs)
            return false;
        if (bs == cs)
            return false;
        return true;
    }

    // check whether two sides are equal in length
    public boolean isIsosceles() {
        // get the length of each side
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        if (as == bs)
            return true;  // isosceles if two sides are equal in length
        if (as == cs)
            return true;
        if (bs == cs)
            return true;
        return false;
    }

    // check whether all three sides are equal in length
    public boolean isEquilateral() {
        // get the length of each side
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        if (as != bs)
            return false;  // not equilateral if two sides differ in length
        if (as != cs)
            return false;
        if (bs != cs)
            return false;
        return true;
    }

    // calculate the sum of the lengths of the sides
    public double getPerimeter() {
        // get the length of each side
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        return (as + bs + cs);
    }
    
    // calculate the signed area
    double getSignedArea() {
        double signedArea = 0.5 * (a.getX() * (b.getY() - c.getY()) +
                                   b.getX() * (c.getY() - a.getY()) +
                                   c.getX() * (a.getY() - b.getY()));
        return signedArea;
    }

    // calculate the absolute area
    public double getArea() {
        return Math.abs(getSignedArea());
    }

    // determine orientation based on the signed area 
    public int getOrientation() {
        double signedArea = getSignedArea();
        if (signedArea > 0.0)
            return 1;
        if (signedArea < 0.0)
            return -1;
        return 0;
    }

    // pretty-print the coordinates inside square brackets
    public String toString() { 
        return ("["+a+",\n "+b+",\n "+c+"]");
    }

    // do the coordinates of the vertices a, b, c match up in order?
    // note that we are not checking all 6 orderings of a, b, c
    public boolean equals(Object o) {
        LLTriangle triangle = (LLTriangle) o;
        if (triangle.getVertexA() != a)
            return false;
        if (triangle.getVertexB() != b)
            return false;
        if (triangle.getVertexC() != c)
            return false;
        return true;
    }

    // check whether a given point falls inside the triangle
    public boolean contains(Coordinate p) {
        int orientation = (new LLTriangle(b, c, p)).getOrientation();
        if ((new LLTriangle(a, b, p)).getOrientation() != orientation)
            return false;
        if (orientation != (new LLTriangle(b, c, p)).getOrientation())
            return false;
        return true;
    }
    
    // converts trilinear coordinates to Cartesian coordinates relative
    // to the incenter; thus, the incenter has coordinates (0.0, 0.0)
    public Coordinate toCartesian(double alpha, double beta, double gamma) {
        double area = getArea();
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        double r = 2 * area / (as + bs + cs);
        double k = 2 * area / (as*alpha + bs*beta + cs*gamma);
        double cosC = Math.cos(getAngleC()), sinC = Math.sin(getAngleC());
        double x = (k*beta - r + (k*alpha - r)*cosC) / sinC;
        double y = k*alpha - r;
        return new Coordinate(x, y);
    }

    // calculates the circumradius
    public Circle getCircumcircle() {
        double cosA = Math.cos(getAngleA());
        double cosB = Math.cos(getAngleB());
        double cosC = Math.cos(getAngleC());
        Coordinate center = toCartesian(cosA, cosB, cosC);
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        double s = 0.5 * (as + bs + cs);
        double radius = (as * bs * cs) / (4 * Math.sqrt(
            s * (as + bs - s) * (as + cs - s) * (bs + cs - s)));
        return new Circle(center, radius);
    }

    // calculates the inradius
    public Circle getIncircle() {
        Coordinate center = toCartesian(1.0, 1.0, 1.0);
        double as = getSideA(), bs = getSideB(), cs = getSideC();
        double semiperimeter = 0.5 * (as + bs + cs);
        double radius = getArea() / semiperimeter;
        return new Circle(center, radius);
    }    
}