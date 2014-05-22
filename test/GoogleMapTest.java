import org.junit.*;
import org.junit.Assert.*;

public class GoogleMapTest {

    static GoogleMap m;

    @Test(expected=IllegalArgumentException.class)
    public void testGoogleMap() {
        m = new GoogleMap(10,10,11,9,10,"");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGoogleMap2() {
        m = new GoogleMap(10,10,9,9,10,"");
    }

    @Test
    public void testGoogleMap4() {
        m = new GoogleMap(10,10,9,11,10,"");
    }

    @Test
    public void testGetNumTiles() throws Exception {
        m = new GoogleMap(10,10,9,11,10,"");
        long n = m.getNumTiles();
        long t = m.getWidth() * m.getHeight() / (256*256);
        Assert.assertEquals(t, n);
    }
}