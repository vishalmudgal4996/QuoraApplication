package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity question){
        entityManager.persist(question);
        return question;
    }

    public List<QuestionEntity> getAllQuestions(){
        return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
    }

    public QuestionEntity quesByUuid(String uuid){

        try{
            return entityManager.createNamedQuery("quesByUuid", QuestionEntity.class).setParameter("uuid",uuid).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public void updateQuestion(QuestionEntity questionEntity){
       entityManager.merge(questionEntity);
    }

    public QuestionEntity deleteQuestion(QuestionEntity questionEntity){
        entityManager.remove(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestionsByUser(UserEntity user){
        return entityManager.createNamedQuery("getAllQuestionsByUser", QuestionEntity.class).setParameter("user",user).getResultList();
    }

}
