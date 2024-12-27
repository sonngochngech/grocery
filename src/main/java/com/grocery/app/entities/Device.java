package com.grocery.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="devices")
public class Device {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private  Long id;


    @Unique
    private String deviceId;

    private String deviceName;


    @ManyToMany(mappedBy = "devices",fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<User> users=new HashSet<>();

}
