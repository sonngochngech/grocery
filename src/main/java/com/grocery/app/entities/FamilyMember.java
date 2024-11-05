package com.grocery.app.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="family_members")
public class FamilyMember {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;   
    
    private String name;

    @ManyToOne
    @JoinColumn(name="family_id")
    private Family family;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    
}
