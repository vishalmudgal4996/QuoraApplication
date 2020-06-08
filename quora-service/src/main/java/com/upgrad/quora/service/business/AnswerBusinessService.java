package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final String quesId,final String authorization, final AnswerEntity answerEntity) throws InvalidQuestionException, AuthorizationFailedException {

        final QuestionEntity question = questionDao.quesByUuid(quesId);

        if(question == null){
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        }

        final UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(authorization);

        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post an answer");
        }

        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setUser(userAuthToken.getUser());
        answerEntity.setQuestion(question);

        return answerDao.createAnswer(answerEntity);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(final String answerId, final String authorization, final String content) throws AuthorizationFailedException, AnswerNotFoundException {

        final UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(authorization);

        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit an answer");
        }

        final AnswerEntity answer = answerDao.ansByUuid(answerId);

        if(answer == null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        if(answer.getUser() == userAuthToken.getUser()){

            answer.setAns(content);
            answerDao.updateAnswerContent(answer);
            return answer;
        }else {
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(final String answerId,final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        final UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(authorization);

        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete an answer");
        }

        final AnswerEntity answer = answerDao.ansByUuid(answerId);

        if(answer == null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        if(answer.getUser() == userAuthToken.getUser() || userAuthToken.getUser().getRole().equals("admin")){
            return answerDao.deleteAnswer(answer);
        }else{
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
        }

    }


    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId,final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(authorization);

        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get the answers");
        }

        final QuestionEntity questionEntity = questionDao.quesByUuid(questionId);

        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
        }

        return answerDao.getAllAnswersToQuestion(questionEntity);
    }
}
