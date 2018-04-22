package com.rli.inst.parser;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URL;
import java.util.List;

public interface MediaExtractor {
    List<MediaSrc> extract(final JsonNode jsonNode);
    boolean isApplicable(final URL url);
}
