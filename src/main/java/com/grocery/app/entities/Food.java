package com.grocery.app.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.mapper.LocalDateConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id") // Specify the foreign key column name for Category
    private Category category;

    @ManyToOne
    @JoinColumn(name = "unit_id") // Specify the foreign key column name for MeasureUnit
    private Unit measureUnit;

    private Date createdAt;
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id") // Specify the foreign key column name for User
    private User user;


    @OneToMany(mappedBy = "food",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FridgeItem> fridgeItems=new ArrayList<>();

    private String status = StatusConfig.AVAILABLE.getStatus();
}
