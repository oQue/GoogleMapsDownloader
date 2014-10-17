import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;


public class TimeoutURL implements Callable<InputStream> {

    private URL url;

    public TimeoutURL(String spec) throws MalformedURLException {
        url = new URL(spec);
    }

    @Override
    public InputStream call() throws Exception {
        return url.openStream();
    }
}
