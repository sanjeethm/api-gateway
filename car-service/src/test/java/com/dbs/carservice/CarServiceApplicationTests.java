package com.dbs.carservice;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.dbs.carservice.CarServiceApplication.Car;
import com.dbs.carservice.CarServiceApplication.CarRepository;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.cloud.discovery.enabled = false"})
public class CarServiceApplicationTests {

    @Autowired
    CarRepository carRepository;

    @Autowired
    WebTestClient webTestClient;
    
    CarServiceApplication carServiceApplication = new CarServiceApplication();

    @Test
    public void testAddCar() {
    	
        Car buggy = carServiceApplication.new Car( UUID.randomUUID(), "ID. BUGGY", LocalDate.of(2022, Month.DECEMBER, 1));

        webTestClient.post().uri("/cars")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(buggy), Car.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("ID. BUGGY");
    }

    @Test
    public void testGetAllCars() {
        webTestClient.get().uri("/cars")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Car.class);
    }

    @Test
    public void testDeleteCar() {
        Car buzzCargo = carRepository.save(carServiceApplication.new Car(UUID.randomUUID(), "ID. BUZZ CARGO",
                LocalDate.of(2022, Month.DECEMBER, 2))).block();

        webTestClient.delete()
                .uri("/cars/{id}", Collections.singletonMap("id", buzzCargo.getId()))
                .exchange()
                .expectStatus().isOk();
    }
}