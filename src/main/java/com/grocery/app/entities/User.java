package com.grocery.app.entities;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public  class User{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String username;

    @Size(min=3,max=20,message="First name must be between 5 and 20 characters")
    private String firstName;

    @Size(min=3,max=20,message="Last name must be between 5 and 20 characters")
    private String lastName;

    private String email;

    private String phoneNumber;


    private String sex;

    private Date birthday;

    private String avatar;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="role_id")
    private Role role;

    @Builder.Default
    private Boolean isActivated=Boolean.TRUE;


    @OneToMany(mappedBy="owner")
    private List<Family> families=new ArrayList<>();

    private String password;


    @ManyToMany(fetch=FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name="user_devices",
    joinColumns = @JoinColumn(name="user_id"),
    inverseJoinColumns = @JoinColumn(name="device_id")
    )
    private Set<Device> devices= new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<UserNoti> notifications=new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Notification> sentNotifications=new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<Invitation> invitations=new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private FavoriteRecipeList favoriteRecipeList;







    





}