package com.lecture.carrental.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name ="cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max=30, message = "Size is exceeded")
    @NotNull(message = "Please enter the car model")
    @Column(length = 30, nullable = false)
    private String model;

    @NotNull(message = "Please enter the car doors")
    @Column(nullable = false)
    private Integer doors;

    @NotNull(message = "Please enter the car seats")
    @Column(nullable = false)
    private Integer seats;

    @NotNull(message = "Please enter the car luggage")
    @Column(nullable = false)
    private Integer leggage;

    @Size(max = 30)
    @NotNull(message = "Please enter the car transmision")
    @Column(length = 30, nullable = false)
    private String transmission;

    @NotNull(message = "Please enter the car airConditioning")
    @Column(nullable = false)
    private Boolean airConditioning;

    @NotNull(message = "Please enter the car age")
    @Column(nullable = false)
    private Integer age;

    @NotNull(message = "Please enter the price per hour of the car")
    @Column(nullable = false)
    private Double pricePerHour;

    @Size(max = 30)
    @NotNull(message = "Please enter the car fuel type")
    @Column(length = 30, nullable = false)
    private String fuelType;

    private Boolean builtIn;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "car_image", joinColumns = @JoinColumn(name = "car_id"), inverseJoinColumns = @JoinColumn(name = "file_id"))
    private Set<FileDB> image;








}
