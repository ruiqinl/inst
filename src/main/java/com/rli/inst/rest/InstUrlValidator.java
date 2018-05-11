package com.rli.inst.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
class InstUrlValidator {
    private static final Logger logger = LoggerFactory.getLogger(InstUrlValidator.class);

    URL validate(String str) {
        URL url = null;

        try {
            url = new URL(str);
        } catch (MalformedURLException e) {
            logger.info("received invalid URL: {}", str);
            throw new InvalidUrlException("Invalid URL");
        }

        if (!url.getHost().toLowerCase().endsWith("instagram.com")) {
            logger.info("received invalid instagram URL: {}", str);
            throw new InvalidUrlException("Invalid Instagram URL");
        }

        logger.info("received URL: {}", str);
        return url;
    }

    static class InvalidUrlException extends RuntimeException {
        public InvalidUrlException(String message) {
            super(message);
        }
    }


}
