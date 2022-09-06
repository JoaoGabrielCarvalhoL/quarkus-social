package br.com.carv.social.rest;

import br.com.carv.social.dto.CreatePostRequest;
import br.com.carv.social.dto.PostResponse;
import br.com.carv.social.model.Post;
import br.com.carv.social.model.User;
import br.com.carv.social.repository.FollowerRepository;
import br.com.carv.social.repository.PostRepository;
import br.com.carv.social.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;

    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest postRequest) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(postRequest.getText());
        post.setUser(user);
        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).entity(post).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listPost(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId").build();
        }

        User follower = userRepository.findById(followerId);

        if (follower == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nonexistent followerId").build();
        }

        boolean follows = followerRepository.follows(follower, user);

        if(!follows) {
            return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts.").build();
        }

        PanacheQuery<Post> posts = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending),user);

        List<PostResponse> response = posts.stream().map(post -> new PostResponse(post.getText(), post.getDateTime())).collect(Collectors.toList());

        return Response.status(Response.Status.OK).entity(response).build();
    }

}
