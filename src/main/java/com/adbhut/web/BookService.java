package com.adbhut.web;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Service;

import com.github.tennaito.rsql.jpa.JpaCriteriaCountQueryVisitor;
import com.github.tennaito.rsql.jpa.JpaCriteriaQueryVisitor;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookService {
    private EntityManager entityManager;


    public BookService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Book> searchByQuery(String queryString) {
        RSQLVisitor<CriteriaQuery<Book>, EntityManager> visitor = new JpaCriteriaQueryVisitor<>();
        CriteriaQuery<Book> query;
        query = getCriteriaQuery(queryString, visitor);
        List<Book> resultList = entityManager.createQuery(query)
                .getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return Collections.emptyList();
        }
        return resultList;
    }

    public int createBook(Book book) {
        return 1;
    }


    /**
     * Run the FIQL Query to SQL, execute it and return the count of the query More
     * details about it: https://github.com/jirutka/rsql-parser
     * 
     * @param queryString
     * @return
     */
    public Long countByQuery(String queryString) {
        RSQLVisitor<CriteriaQuery<Long>, EntityManager> visitor = new JpaCriteriaCountQueryVisitor<Book>();
        CriteriaQuery<Long> query;
        query = getCriteriaQuery(queryString, visitor);

        return entityManager.createQuery(query)
                .getSingleResult();
    }

    private <T> CriteriaQuery<T> getCriteriaQuery(String queryString,
            RSQLVisitor<CriteriaQuery<T>, EntityManager> visitor) {
        Node rootNode;
        CriteriaQuery<T> query;
        try {
            rootNode = new RSQLParser().parse(queryString);
            query = rootNode.accept(visitor, entityManager);
        } catch (Exception e) {
            log.error("An error happened while executing RSQL query", e);
            throw new IllegalArgumentException(e.getMessage());
        }
        return query;
    }
}
