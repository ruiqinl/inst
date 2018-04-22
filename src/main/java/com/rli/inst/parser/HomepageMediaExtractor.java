package com.rli.inst.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("homepageMediaExtractor")
public class HomepageMediaExtractor implements MediaExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<MediaSrc> extract(final JsonNode jsonNode) {
        JsonNode mediaNodesRoot = jsonNode.findPath("edge_owner_to_timeline_media").findPath("edges");

        return mediaNodesRoot.findValues("node")
                .stream()
                .map(n -> getSingleMediaSrc(n).orElse(null))
                .filter(m -> m != null)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isApplicable(final URL url) {
        final String path = url.getPath();
        return path.matches("/[^/]+/?");
    }

    private Optional<MediaSrc> getSingleMediaSrc(JsonNode node) {
        final String id = node.path("id").asText();
        final String displayUrl = node.path("display_url").asText();
        final boolean isVideo = node.path("is_video").asBoolean();
        final String videoUrl = node.path("video_url").asText();
        final String thumbnailSrc = node.path("thumbnail_src").asText();

        try {
            return Optional.of(new MediaSrc(id, thumbnailSrc, displayUrl, videoUrl, isVideo));
        } catch (MalformedURLException e) {
            logger.warn("Invalid URL found in fetched data: {}", node, e);
            return Optional.empty();
        }
    }

}
