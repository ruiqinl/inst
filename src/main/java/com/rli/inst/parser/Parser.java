package com.rli.inst.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.rli.inst.HtmlFetcher;
import com.rli.inst.model.MediaSrc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service("parser")
public class Parser {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HtmlFetcher htmlFetcher;

    private List<MediaExtractor> mediaExtractors = null;

    public List<MediaSrc> parse(final URL url) throws IOException {
        List<MediaExtractor> extractors = getApplicableExtractor(url);
        if (extractors.size() != 1) {
            logger.warn("Found {} MediaExtractor for URL {}", extractors.size(), url);
        }

        for (MediaExtractor extractor : extractors) {
            List<MediaSrc> result = null;

             Optional<JsonNode> jsonNode = htmlFetcher.fetchData(url);
            result = extractor.extract(jsonNode.get());

            if (!result.isEmpty()) {
                return result;
            }
        }

        return Lists.newArrayList();
    }

    private List<MediaExtractor> getApplicableExtractor(URL url) {
        List<MediaExtractor> list = Lists.newArrayList();
        for (MediaExtractor extractor : mediaExtractors) {
            if (extractor.isApplicable(url)) {
                list.add(extractor);
                logger.debug("Apply extractor {}", extractor.getClass().getName());
            }
        }
        return list;
    }

    @Autowired
    private void injectMediaExtractors(HomepageMediaExtractor homepageMediaExtractor,
                                    SinglePageMediaExtractor singlePageMediaExtractor) {
        this.mediaExtractors = Lists.newArrayList(homepageMediaExtractor, singlePageMediaExtractor);
    }

}
