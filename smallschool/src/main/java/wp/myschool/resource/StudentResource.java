package wp.myschool.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import wp.myschool.dto.Pagination;
import wp.myschool.dto.Response;
import wp.myschool.dto.ResponseStatus;
import wp.myschool.entity.Gender;
import wp.myschool.entity.Student;

@Path("/student")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {

    @POST
    @Transactional
    public Response newStudent(
        @QueryParam("name") String newStudentName,
        @QueryParam("dob") LocalDate dob,
        @QueryParam("gender") Gender gender,
        @QueryParam("phoneNumber") String phoneNumber
    ){
        Response response = new Response();
        try {
            Student studentEntity = new Student();
            studentEntity.name = newStudentName;
            studentEntity.dob = dob;
            studentEntity.gender = gender;
            studentEntity.phoneNumber = phoneNumber;
            studentEntity.persist();
            response.data = studentEntity;
            response.status = ResponseStatus.SUCCESS;
            response.httpCode = 200;
        } catch (Exception e) {
            e.printStackTrace();
            response.status = ResponseStatus.ERROR;
        }
        return response;

    }

    
    @PUT
    @Transactional
    public Response editStudent(
        @QueryParam("id") String studentId,
        @QueryParam("name") String newStudentName,
        @QueryParam("dob") LocalDate dob,
        @QueryParam("gender") Gender gender,
        @QueryParam("phoneNumber") String phoneNumber
    ){        
        Response response = new Response();
        Student studentEntity = Student.findById(studentId);
        if(Objects.isNull(studentEntity)) {
            response.message.add("Student not found");
            response.status = ResponseStatus.ERROR;
            return response;
        }
        
        if(Objects.nonNull(newStudentName)){
            studentEntity.name = newStudentName;
        }
        if(Objects.nonNull(dob)){
            studentEntity.dob = dob;
        }
        if(Objects.nonNull(gender)){
            studentEntity.gender = gender;
        } 
        if(Objects.nonNull(phoneNumber)){
            studentEntity.phoneNumber = phoneNumber;
        }        
        studentEntity.persist();
        response.data = studentEntity;
        response.status = ResponseStatus.SUCCESS;
        return response;
    }

    @DELETE
    @Transactional
    public Response deleteStudent(@QueryParam("id") String id){
        Response response = new Response();        
        Student student = Student.findById(id);
        try {
            if(student.isPersistent()){
                student.delete();
                response.status = ResponseStatus.SUCCESS;
                response.httpCode = 200;
            } else {
                response.status = ResponseStatus.FAILED;
                response.httpCode = 400;
                response.message.add("Error deleting id:" + id + ". non persistent or not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.httpCode = 500;
            response.status = ResponseStatus.ERROR;
        }
        return response;
    }

    @GET
    @Path("all")
    public Response listAllStudents(@DefaultValue("1") @QueryParam("page") Integer searchPage) {
        Response response = new Response();
        PanacheQuery<Student> studentEntities = Student.findAll(Sort.by("id").ascending());
        List<Student> paginatedStudentList = studentEntities.page(Page.of(searchPage - 1, 5)).list();

        Pagination paging = new Pagination();
        paging.content = paginatedStudentList;
        paging.currentPage  = searchPage;
        paging.totalPage = studentEntities.pageCount();

        response.data = paging;
        response.status = ResponseStatus.SUCCESS;
        response.httpCode = 200;
        return response;
    }
    
    @GET
    @Path("search")
    public Response searchStudentByName(
        @QueryParam("name") String searchQuery,
        @QueryParam("page") Integer searchPage
    ) {
        PanacheQuery<Student> studentEntities = Student.find("lower(name) LIKE concat('%', ?1, '%')", searchQuery);
        List<Student> paginatedStudentList = studentEntities.page(Page.of(searchPage - 1, 5)).list();
        
        Pagination paging = new Pagination();
        paging.currentPage  = searchPage;
        paging.totalPage = studentEntities.pageCount();
        paging.content = paginatedStudentList;

        Response response = new Response();
        response.data = paging;
        response.status = ResponseStatus.SUCCESS;
        response.httpCode = 200;
        return response;
    }


}
