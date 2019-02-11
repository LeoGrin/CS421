package graph;

import java.awt.Graphics;

public class Place {

    private final String name;

    // -180 < long < 180
    // -90 < lat < 90
    private final double latitude, longitude;
    static int precision=2;

    public Place(String s, double lat,double longi) {
    	name = s;
    	latitude = lat;
    	longitude = longi; 
    }

    public Place() {
		// TODO Auto-generated constructor stub
    	this("",0,0);
	}

	public String toString() {
    	return name+"\tLatitude: "+latitude+"\t"+"Longitude: "+longitude;
    }

    public String getName() {
	return name;
    }

    public double getLatitude() {
	return latitude;
    }

    public double getLongitude() {
	return longitude;
    }

    private long getRoundLatitude() {
	return Math.round(latitude*(Math.pow(10.,precision)));
    }

    private long getRoundLongitude() {
	return Math.round(longitude*(Math.pow(10.,precision)));
    }

    private int tLat(double latitude, double latmin, double latmax, int h) {
	return h-(int) ((latitude-latmin)/(latmax-latmin)*h);
    }
    private int tLong(double longitude, double lonmin, double lonmax, int w) {
	return (int) ((longitude-lonmin)/(lonmax-lonmin)*w);
    }

    public void drawEdge(Graphics g, double latmin,double lonmin, double latmax, double lonmax, int w, int h,Place d) {
	int lat1 = tLat(latitude,latmin,latmax,h);
	int lat2 = tLat(d.getLatitude(),latmin,latmax,h);
	int lon1 = tLong(longitude,lonmin,lonmax,w);
	int lon2 = tLong(d.getLongitude(),lonmin,lonmax,w);
	g.drawLine(lon1,lat1,lon2,lat2);
    }

    private double sqr(double a) {
	    return a*a;
		}

    public double distance(Place dest) {
	// utilise la distance ellipsoidale de vincenty
	double R = 6371000; // rayon de la terre
	return R*Math.sqrt(sqr((getLatitude()-dest.getLatitude()))+sqr((getLongitude()-dest.getLongitude())))/180.0*Math.PI; 
    }
    
    public void draw(Graphics g, double latmin,double lonmin, double latmax, double lonmax, int w, int h, boolean names, int radius) {
	g.fillOval(tLong(longitude,lonmin,lonmax,w),tLat(latitude,latmin,latmax,h),radius,radius);
	if (names) 
	    g.drawString(name,tLong(longitude,lonmin,lonmax,w)-5*name.length(),tLat(latitude,latmin,latmax,h)-10);
    } 
    
    public boolean equals(Object obj){
	return (obj instanceof Place)&&(getRoundLatitude()==((Place) obj).getRoundLatitude())&&(getRoundLongitude()==((Place) obj).getRoundLongitude());
    }

    public int hashCode(){
	long x=getRoundLatitude();
	long y=getRoundLongitude();
	return (int) ((x*(29*(Math.pow(10.,precision)))+y)%Integer.MAX_VALUE);
    }
}

