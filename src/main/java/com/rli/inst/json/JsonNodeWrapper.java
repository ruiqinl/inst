package com.rli.inst.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonNodeWrapper {
    private final JsonNode jsonNode;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JsonNodeWrapper(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public static JsonNodeWrapper of(JsonNode node) {
        return new JsonNodeWrapper(node);
    }

    public Optional<JsonNode> get(final String s) {
        // replace [0] by .0
        // replace [0][1][23] by .0.1.23
        String replacedS = s.replaceAll("\\[([0-9]+)\\]", "\\.$1");

        String[] path = replacedS.split("\\.");
        JsonNode node = jsonNode;
        for (int i = 0; i < path.length; i++) {
            final String p = path[i];

            if (p.matches("^[0-9]+$")) {
                node = node.get(Integer.valueOf(p));
            } else {
                node = node.get(p);
            }
            if (node == null) {
                logger.debug("{}-th Child node in path {} not found", i, s);
                return Optional.absent();
            }
        }
        return Optional.of(node);
    }
}
