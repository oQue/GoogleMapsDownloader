public class MapTile {

    int x, y, z;

    MapTile(int x, int y, int zoom) {
        this.x = x;
        this.y = y;
        z = zoom;
    }

    MapTile(double lat, double lon, int zoom) {
        x = (int)(Math.pow(2, zoom)*(lon+180)/360/2);
        y = (int)(-(.5*Math.log((1+Math.sin(Math.toRadians(lat)))/(1-Math.sin(Math.toRadians(lat))))/Math.PI-1)*
                Math.pow(2, zoom-1)/2);
        z = zoom;
    }

    public String tileURL() {
        /**
         * @return full link to tile image from google maps
         */
        return "http://khms" + (int)(Math.random()*4) +
                ".google.com/kh/v=149&src=app&x=" + x + "&y=" + y + "&z=" + (z-1) + "&s=";
    }
}
