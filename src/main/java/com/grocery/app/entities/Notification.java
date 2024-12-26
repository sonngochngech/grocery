package com.grocery.app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String message;

    private String externalData;

    private String type;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

   @OneToMany(mappedBy = "notification",cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
    private List<UserNoti> userNotis = new ArrayList<>();



}
