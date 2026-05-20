package base;

public class Coordinates {
    private double x;

    private Double y;

    public Coordinates(double x, Double y) {
        this.y = y;
        this.x = x;
    }

    public Coordinates() {}

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
