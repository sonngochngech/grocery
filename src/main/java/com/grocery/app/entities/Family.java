package com.grocery.app.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name="families")
public class Family {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;   
    
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="owner_id")
    @NotNull
    private User owner;

    @OneToMany(mappedBy = "family",cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<FamilyMember> familyMembers= new ArrayList<>();

    @Builder.Default
    private Boolean isDeleted=Boolean.FALSE;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToOne(mappedBy = "family",cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonIgnore
    private Fridge fridge;


    @OneToMany(mappedBy = "family",cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonIgnore
    private List<Invitation> invitations=new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if(this.fridge==null){
            this.fridge=Fridge.builder().family(this).build();
        }

    }

}
