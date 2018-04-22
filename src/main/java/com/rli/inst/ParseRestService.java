package com.rli.inst;

import com.rli.inst.json.JSONError;
import com.rli.inst.model.MediaSrc;
import com.rli.inst.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping(path="/api/parse")
public class ParseRestService {

    private static final Logger logger = LoggerFactory.getLogger(ParseRestService.class);

    @Autowired
    private Parser parser;

    @GetMapping("")
    public List<MediaSrc> parse(@RequestParam("url") String str) {
        URL url = validateInstUrl(str);

        try {
            return parser.parse(url);
        } catch (IOException e) {
            logger.warn("Timeout accessing {}", url);
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new JSONError("Timeout accessing " + url))
                            .build()
            );
        }
    }

    private URL validateInstUrl(String str) {
        URL url = null;

        try {
            url = new URL(str);
        } catch (MalformedURLException e) {
            logger.debug("received invalid URL: {}", str);
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(new JSONError("Invalid URL"))
                            .build());
        }

        if (!url.getHost().toLowerCase().endsWith("instagram.com")) {
            logger.debug("received invalid instagram URL: {}", url);
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(new JSONError("Entered URL is not an Instagram URL"))
                            .build());
        }

        logger.debug("received URL: {}", url);
        return url;
    }

}
