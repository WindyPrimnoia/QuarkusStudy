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
import jakarta.ws.rs.core.Response;
import wp.myschool.dto.Pagination;
import wp.myschool.dto.MyResponse;
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
        Student newStudent
    ){
        MyResponse response = new MyResponse();
        try {
            Student studentEntity = new Student();
            studentEntity.name = newStudent.name;
            studentEntity.dob = newStudent.dob;
            studentEntity.gender = newStudent.gender;
            studentEntity.phoneNumber = newStudent.phoneNumber;
            studentEntity.persist();
            response.setData(studentEntity); 
            response.setDefaultSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            response.setDefaultError();
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(response)
                .build();
        }
        return Response
            .status(Response.Status.OK)
            .entity(response)
            .build();

    }

    
    @PUT
    @Transactional
    public Response editStudent(
        Student newStudent
    ){        
        MyResponse response = new MyResponse();
        Student studentEntity = Student.findById(newStudent.id);
        if(Objects.isNull(studentEntity)) {
            response.getMessage().add("Student not found");
            response.setDefaultFailedNotFound();
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(response)
                .build();
        }
        
        if(Objects.nonNull(newStudent.name)){
            studentEntity.name = newStudent.name;
        }
        if(Objects.nonNull(newStudent.dob)){
            studentEntity.dob = newStudent.dob;
        }
        if(Objects.nonNull(newStudent.gender)){
            studentEntity.gender = newStudent.gender;
        } 
        if(Objects.nonNull(newStudent.phoneNumber)){
            studentEntity.phoneNumber = newStudent.phoneNumber;
        }        
        studentEntity.persist();
        response.setData(studentEntity);
        response.setDefaultSuccess();
        return Response
            .status(Response.Status.OK)
            .entity(response)
            .build();
    }

    @DELETE
    @Transactional
    public Response deleteStudent(@QueryParam("id") String id){
        MyResponse response = new MyResponse();        
        Student student = Student.findById(id);
        try {
            if (Objects.isNull(student) || student.isPersistent() == false) {
                response.setStatus(ResponseStatus.FAILED);
                response.setHttpCode(404);
                response.getMessage().add("Error deleting id:" + id + ". non persistent or not exist");
                return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(response)
                    .build();
            } else {
                student.delete();
                response.setDefaultSuccess();
                return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
            } 
        } catch (Exception e) {
            e.printStackTrace();
            response.setDefaultError();
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(response)
                .build();
        }
    }

    @GET
    @Path("all")
    public Response listAllStudents(@DefaultValue("1") @QueryParam("page") Integer page) {
        MyResponse response = new MyResponse();
        PanacheQuery<Student> studentEntities = Student.findAll(Sort.by("id").ascending());
        List<Student> paginatedStudentList = studentEntities.page(Page.of(page - 1, 5)).list();
        
        Pagination paging = new Pagination();
        paging.setContent(paginatedStudentList);
        paging.setCurrentPage(page); 
        paging.setTotalPage(studentEntities.pageCount());

        response.setData(paging);
        response.setDefaultSuccess();
        return Response
            .status(Response.Status.OK)
            .entity(response)
            .build();
    }
    
    @GET
    @Path("search")
    public Response searchStudentByName(
        @DefaultValue("") @QueryParam("name") String searchQuery,
        @DefaultValue("1") @QueryParam("page") Integer page
    ) {
        PanacheQuery<Student> studentEntities = Student.find("lower(name) LIKE concat('%', ?1, '%')", searchQuery);
        List<Student> paginatedStudentList = studentEntities.page(Page.of(page - 1, 5)).list();
        
        Pagination paging = new Pagination();
        paging.setCurrentPage(page);
        paging.setTotalPage(studentEntities.pageCount());
        paging.setContent(paginatedStudentList);

        MyResponse response = new MyResponse();
        response.setData(paging);
        response.setDefaultSuccess();
        return Response
            .status(Response.Status.OK)
            .entity(response)
            .build();
    }

    @GET
    public Response searchStudentById(
        @QueryParam("id") Long id
    ) {
        MyResponse response = new MyResponse();        
        Student student = Student.findById(id);
        try {
            if (Objects.isNull(student) || student.isPersistent() == false) {
                response.setStatus(ResponseStatus.FAILED);
                response.setHttpCode(404);
                response.getMessage().add("Error finding student with id:" + id + ". non persistent or not exist");
                return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(response)
                    .build();
            } else {
                response.setData(student);
                response.setDefaultSuccess();
                return Response
                    .status(Response.Status.OK)
                    .entity(response)
                    .build();
            } 
        } catch (Exception e) {
            e.printStackTrace();
            response.setDefaultError();
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(response)
                .build();
        }

    }


}
