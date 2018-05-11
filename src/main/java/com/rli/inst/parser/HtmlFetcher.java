package com.rli.inst.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Component("htmlFetcher")
class HtmlFetcher {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String key = "window._sharedData =";
    private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
    private final String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";

    Optional<JsonNode> fetchData(final URL url) {
        String html = null;
        try {
            html = fetchHtml(url);
        } catch (IOException e) {
            logger.error("timeout accessing {}, return empty content", url);
            return Optional.empty();
        }

        Optional<String> data = getSharedData(html);
        if (!data.isPresent()) {
            return Optional.empty();
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
            if (line.contains(key)) {
                int start = line.indexOf("{");
                int end = line.lastIndexOf("}");
                if (start == -1 || end == -1) {
                    continue;
                }

                String data = line.substring(start, end+1);
                return Optional.of(data);
            }
        }
        logger.debug("No shared data found in html: {}", html);
        return Optional.empty();
    }

    private Optional<JsonNode> str2JsonNode(final String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Optional.of(objectMapper.readTree(data));
        } catch (IOException e) {
            logger.debug("Invalid Json node String: {}", data);
            return Optional.empty();
        }
    }

}
