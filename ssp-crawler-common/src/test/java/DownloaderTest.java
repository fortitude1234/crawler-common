import com.dianping.ssp.crawler.common.downloader.impl.HttpClientDownloaderImpl;
import com.google.common.collect.Sets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.processor.SimplePageProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by iClaod on 10/21/16.
 */
public class DownloaderTest extends HttpClientDownloaderImpl{

    public static void main(String[] args) {
        DownloaderTest test = new DownloaderTest();
        Page page = test.handle();
        System.out.println(page.getRawText());

    }

    public Page handle() {

        Request request = new Request();
        request.setUrl("http://www.canyin88.com/zixun/2016/10/19/43676.html");
        Site site = Site.me().setCharset("GBK");
        Set<Integer> acceptStatCode;
        String charset = null;
        Map<String, String> headers = null;
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
            charset = site.getCharset();
            headers = site.getHeaders();
        } else {
            acceptStatCode = Sets.newHashSet(200);
        }
        CloseableHttpResponse httpResponse = null;
        int statusCode = 0;
        try {
            HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);
            httpResponse = getHttpClient(site).execute(httpUriRequest);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);
            if (statusAccept(acceptStatCode, statusCode)) {
                Page page = handleResponse(request, charset, httpResponse, null);
                return page;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return httpClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }


}
