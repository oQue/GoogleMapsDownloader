import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Proxy {

    private final static String IP = getOwnIP();
    private static int page = 1;
    private static ArrayList<String> proxies;
    private static int usedProxy = 0;

    private static ArrayList<String> getList() throws IOException {
        usedProxy = 0;
        // by http://javatalks.ru/topics/36952
        proxies = new ArrayList<String>();
        Document doc;
        if (page == 1) {
            doc = Jsoup.connect("http://hidemyass.com/proxy-list/").get();
        }
        else {
            doc = Jsoup.connect("http://hidemyass.com/proxy-list/" + page).get();
        }
        page++;
        Elements trs = doc.select("#listtable tbody > tr");
        for (Element tr : trs)
        {
            String port = null;
            String ip = null;

            Elements tds = tr.select("td:gt(0):lt(3)");
            for (Element td : tds)
            {
                Elements style = td.select("style");
                if(style.isEmpty())
                {
                    port = td.text();
                }
                else
                {
                    // в блоке <style></style> перечислены "невидимые" классы, они создают шум - защита от парсинга
                    String unvizClasses = style.first().data()
                            .replaceAll("\\..*?\\{display:inline\\}|\n|\r", "")
                            .replaceAll("\\{.*?\\}|^\\.", "")
                            .replaceAll("\\.", "|");

                    // Теперь получаем блок с ip удаляем мусор и шум - защиту.
                    Element els = td.select("span").first();
                    ip = els.html()
                            .replaceAll("\n|\r", "")
                            .replaceAll("\\<style\\>.*?\\</style\\>|\\<(span|div) style=\"display:none\"\\>.*?\\</(span|div)\\>", "")
                            .replaceAll("\\<(span|div) class=\"(" + unvizClasses + ")\"\\>.*?\\</(span|div)\\>", "")
                            .replaceAll("\\<.*?\\>| ", "");
                }
            }
            proxies.add(ip + ":" + port);
        }
        return proxies;
    }

    public static void setProxy() throws IOException {
        if (proxies == null)
            proxies = getList();
        boolean alreadyChanged = false;
        while (!alreadyChanged || !isWorking()) {
            System.setProperty("http.proxyHost", proxies.get(usedProxy).split(":")[0]);
            System.setProperty("http.proxyPort", proxies.get(usedProxy).split(":")[1]);
            usedProxy++;
            if (usedProxy == proxies.size()-1) {
                proxies = getList();
                usedProxy = 0;
            }
            alreadyChanged = true;
        }
    }

    private final static String getOwnIP() {
        try {
            Document doc;
            doc = Jsoup.connect("http://2ip.ru/").get();
            Element ip = doc.select("big").first();
            return ip.text();
        } catch (IOException e) {
            try {
                Document doc;
                doc = Jsoup.connect("http://www.whatismyip.com/").get();
                return doc.select(".the-ip").first().text();
            } catch (IOException ex) { return null; }
        }
    }

    private static String getIP() throws IOException {
        Document doc;
        int attempt = 0;
        while (attempt < 5) {
            try {
                doc = Jsoup.connect("http://2ip.ru/").get();
                Element ip = doc.select("big").first();
                return ip.text();
            } catch (IOException e) {
                try {
                    doc = Jsoup.connect("http://www.whatismyip.com/").get();
                    return doc.select(".the-ip").first().text();
                } catch (IOException ex) { }
            }
            attempt++;
        }
        return IP; // return your own IP => isWorking() -> False
    }

    private static boolean isWorking() throws IOException {
        return !getIP().equals(IP);
    }

    public static void main(String[] args) {
        try {
            System.out.println("Your IP: " + IP);
            setProxy();
            System.out.println("Your current IP: " + getIP());
            System.out.println("Proxy is working: " + isWorking());
            setProxy();
            System.out.println("\nChanged proxy info");
            System.out.println("Your current IP: " + getIP());
            System.out.println("Proxy is working: " + isWorking());
        } catch (IOException e) {
            System.out.println("Error! " + e.toString());
        }
    }
}