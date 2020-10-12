package in.nic.mizoram.mizoramsafety;

public class DistanceCalculate {

    static final double earthRadius= 6378.13170D;
    static final double distanceRadiud=(Math.PI/180D);

    public static int myDistanceinM(double lat1,double long1,double lat2,double long2)
    {
        return (int)(1000D * myDistanceinKM(lat1,long1,lat2,long2));
    }

    public  static double myDistanceinKM(double lat1, double long1, double lat2, double long2)
    {
        double dlong=(long2-long1) * distanceRadiud;
        double dlat=(lat2-lat1) * distanceRadiud;

        double a = Math.pow(Math.sin(dlat / 2D),2D) + Math.pow(Math.sin(dlong / 2D),2D) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2D * Math.asin(Math.sqrt(a));
        double d= earthRadius * c;
        return d;

    }
}
