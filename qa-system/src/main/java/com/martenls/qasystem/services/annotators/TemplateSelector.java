package com.martenls.qasystem.services.annotators;


import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.models.Template;
import com.martenls.qasystem.models.TemplateRated;
import com.martenls.qasystem.services.TemplateProvider;
import com.martenls.qasystem.services.annotators.QuestionAnnotator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Log4j2
@Service
public class TemplateSelector implements QuestionAnnotator {

    @Autowired
    private TemplateProvider templateProvider;

    @Override
    public Question annotate(Question question) {
        List<TemplateRated> candidates = new ArrayList<>();
        for (Template template : templateProvider.getTemplates()) {
            candidates.add(new TemplateRated(template, rateTemplateQuestionPair(template, question)));
        }
        candidates.sort(Comparator.comparing(TemplateRated::getRating));
        Collections.reverse(candidates);
        question.getTemplateCandidates().addAll(candidates.subList(0,2));
        return question;
    }


    private int rateTemplateQuestionPair(Template template, Question question) {
        int rating = 0;

        if (template.getPropertyCount() == question.getOntologyProperties().size()) {
            rating += 10;
        }

        if (template.getEntityCount() == question.getLocations().size()) {
            rating += 10;
        }

        if (template.hasCountAggregate() == question.hasProperty(Question.properties.COUNT)) {
            rating += 10;
        }

        if (template.hasOrderAscModifier() == question.hasProperty(Question.properties.ASC_ORDERED)) {
            rating += 10;
        }

        if (template.hasOrderDescModifier() == question.hasProperty(Question.properties.DESC_ORDERED)) {
            rating += 10;
        }

        if (template.hasGroupByAggregate() == question.hasProperty(Question.properties.COUNT)) {
            rating += 5;
        }

        // TODO: add more rules

        // "how many, the most, the least" -> count() / group by + order by


        return rating;
    }



}