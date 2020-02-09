package com.adbhut.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/books")
@Slf4j
public class BookResources {
    @Autowired
    private BookService bookService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBook(@RequestBody Book book) throws URISyntaxException {
        log.info("New Book [{}}", book);
       int id =  bookService.createBook(book);
       URI location = ServletUriComponentsBuilder
               .fromCurrentRequest().path("/{id}")
               .buildAndExpand(id).toUri();
        return ResponseEntity.created(location)
                .build();
    }

    /**
     *
     * @param query
     * @return the ResponseEntity with status 200 (OK) with a list of Books that
     *         matched your query, or status 400 (Bad request) if the attribute
     *         couldn't be returned
     */
    @GetMapping(path = "/", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<Book>> query(@RequestParam(value = "q") String query) {
        List<Book> result = null;
        try {
            result = bookService.searchByQuery(query);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}
