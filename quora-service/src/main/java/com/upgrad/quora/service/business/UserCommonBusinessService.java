package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCommonBusinessService {

    @Autowired
    private UserDao userDao;

    public UserEntity getUser(final String userId, final String authToken) throws AuthorizationFailedException, UserNotFoundException {

        final UserAuthTokenEntity authTokenEntity = userDao.getUserAuthToken(authToken);

        if(authTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(authTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }

        final UserEntity user = userDao.userByUuid(userId);

        if(user == null){
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        return user;
    }
}
