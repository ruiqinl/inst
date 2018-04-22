package com.rli.inst.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

@Service("homepageMediaExtractor")
public class HomepageMediaExtractor implements MediaExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String path = "entry_data.ProfilePage[0].graphql.user.edge_owner_to_timeline_media.edges";

    @Override
    public List<MediaSrc> extract(final JsonNode jsonNode) {

        Optional<JsonNode> nodes = JsonNodeWrapper.of(jsonNode).get(path);
        if (!nodes.isPresent()) {
            return Lists.newArrayList();
        }

        List<MediaSrc> mediaSrcs = Lists.newArrayList();
        Iterator<JsonNode> ite = nodes.get().elements();
        while (ite.hasNext()) {
            JsonNode node = ite.next().get("node");

            final String id = node.get("id").asText();
            final String thumbnailSrc = node.get("thumbnail_src").asText();
            final String displaySrc = node.get("display_url").asText();
            final boolean isVideo = node.get("is_video").asBoolean();

            try {
                mediaSrcs.add(new MediaSrc(id, thumbnailSrc, displaySrc, null, isVideo));
            } catch (MalformedURLException e) {
                logger.error("Invalid URL found in fetched data: {}", node, e);
            }
        }

        return mediaSrcs;
    }

    @Override
    public boolean isApplicable(final URL url) {
        final String path = url.getPath();
        return path.matches("/[^/]+/?");
    }


}
