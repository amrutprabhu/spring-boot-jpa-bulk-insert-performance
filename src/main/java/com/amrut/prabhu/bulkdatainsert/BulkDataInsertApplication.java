package com.amrut.prabhu.bulkdatainsert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class BulkDataInsertApplication {

    public static void main(String[] args) {
        SpringApplication.run(BulkDataInsertApplication.class, args);
    }

    @Autowired
    private BookRepository repository;

   @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {

        int totalObjects = 10000;

        long start = System.currentTimeMillis();
        List<Book> books = IntStream.range(0, totalObjects)
                .mapToObj(val -> Book.builder()
                        .name("books" + val)
                        .Price(val)
                        .build())
                .collect(Collectors.toList());

        System.out.println("Finished creating "+totalObjects+" objects in memory in:" + (System.currentTimeMillis() - start)/1000);

        start = System.currentTimeMillis();
        System.out.println("Inserting ..........");

        for (int i = 0; i < totalObjects; i += batchSize) {
            if( i+ batchSize > totalObjects){
                List<Book> books1 = books.subList(i, totalObjects - 1);
                repository.saveAll(books1);
                break;
            }
            List<Book> books1 = books.subList(i, i + batchSize);
            repository.saveAll(books1);
        }

        System.out.println("Finished inserting "+totalObjects+" objects in :" + (System.currentTimeMillis() - start));
    }
}
