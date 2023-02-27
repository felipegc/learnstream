package com.felipe.learnstream;

import com.felipe.learnstream.jpa.models.Customer;
import com.felipe.learnstream.jpa.models.Order;
import com.felipe.learnstream.jpa.models.Product;
import com.felipe.learnstream.jpa.repos.CustomerRepo;
import com.felipe.learnstream.jpa.repos.OrderRepo;
import com.felipe.learnstream.jpa.repos.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.maxBy;

@Slf4j
@DataJpaTest
public class StreamApiTest {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Test
    @DisplayName("Obtain a list of product with category = Books and price > 100")
    public void exercise1() {
        List<Product> result = productRepo.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
                .filter(p -> p.getPrice() > 100.0)
                .collect(Collectors.toList());

        result.forEach(r -> log.info(r.toString()));
    }

    @Test
    @DisplayName("Obtain a list of order with products belong to category \"Baby\"")
    public void exercise2() {
        List<Order> result = orderRepo.findAll()
                .stream()
                .filter(o -> o.getProducts()
                        .stream()
                        .anyMatch(p -> p.getCategory().equalsIgnoreCase("Baby")))
                .collect(Collectors.toList());

        result.forEach(r -> log.info(r.toString()));
    }

    @Test
    @DisplayName("Obtain a list of product with category = Toys and then apply 10% discount")
    public void exercise3() {
        List<Product> result = productRepo.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("toys"))
//                .map(p -> {
//                    Double discountedPrice = Double.valueOf(p.getPrice() * 0.9);
//                    p.setPrice(discountedPrice);
//                    return p;
//                })
                .map(p -> p.withPrice(Double.valueOf(p.getPrice() * 0.9)))
                .collect(Collectors.toList());

        result.forEach(r -> log.info(r.toString()));
    }

    @Test
    @DisplayName("Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021")
    public void exercise4() {
        List<Product> result = orderRepo.findAll()
                .stream()
                .filter(o -> o.getCustomer().getTier() == 2)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >=0)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 4, 1)) <= 0)
                .flatMap(o -> o.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());

        result.forEach(r -> log.info(r.toString()));
    }

    @Test
    @DisplayName("Get the cheapest products of Books category")
    public void exercise5() {
//        Product book = productRepo.findAll()
//                .stream()
//                .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
//                .min(Comparator.comparing(Product::getPrice))
//                .orElseThrow(NoSuchElementException::new);
        Product book = productRepo.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
                .sorted(Comparator.comparing(Product::getPrice))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        log.info(book.toString());
    }

    @Test
    @DisplayName("Get the 3 most recent placed order")
    public void exercise6() {
        List<Order> orders = orderRepo.findAll()
                .stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(3)
                .collect(Collectors.toList());

        orders.forEach(o -> log.info(o.toString()));
    }

    @Test
    @DisplayName("Get a list of orders which were ordered on 15-Mar-2021, log the order records to the console and then return its product list")
    public void exercise7() {
        List<Product> products = orderRepo.findAll()
                .stream()
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 3, 15)) == 0)
                .peek(System.out::println)
                .flatMap(o -> o.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());

        products.forEach(p -> log.info(p.toString()));

//        List<Product> result = orderRepo.findAll()
//                .stream()
//                .filter(o -> o.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
//                .peek(o -> System.out.println(o.toString()))
//                .flatMap(o -> o.getProducts().stream())
//                .distinct()
//                .collect(Collectors.toList());
//
//        result.forEach(r -> log.info(r.toString()));
    }

    @Test
    @DisplayName("Calculate total lump sum of all orders placed in Feb 2021")
    public void exercise8() {
        double sum = orderRepo.findAll()
                .stream()
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 02, 1)) >= 0)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 03, 1)) < 0)
                .flatMap(o -> o.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();

        log.info("Lump sum for feb/2021 is {}", sum);
    }

    @Test
    @DisplayName("Calculate order average payment placed on 14-Mar-2021") // 14 gives 0 so changed to 15
    public void exercise9() {
        double avg = orderRepo.findAll()
                .stream()
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 03, 15)) == 0)
                .flatMap(o -> o.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0l);


        log.info("Avg for 14-mar-2021 is {}", avg);
    }

    @Test
    @DisplayName("Obtain a collection of statistic figures (i.e. sum, average, max, min, count) for all products of category Books")
    public void exercise10() {
        DoubleSummaryStatistics summaryStatistics = productRepo.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();
        log.info("Summary statistics for products is: {}", summaryStatistics);
    }

    @Test
    @DisplayName("Obtain a data map with order id and order’s product count")
    public void exercise11() {
        Map<Long, Integer> orderMap = orderRepo.findAll()
                .stream()
                .collect(Collectors.toMap(
                        o -> o.getId(),
                        o -> o.getProducts().size()
                ));

        log.info("Order map -> {}", orderMap);
    }

    @Test
    @DisplayName("Produce a data map with order records grouped by customer")
    public void exercise12() {
        Map<Customer, List<Order>> collect = orderRepo.findAll()
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomer));
        log.info("Grouping by customer-> {}", collect);
        collect.forEach((k, v) -> log.info("{} -> {}", k.getName(), v.stream().map(a -> a.getId().toString()).reduce("", (a,b) -> a + "," + b)));
    }

    @Test
    @DisplayName("Produce a data map with order record and product total sum")
    public void exercise13() {
        Map<Order, Double> collect = orderRepo.findAll()
                .stream()
                .collect(Collectors.toMap(
                        o -> o,
                        o -> o.getProducts()
                                .stream()
                                .map(p -> p.getPrice())
                                .reduce(0.0, (a, sum) -> a + sum)));
        log.info("Total of each order = {}", collect);
        collect.forEach((k,v) -> log.info("Order id: {} = {}", k.getId(), v));

//        Map<Order, Double> collect2 = orderRepo.findAll()
//                .stream()
//                .collect(Collectors.toMap(
//                        Function.identity(),
//                        o -> o.getProducts()
//                                .stream()
//                                .mapToDouble(p -> p.getPrice())
//                                .sum()));
//
//        log.info("Total of each order = {}", collect);
//        collect2.forEach((k,v) -> log.info("Order id: {} = {}", k.getId(), v));
    }

    @Test
    @DisplayName("Obtain a data map with list of product name by category")
    public void exercise14() {
        Map<String, List<String>> collect = productRepo.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.mapping(p -> p.getName(), Collectors.toList())));

        log.info("Product by category -> {}", collect);
    }

    @Test
    @DisplayName("Get the most expensive product by category")
    public void exercise15() {
        Map<String, Optional<Product>> collect = productRepo.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        maxBy(Comparator.comparing(Product::getPrice))));

        collect.forEach((k, v) -> log.info("Category {} and expensive is {}", k , v.get()));
    }

}
