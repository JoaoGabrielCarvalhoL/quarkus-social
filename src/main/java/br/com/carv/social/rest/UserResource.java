package br.com.carv.social.rest;

import br.com.carv.social.dto.CreateUserRequest;
import br.com.carv.social.dto.ResponseError;
import br.com.carv.social.model.User;
import br.com.carv.social.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
public class UserResource {

    private UserRepository userRepository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {

        Set<ConstraintViolation<CreateUserRequest>> validate = validator.validate(userRequest);

        if(!validate.isEmpty()) {
            ResponseError responseError = ResponseError.createFromValidation(validate);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseError).build();
        }

        User user = new User(userRequest.getName(), userRequest.getAge());
        userRepository.persist(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllUsers() {
        PanacheQuery<User> list = userRepository.findAll();
        return Response.ok(list.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            userRepository.delete(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        User user = userRepository.findById(id);
        if (user != null) {
            user.setName(userData.getName());
            user.setAge(userData.getAge());
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
