package com.example.backend.controller;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The type Contree common error.
 */
@Component
public class ContreeCommonError implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage e404 = new ErrorPage(HttpStatus.NOT_FOUND, "/index");
        registry.addErrorPages(e404);
    }

}
