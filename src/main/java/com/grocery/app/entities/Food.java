package com.grocery.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "food")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")  // Specify the foreign key column name for Category
    private Categories category;

    @ManyToOne
    @JoinColumn(name = "unit_id")  // Specify the foreign key column name for MeasureUnit
    private Unit measureUnit;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    // Getters and setters (or use Lombok annotations if preferred)
}
