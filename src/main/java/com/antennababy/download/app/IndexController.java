package com.antennababy.download.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping("/")
@Controller
public class IndexController {
        @RequestMapping
        public void index(HttpServletResponse response) throws IOException {
            response.sendRedirect("/static/index.html");
        }
}
