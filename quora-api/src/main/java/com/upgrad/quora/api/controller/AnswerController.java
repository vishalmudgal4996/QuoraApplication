package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@RestController
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId")final String questionId, @RequestHeader("authorization")final String authorization, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAns(answerRequest.getAnswer());

        final AnswerEntity createdAnswer = answerBusinessService.createAnswer(questionId, authorization, answerEntity);

        return new ResponseEntity<AnswerResponse>(new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED"), HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(@PathVariable("answerId")final String answerId, @RequestHeader("authorization")final String authorization, final AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, AnswerNotFoundException {

        final AnswerEntity answerEntity = answerBusinessService.editAnswerContent(answerId, authorization, answerEditRequest.getContent());

        return new ResponseEntity<AnswerEditResponse>(new AnswerEditResponse().id(answerEntity.getUuid()).status("ANSWER EDITED"),HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId")final String answerId, @RequestHeader("authorization")final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        final AnswerEntity answerEntity = answerBusinessService.deleteAnswer(answerId, authorization);

        return new ResponseEntity<AnswerDeleteResponse>(new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED"),HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId")final String questionId, @RequestHeader("authorization")final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final List<AnswerEntity> allAnswersToQuestion = answerBusinessService.getAllAnswersToQuestion(questionId, authorization);

        ListIterator<AnswerEntity> ansIterator = allAnswersToQuestion.listIterator();

        List<AnswerDetailsResponse> allAnswersToQuestionList = new LinkedList<>();

        while (ansIterator.hasNext()){

            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();

            AnswerEntity answerEntity = ansIterator.next();
            answerDetailsResponse.setId(answerEntity.getUuid());
            answerDetailsResponse.setQuestionContent(answerEntity.getQuestion().getContent());
            answerDetailsResponse.setAnswerContent(answerEntity.getAns());

            allAnswersToQuestionList.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(allAnswersToQuestionList,HttpStatus.OK);
    }

}
