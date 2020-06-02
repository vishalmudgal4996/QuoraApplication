package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SignoutAuthorizationService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signOut(final String authToken) throws SignOutRestrictedException {

        final UserAuthTokenEntity authTokenEntity = userDao.getUserAuthToken(authToken);

        if(authTokenEntity == null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }

        authTokenEntity.setLogoutAt(ZonedDateTime.now());
        userDao.updateUserAuth(authTokenEntity);

        return authTokenEntity.getUser();
    }
}
