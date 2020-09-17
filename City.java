
public class City {
    private int id;
    private int x;  
    private int y;

    public City(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
    
    public int distance(City city) { // Computing distance between current city and given city.
    	return (int) Math.round(Math.sqrt(Math.pow(getX() - city.getX(), 2) + Math.pow(getY() - city.getY() , 2)));
    }
    
    // Getter and setters methods.
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

}