package com.example.coronatracking.application;

import com.example.coronatracking.application.controller.ControllerAuth;
import com.example.coronatracking.application.controller.ControllerTrack;
import com.example.coronatracking.application.model.entity.EntityTrack;
import com.example.coronatracking.application.model.request.RequestId;
import com.example.coronatracking.application.model.request.RequestSignIn;
import com.example.coronatracking.application.model.request.RequestSignUp;
import com.example.coronatracking.application.model.request.RequestTrack;
import com.example.coronatracking.application.model.response.ResponseOperation;
import com.example.coronatracking.application.model.response.ResponseTrack;
import com.example.coronatracking.application.repo.RepoTrack;
import com.example.coronatracking.application.repo.RepoUser;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnitTestsTrack {

    @Autowired
    private ControllerAuth contAuth;
    @Autowired
    private ControllerTrack contTrack;
    @Autowired
    private RepoTrack repoTrack;
    @Autowired
    private RepoUser repoUser;

    @BeforeAll
    public void setup() {
        // Sign Up User
        String name = "Fatih Sevban Uyanık";
        String password = "mysecretpassword";
        String email = "fatih15@gmail.com";
        String username = "fatih";
        Integer age = 23;

        RequestSignUp requestSignUp = new RequestSignUp(name, age,  email, username, password);
        RequestSignIn requestSignIn = new RequestSignIn(email, password);
        contAuth.signUp(requestSignUp);
        contAuth.signIn(requestSignIn);
    }


    @Test
    @Order(1)
    public void testCreateTrack() {
        RequestTrack[] requestTracks = {
                new RequestTrack(true, false, true, true, false,false,
                        false, false, false, false), // correct data
                new RequestTrack(false, false, true, false, false, false,
                        false, false, false, false), // correct data
                new RequestTrack(false, null, true, true, true, false,
                        null, null, true, true) // wrong data, not sending some symptomps
        };

        boolean[] expected = { true, true, false };
        for (int i = 0; i < 3; i++) {
            testCreateTrack(requestTracks[i], expected[i]);
        }
    }

    public void testCreateTrack(RequestTrack requestTrack, Boolean expected) {
        ResponseEntity<ResponseOperation> response = contTrack.createTrack(requestTrack);
        Assert.notNull(response);
        Assert.isTrue(response.getBody().getSuccess() == expected);
    }

    @Test
    @Order(2)
    public void testGetTracks() {
        ResponseEntity<List<ResponseTrack>> response = contTrack.getTracks();
        List<ResponseTrack> responseList = response.getBody();
        Assert.notNull(responseList);
        Assert.isTrue(responseList.size() > 0);
    }

    @Test
    @Order(3)
    public void testDeleteTrack() {
        List<EntityTrack> tracks = repoTrack.findAll();
        for (EntityTrack track: tracks) {
            testDeleteTrack(track.getId());
        }
    }

    public void testDeleteTrack(Long id) {
        RequestId requestId = new RequestId(id);
        ResponseEntity<ResponseOperation> response = contTrack.deleteTrack(requestId);
        Assert.notNull(response);
        Assert.isTrue(response.getBody().getSuccess());
    }

    @AfterAll
    public void end() {
        // clear database
        repoTrack.deleteAll();
        repoUser.deleteAll();
    }

}
