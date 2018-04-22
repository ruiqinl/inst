package com.rli.inst.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.rli.inst.json.JsonNodeWrapper;
import com.rli.inst.model.MediaSrc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


@Service("singlePageMediaExtractor")
public class SinglePageMediaExtractor implements MediaExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String singleMediaPath = "entry_data.PostPage[0].graphql.shortcode_media";
    private final String multipleMediaPath = singleMediaPath + ".edge_sidecar_to_children.edges";

    @Override
    public List<MediaSrc> extract(JsonNode jsonNode) {
        Optional<JsonNode> multipleMedia = JsonNodeWrapper.of(jsonNode).get(multipleMediaPath);
        if (multipleMedia.isPresent()) {
            List<MediaSrc> list = getMultipleMediaSrc(multipleMedia.get());
            if (!list.isEmpty()) {
                return list;
            }
        }

        Optional<JsonNode> singleMedia = JsonNodeWrapper.of(jsonNode).get(singleMediaPath);
        if (singleMedia.isPresent()) {
            Optional<MediaSrc> mediaSrc = getSingleMediaSrc(singleMedia.get());
            if (mediaSrc.isPresent()) {
                return Lists.newArrayList(mediaSrc.get());
            }
        }

        logger.warn("Found no Media in {}", jsonNode);
        return Lists.newArrayList();
    }

    @Override
    public boolean isApplicable(final URL url) {
        final String path = url.getPath();
        return path.matches("/[^/]+/[^/]+/?");
    }

    private List<MediaSrc> getMultipleMediaSrc(JsonNode multipleMedia) {
        List<MediaSrc> list = Lists.newArrayList();
        for (JsonNode edge : multipleMedia) {
            JsonNode node = edge.get("node");
            if (node == null) continue;

            Optional<MediaSrc> mediaSrc = getSingleMediaSrc(node);
            if (mediaSrc.isPresent()) {
                list.add(mediaSrc.get());
            }
        }
        return list;
    }

    private Optional<MediaSrc> getSingleMediaSrc(JsonNode mediaNode) {
        final String id = mediaNode.get("id").asText();
        final String displayUrl = mediaNode.get("display_url").asText();
        final boolean isVideo = mediaNode.get("is_video").asBoolean();
        String videoUrl = null;
        if (isVideo) {
            videoUrl = mediaNode.get("video_url").asText();
        }

        try {
            return Optional.of(new MediaSrc(id, null, displayUrl, videoUrl, isVideo));
        } catch (MalformedURLException e) {
            logger.error("Invalid URL found in fetched data: {}", mediaNode);
            return Optional.absent();
        }
    }
}
