package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String userId, final String authToken) throws AuthorizationFailedException, UserNotFoundException {

        final UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(authToken);

        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.");
        }

        final UserEntity user = userAuthToken.getUser();

        if(user.getRole().equals("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }else {

            final UserEntity userEntity = userDao.userByUuid(userId);

            if(userEntity == null){
                throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
            }

            return userDao.deleteUser(userEntity);
        }
    }

}
