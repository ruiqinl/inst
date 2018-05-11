package com.rli.inst.rest;

import com.rli.inst.parser.MediaSrc;
import com.rli.inst.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/parse")
public class ParseRestController {

    private static final Logger logger = LoggerFactory.getLogger(ParseRestController.class);

    @Autowired
    private Parser parser;

    @Autowired
    private InstUrlValidator instUrlValidator;

    @GetMapping("")
    public List<MediaSrc> parse(@RequestParam("url") String str) {
        if (!str.startsWith("http://") && !str.startsWith("https://")) {
            str = "https://" + str;
        }

        URL url = instUrlValidator.validate(str);
        return parser.parse(url);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InstUrlValidator.InvalidUrlException.class)
    @ResponseBody
    private ErrorInfo handleInvalidUrlException(HttpServletRequest request, Exception e) {
        return new ErrorInfo(Instant.now(), e.getMessage(), request.getQueryString());
    }

}
