package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }


    public void updateAnswerContent(AnswerEntity answerEntity){
        entityManager.merge(answerEntity);
    }


    public AnswerEntity ansByUuid(String uuid){

        try{
            return entityManager.createNamedQuery("ansByUuid",AnswerEntity.class).setParameter("uuid",uuid).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }


    public AnswerEntity deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    public List<AnswerEntity> getAllAnswersToQuestion(QuestionEntity questionEntity){
        return entityManager.createNamedQuery("getAllAnswersToQuestion",AnswerEntity.class).setParameter("question",questionEntity).getResultList();
    }
}
