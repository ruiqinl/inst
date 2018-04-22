package com.rli.inst;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service("htmlFetcher")
public class HtmlFetcher {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String prefix = "<script type=\"text/javascript\">window._sharedData = ";
    private final String postfix = ";</script>";
    private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
    private final String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";

    public Optional<JsonNode> fetchData(final URL url) throws IOException {
        String html = fetchHtml(url);

        Optional<String> data = getSharedData(html);
        if (!data.isPresent()) {
            return Optional.absent();
        }

        return str2JsonNode(data.get());
    }

    private String fetchHtml(URL url) throws IOException {

        HttpGet httpGet = new HttpGet(url.toString());
        httpGet.setHeader(new BasicHeader(HttpHeaders.USER_AGENT, userAgent));
        httpGet.setHeader(new BasicHeader(HttpHeaders.ACCEPT, accept));

        CloseableHttpClient client = getTimeoutHttpClient(5);

        try (CloseableHttpResponse response = client.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        }
    }

    private CloseableHttpClient getTimeoutHttpClient(final int timeout) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();
        return HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    private Optional<String> getSharedData(String html) {
        for (String rawLine : html.split("\n")) {
            String line = rawLine.trim();
            if (line.startsWith(prefix) && line.endsWith(postfix)) {
                String data = line.substring(prefix.length(), line.length() - postfix.length());
                return Optional.of(data);
            }
        }
        logger.debug("No shared data found in html: {}", html);
        return Optional.absent();
    }

    private Optional<JsonNode> str2JsonNode(final String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Optional.of(objectMapper.readTree(data));
        } catch (IOException e) {
            logger.debug("Invalid Json node String: {}", data);
            return Optional.absent();
        }
    }

}
