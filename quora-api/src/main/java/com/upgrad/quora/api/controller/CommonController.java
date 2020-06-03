package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserCommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private UserCommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@PathVariable("userId")final String userId, @RequestHeader("authorization")final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        final UserEntity userEntity = commonBusinessService.getUser(userId,authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse.setFirstName(userEntity.getFirstName());
        userDetailsResponse.setLastName(userEntity.getLastName());
        userDetailsResponse.setUserName(userEntity.getUsername());
        userDetailsResponse.setEmailAddress(userEntity.getEmail());
        userDetailsResponse.setCountry(userEntity.getCountry());
        userDetailsResponse.setAboutMe(userEntity.getAboutMe());
        userDetailsResponse.setDob(userEntity.getDob());
        userDetailsResponse.setContactNumber(userEntity.getContactNumber());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }

}
