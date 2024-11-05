package com.grocery.app.unitTests.repository;


import com.grocery.app.entities.Family;
import com.grocery.app.entities.FamilyMember;
import com.grocery.app.entities.Role;
import com.grocery.app.entities.User;
import com.grocery.app.repositories.FamilyMemberRepo;
import com.grocery.app.repositories.FamilyRepo;
import com.grocery.app.repositories.RoleRepo;
import com.grocery.app.repositories.UserRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class FamilyRepositoryTests {

    @Autowired
    private FamilyRepo familyRepo;


    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private FamilyMemberRepo familyMemberRepo;

    @BeforeEach
    public void setUp(){

        User user1=User.builder().username("test").password("test").build();
        testEntityManager.persistAndFlush(user1);

        User user2=User.builder().username("test").password("test").build();
        testEntityManager.persistAndFlush(user2);

        User user3=User.builder().username("test").password("test").build();
        testEntityManager.persistAndFlush(user3);

        FamilyMember fm1=FamilyMember.builder().name("FamilyMember1").user(user1).build();
        FamilyMember fm2=FamilyMember.builder().name("FamilyMember1").user(user2).build();
        FamilyMember fm3=FamilyMember.builder().name("FamilyMember1").user(user3).build();


        Family family=Family.builder().name("Family1").owner(user1).familyMembers(List.of(fm2,fm3)).build();
        fm2.setFamily(family);
        fm3.setFamily(family);
        testEntityManager.persistAndFlush(family);

        Family family1=Family.builder().name("Family1").owner(user2).familyMembers(List.of(fm1)).build();
        fm1.setFamily(family);
        testEntityManager.persistAndFlush(family1);

    }

    @Test
    @DisplayName("Test remove a family member")
    @Order(1)
    public void testRemoveAFamilyMember(){
        Family family=testEntityManager.find(Family.class,1L);
        assertThat(family).isNotNull();
        List<FamilyMember> mutableFamilyMembers = new ArrayList<>(family.getFamilyMembers());
        mutableFamilyMembers.removeIf(familyMember -> familyMember.getUser().getId().equals(2L));
        family.setFamilyMembers(mutableFamilyMembers);
        familyRepo.save(family);
        Family family1=familyRepo.findById(1L).get();
        assertThat(family1).isNotNull();
        assertThat(family1.getFamilyMembers().size()).isEqualTo(1);
    }

//    @Test
//    @DisplayName("Test add a family member")
//    public void testAddAFamilyMember(){
//
//    }

    @Test
    @DisplayName("Test find family by user")
    @Order(2)
    public void testFindFamilyByUser(){
        List<Family> family=familyRepo.findAll();
        System.out.println(family.size());
        List<Family> families=familyRepo.findFamilyByUser(1L).orElse(null);
        assertThat(families).isNotNull();
        System.out.println(families.size());

    }
}
