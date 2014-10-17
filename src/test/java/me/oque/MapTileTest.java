package me.oque;

import org.junit.Test;
import me.oque.MapTile;

import static org.junit.Assert.*;

public class MapTileTest {
    static MapTile m;

    @Test(expected=IllegalArgumentException.class)
    public void MapTile() {
        m = new MapTile(-91.0,0.0,10);
    }

    @Test(expected=IllegalArgumentException.class)
    public void MapTile2() {
        m = new MapTile(91.0,0.0,10);
    }

    @Test(expected=IllegalArgumentException.class)
    public void MapTile3() {
        m = new MapTile(0.0,-181.0,10);
    }

    @Test(expected=IllegalArgumentException.class)
    public void MapTile4() {
        m = new MapTile(0.0,181.0,10);
    }

    @Test
    public void testTileURL() throws Exception {
        m = new MapTile(56.0,37.0,10);
        assertEquals("http://khms", m.tileURL().substring(0, 11));
        assertEquals(".google.com/kh/v=149&src=app&x=", m.tileURL().substring(12, 43));
    }
}