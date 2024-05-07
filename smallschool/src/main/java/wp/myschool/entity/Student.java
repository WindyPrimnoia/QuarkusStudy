package wp.myschool.entity;

import java.time.LocalDate;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.Entity;

@Entity
public class Student extends PanacheEntity{
    // public String id;
    public String name;
    public LocalDate dob;
    public Gender gender;
    public String phoneNumber;

    public static PanacheQuery<Student> findByName(String name){
        return find("name like ?1", name);
    }

    public static PanacheQuery<Student> findByGender(Gender gender){
        return find("gender", gender);        
    }
}
