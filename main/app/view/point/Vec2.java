package app.view.point;

/**
 * Vektor in zwei Richtungen dient als Oberklasse der Punkte
 */
public class Vec2<T extends Number> {

////////////////////////////////////////////////////////////////////////////////////////////////////attribute

    private T x;
    private T y;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set

    public T getX(){
        return this.x;
    }
    public T getY(){
        return this.y;
    }

    public void setX(T pX){
        this.x = pX;
    }
    public void setY(T pY){
        this.y = pY;
    }

    public Vec2(T pX, T pY){
        this.x = pX;
        this.y = pY;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "["+x+"|"+y+"]";
    }
}
