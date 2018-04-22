package com.rli.inst.parser;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;


@Service("singlePageMediaExtractor")
public class SinglePageMediaExtractor implements MediaExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<MediaSrc> extract(JsonNode jsonNode) {
        JsonNode singleMediaNode = jsonNode.findPath("shortcode_media");
        JsonNode multiMediasNode = singleMediaNode.findPath("edge_sidecar_to_children").findPath("edges");

        if (!multiMediasNode.isMissingNode()) {
            return getMultipleMediaSrc(multiMediasNode);
        } else if (!singleMediaNode.isMissingNode()) {
            Optional<MediaSrc> mediaSrc = getSingleMediaSrc(singleMediaNode);
            if (mediaSrc.isPresent()) {
                return Lists.newArrayList(mediaSrc.get());
            }
        }
        return Lists.newArrayList();
    }

    @Override
    public boolean isApplicable(final URL url) {
        final String path = url.getPath();
        return path.matches("/[^/]+/[^/]+/?");
    }

    private List<MediaSrc> getMultipleMediaSrc(JsonNode multipleMedia) {
        return multipleMedia.findValues("node")
                .stream()
                .map(n -> getSingleMediaSrc(n).orElse(null))
                .filter(m -> m != null)
                .collect(Collectors.toList());
    }

    private Optional<MediaSrc> getSingleMediaSrc(JsonNode node) {
        final String id = node.path("id").asText();
        final String displayUrl = node.path("display_url").asText();
        final boolean isVideo = node.path("is_video").asBoolean();
        final String videoUrl = node.path("video_url").asText();

        try {
            return Optional.of(new MediaSrc(id, null, displayUrl, videoUrl, isVideo));
        } catch (MalformedURLException e) {
            logger.warn("Invalid URL found in fetched data: {}", node, e);
            return Optional.empty();
        }
    }
}
