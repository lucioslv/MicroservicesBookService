package com.microservices.bookservice.controller;

import java.util.HashMap;

import com.microservices.bookservice.model.Book;
import com.microservices.bookservice.proxy.CambioProxy;
import com.microservices.bookservice.repository.BookRepository;
import com.microservices.bookservice.response.Cambio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("book-service")
public class BookController {
    
    @Autowired
    private Environment environment;

    @Autowired
    private BookRepository repository;

    @Autowired
    private CambioProxy proxy;

    @GetMapping(value = "/{id}/{currency}")
    public Book findBook(
        @PathVariable("id") Long id,
        @PathVariable("currency") String currency
    ) {

        var book = repository.getById(id);
        if(book == null) throw new RuntimeException("Book not found!");
        
        var cambio = proxy.getCambio(book.getPrice(), "USD", currency);

        var port = environment.getProperty("local.server.port");

        book.setEnvironment(port);
        book.setPrice(cambio.getConvertedValue());

        return book;
    }
}
