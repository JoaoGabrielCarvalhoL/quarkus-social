package br.com.carv.social.rest;

import br.com.carv.social.dto.FollowRequest;
import br.com.carv.social.dto.FollowerResponse;
import br.com.carv.social.dto.FollowersPerUserResponse;
import br.com.carv.social.model.Follower;
import br.com.carv.social.model.User;
import br.com.carv.social.repository.FollowerRepository;
import br.com.carv.social.repository.UserRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
public class FollowerResource {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowRequest request) {

        if (userId.equals(request.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("You can't follow your self.").build();
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User follower = userRepository.findById(request.getFollowerId());

        Boolean follows = followerRepository.follows(follower, user);

        if (!follows) {
            Follower entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);
            followerRepository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listFollowers(@PathParam("userId") Long userId) {

        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> list = followerRepository.findByUser(userId);
        FollowersPerUserResponse response = new FollowersPerUserResponse();
        response.setCount(list.size());

        List<FollowerResponse> result = list.stream().map(follower ->
                new FollowerResponse(follower.getId(), follower.getUser().getName())).collect(Collectors.toList());
        response.setContent(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
