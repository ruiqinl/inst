package com.rli.inst.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service("parser")
public class Parser {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HtmlFetcher htmlFetcher;

    @Autowired
    private List<MediaExtractor> mediaExtractors;

    public List<MediaSrc> parse(final URL url) {
        Preconditions.checkNotNull(url);

        List<MediaExtractor> extractors = getApplicableExtractors(url);

        if (extractors.isEmpty()) {
            logger.error("no applicable extractor is found for {}", url);
            return Lists.newArrayList();
        }

        if (extractors.size() > 1) {
            logger.warn("Found {} MediaExtractor for URL {}, first one {} will be used",
                    extractors.size(), url, extractors.get(0).getClass().getName());
        }

        Optional<JsonNode> jsonNode = htmlFetcher.fetchData(url);
        if (!jsonNode.isPresent()) {
            return Lists.newArrayList();
        }

        return extractors.get(0).extract(jsonNode.get());
    }

    private List<MediaExtractor> getApplicableExtractors(URL url) {
        return mediaExtractors.stream()
                .filter(e -> e.isApplicable(url))
                .collect(Collectors.toList());
    }

}
