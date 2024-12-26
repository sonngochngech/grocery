package com.grocery.app.entities;

import com.grocery.app.config.constant.StatusConfig;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food")
@Getter
@Setter
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
