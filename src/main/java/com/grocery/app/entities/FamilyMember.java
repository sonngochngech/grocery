package com.grocery.app.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="family_members")
public class FamilyMember {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;   
    
    private String name;

    @OneToOne
    @JoinColumn(name="family_id")
    private Family family;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;
    
}
