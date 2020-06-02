package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signUp(final UserEntity userEntity) throws SignUpRestrictedException {

        if(checkExistingUsername(userEntity.getUsername())) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        if(checkExistingEmail(userEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        final String password = userEntity.getPassword();

        final String[] encryptedText = cryptographyProvider.encrypt(password);

        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.signUp(userEntity);
    }

    public boolean checkExistingUsername(final String username){
        return userDao.userByUsername(username) != null;
    }

    public boolean checkExistingEmail(final String email){
        return userDao.userByEmail(email) != null;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signIn(final String username, final String password) throws AuthenticationFailedException {

        final UserEntity user = userDao.userByUsername(username);

        if(user == null)
            throw new AuthenticationFailedException("ATH-001","This username does not exist");

        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, user.getSalt());

        if(encryptedPassword.equals(user.getPassword())){

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
            userAuthTokenEntity.setUuid(UUID.randomUUID().toString());
            userAuthTokenEntity.setUser(user);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(),now,expiresAt));
            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuthTokenEntity);
            userDao.updateUser(user);

            return userAuthTokenEntity;

        }else {
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }

    }


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
