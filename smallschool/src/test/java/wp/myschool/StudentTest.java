package wp.myschool;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import wp.myschool.dto.MyResponse;
import wp.myschool.dto.ResponseStatus;
import wp.myschool.entity.Gender;
import wp.myschool.entity.Student;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@QuarkusTest
@Slf4j
public class StudentTest {
    @Test
    void testListAllStudentNoParam() {
        given()
          .when().get("/student/all")
          .then().log().body()
             .statusCode(200);
    }


    private Student createNewRandomStudent(){
        Student newStudent = new Student();
        newStudent.name = UUID.randomUUID().toString();
        long minDay = LocalDate.of(1990, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2015, 12, 31).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
        newStudent.dob = randomDate;
        newStudent.gender = ThreadLocalRandom.current().nextBoolean() == true ? Gender.MALE : Gender.FEMALE;
        Random rand = new Random();
        newStudent.phoneNumber = String.valueOf(rand.nextLong());
        return newStudent;
    }

    private Student copyStudent(Student firstStudent){
        Student copiedStudent = new Student();
        if (Objects.nonNull(firstStudent.id)){
            copiedStudent.id = firstStudent.id;
        }
        copiedStudent.name = firstStudent.name;
        copiedStudent.dob = firstStudent.dob;
        copiedStudent.gender = firstStudent.gender;
        copiedStudent.phoneNumber = firstStudent.phoneNumber;
        return copiedStudent;
    }

    private boolean compareStudentWithoutId(Student firstStudent, Student secondStudent){
        return firstStudent.name.equalsIgnoreCase(secondStudent.name)
            && firstStudent.dob.isEqual(secondStudent.dob)
            && firstStudent.gender.equals(secondStudent.gender)
            && firstStudent.phoneNumber.equalsIgnoreCase(secondStudent.phoneNumber)
        ;
    }

    private boolean compareStudentWithId(Student firstStudent, Student secondStudent){
        return 
        firstStudent.id.equals(secondStudent.id) 
            && firstStudent.name.equalsIgnoreCase(secondStudent.name)
            && firstStudent.dob.isEqual(secondStudent.dob)
            && firstStudent.gender.equals(secondStudent.gender)
            && firstStudent.phoneNumber.equalsIgnoreCase(secondStudent.phoneNumber)
        ;
    }

    @Test
    void testCreateNewStudent() {
        Student newStudent = createNewRandomStudent();

        Student expectedStudent = copyStudent(newStudent);

        MyResponse receivedResponse = 
        given()
            // .contentType("application/json\r\n")
            .contentType(ContentType.JSON)
            .body(newStudent)
            .log().all()
                .when().post("/student")
                .then()
                .log().all()
                .statusCode(200).extract().as(MyResponse.class);
        
        // this mapper is needed to serialize date time. 
        // serializing date time is not supported by jackson by default
        
        ObjectMapper mapper = new ObjectMapper()   
            .registerModule(new JavaTimeModule())
        ; 
        Student receivedStudent = mapper.convertValue(receivedResponse.getData(),new TypeReference<Student>(){});
        assertTrue(compareStudentWithoutId(receivedStudent, expectedStudent)); // normal compare no ID
        assertTrue(receivedResponse.getMessage().isEmpty());
        assertEquals(receivedResponse.getStatus(), ResponseStatus.SUCCESS);
        assertEquals(receivedResponse.getHttpCode(), 200);
    }

    @Test
    void testEditStudent() {
        Student newStudent = createNewRandomStudent();

        Student expectedStudent = copyStudent(newStudent);

        // step 1 -- create new student -- 
        MyResponse receivedResponse = 
        given()
            // .contentType("application/json\r\n")
            .contentType(ContentType.JSON)
            .body(newStudent)
            .log().all()
                .when().post("/student")
                .then()
                .log().all()
                .statusCode(200).extract().as(MyResponse.class);
        
        // this mapper is needed to serialize date time. 
        // serializing date time is not supported by jackson by default
        ObjectMapper mapper = new ObjectMapper()   
            .registerModule(new JavaTimeModule())
        ; 
        Student receivedStudent = mapper.convertValue(receivedResponse.getData(),new TypeReference<Student>(){});
        assertTrue(compareStudentWithoutId(receivedStudent, expectedStudent)); // normal compare no ID
        assertTrue(receivedResponse.getMessage().isEmpty());
        assertEquals(receivedResponse.getStatus(), ResponseStatus.SUCCESS);
        assertEquals(receivedResponse.getHttpCode(), 200);
        
        // step 2 -- edit created new student --
        Student editedNewStudent = createNewRandomStudent();
        editedNewStudent.id = receivedStudent.id;

        receivedResponse = 
        given()
            // .contentType("application/json\r\n")
            .contentType(ContentType.JSON)
            .body(editedNewStudent)
            .log().all()
                .when().put("/student")
                .then()
                .log().all()
                .statusCode(200).extract().as(MyResponse.class);
        
        receivedStudent = mapper.convertValue(receivedResponse.getData(),new TypeReference<Student>(){});
        assertTrue(compareStudentWithId(receivedStudent, editedNewStudent));// compare WITH ID 
        assertTrue(receivedResponse.getMessage().isEmpty());
        assertEquals(receivedResponse.getStatus(), ResponseStatus.SUCCESS);
        assertEquals(receivedResponse.getHttpCode(), 200);
        
    }

    @Test
    void testDeleteStudent() {
        Student newStudent = createNewRandomStudent();

        Student expectedStudent = copyStudent(newStudent);

        // step 1 -- create new student -- 
        MyResponse receivedResponse = 
        given()
            // .contentType("application/json\r\n")
            .contentType(ContentType.JSON)
            .body(newStudent)
            .log().all()
                .when().post("/student")
                .then()
                .log().all()
                .statusCode(200).extract().as(MyResponse.class);
        
        // this mapper is needed to serialize date time. 
        // serializing date time is not supported by jackson by default
        ObjectMapper mapper = new ObjectMapper()   
            .registerModule(new JavaTimeModule())
        ; 
        Student receivedStudent = mapper.convertValue(receivedResponse.getData(),new TypeReference<Student>(){});
        assertTrue(compareStudentWithoutId(receivedStudent, expectedStudent)); // normal compare no ID
        assertTrue(receivedResponse.getMessage().isEmpty());
        assertEquals(receivedResponse.getStatus(), ResponseStatus.SUCCESS);
        assertEquals(receivedResponse.getHttpCode(), 200);
        
        // step 2 -- delete created new student --
        receivedResponse = 
        given()
            // .contentType("application/json\r\n")
            .contentType(ContentType.JSON)
            .queryParam("id", receivedStudent.id)
            .log().all()
                .when().delete("/student")
                .then()
                .log().all()
                .statusCode(200).extract().as(MyResponse.class);
        
        assertTrue(receivedResponse.getMessage().isEmpty());
        assertEquals(receivedResponse.getStatus(), ResponseStatus.SUCCESS);
        assertEquals(receivedResponse.getHttpCode(), 200);

        // step 3 -- check student id --
        receivedResponse = 
        given()
            // .contentType("application/json\r\n")
            .contentType(ContentType.JSON)
            .queryParam("id", receivedStudent.id)
            .log().all()
                .when().get("/student")
                .then()
                .log().all()
                .statusCode(404).extract().as(MyResponse.class);
        
        String firstMessage = receivedResponse.getMessage().stream().findFirst().orElse(null);
        assertTrue(receivedResponse.getMessage().isEmpty() == false);
        assertNotNull(firstMessage);
        assertTrue(firstMessage.equalsIgnoreCase("Error finding student with id:" + receivedStudent.id + ". non persistent or not exist"));
        assertEquals(receivedResponse.getStatus(), ResponseStatus.FAILED);
        assertEquals(receivedResponse.getHttpCode(), 404);
    }

    @Test
    void testGetOneStudent() {
        Student expectedStudent = new Student();
        expectedStudent.id = Long.valueOf(1);
        expectedStudent.dob = LocalDate.of(1995, 9, 12);
        expectedStudent.name = "Emily Brown";
        expectedStudent.gender = Gender.FEMALE;
        expectedStudent.phoneNumber = "081235131";

        // step 1 -- check student id 1 -- 
        MyResponse receivedResponse = 
        given()
            .contentType(ContentType.JSON)
            .queryParam("id", 1)
            .log().all()
                .when().get("/student")
                .then()
                .log().all()
                .statusCode(200).extract().as(MyResponse.class);
        
        // this mapper is needed to serialize date time. 
        // serializing date time is not supported by jackson by default
        ObjectMapper mapper = new ObjectMapper()   
            .registerModule(new JavaTimeModule())
        ; 
        Student receivedStudent = mapper.convertValue(receivedResponse.getData(),new TypeReference<Student>(){});
        assertTrue(compareStudentWithoutId(receivedStudent, expectedStudent)); // normal compare no ID
        assertTrue(receivedResponse.getMessage().isEmpty());
        assertEquals(receivedResponse.getStatus(), ResponseStatus.SUCCESS);
        assertEquals(receivedResponse.getHttpCode(), 200);
        
        // step 2 -- check random student id, --
        receivedResponse = 
        given()
            // .contentType("application/json\r\n")
            .contentType(ContentType.JSON)
            .queryParam("id", 999999999)
            .log().all()
                .when().get("/student")
                .then()
                .log().all()
                .statusCode(404).extract().as(MyResponse.class);
        
        String firstMessage = receivedResponse.getMessage().stream().findFirst().orElse(null);
        assertTrue(receivedResponse.getMessage().isEmpty() == false);
        assertNotNull(firstMessage);
        assertTrue(firstMessage.contains(String.valueOf(999999999)));
        assertTrue(firstMessage.toLowerCase().contains("not exist"));
        assertEquals(receivedResponse.getStatus(), ResponseStatus.FAILED);
        assertEquals(receivedResponse.getHttpCode(), 404);
    }



}
