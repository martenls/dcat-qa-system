package com.martenls.qasystem.services.annotators;


import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.models.Template;
import com.martenls.qasystem.models.TemplateRated;
import com.martenls.qasystem.services.TemplateProvider;
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

    /**
     * Rates all templates. Orders them by their rating and adds them to the question object.
     * @param question for which a template should be selected
     * @return annotated question
     */
    @Override
    public Question annotate(Question question) {
        List<TemplateRated> candidates = new ArrayList<>();
        List<Template> templates = question.getAdditionalProperties().contains(Question.properties.ASK_QUERY) ? templateProvider.getAskTemplates() : templateProvider.getTemplates();
        for (Template template : templates) {
            candidates.add(new TemplateRated(template, rateTemplateQuestionPair(template, question)));
        }
        candidates.sort(Comparator.comparing(TemplateRated::getRating));
        Collections.reverse(candidates);
        question.getTemplateCandidates().addAll(candidates);
        return question;
    }

    /**
     * Rates a template-question pair based on:
     * - the number of property slots and properties
     * - the number of entity slots and entities
     * - if a count modifier is present and the question indicates one
     * - if a order by modifier is present and the question indicates one
     * - if a filter for literals is present and the question has literals
     * - if a filter for temporal entities is present and the question has temporal entities
     * @param template to be rated
     * @param question to base the rating on
     * @return a rating of how well the specified template fits the specified question
     */
    private int rateTemplateQuestionPair(Template template, Question question) {
        int rating = 0;


        // same amount of placeholders and properties -> 10 points else 10 points - difference
        if (template.getPropertyCount() <= question.getOntologyProperties().size()) {
            rating += Math.max(0, 10 - Math.abs(template.getPropertyCount() - question.getOntologyProperties().size()));
        } else {
            return 0;
        }

        if (template.getEntityCount() <= question.getEntityCount()) {
            rating += Math.max(0, 10 - Math.abs(template.getEntityCount() - question.getEntityCount()));
        } else {
            return 0;
        }


        // count indicator -> template with count
        if (template.hasCountAggregate() != question.hasProperty(Question.properties.COUNT)) {
            return 0;
        }

        // asc indicator -> template with order by and asc
        if (template.hasOrderAscModifier() == question.hasProperty(Question.properties.ASC_ORDERED)) {
            rating += 10;
        } else {
            rating -= 10;
        }

        // desc indicator -> template with order by and desc
        if (template.hasOrderDescModifier() == question.hasProperty(Question.properties.DESC_ORDERED)) {
            rating += 10;
        } else {
            rating -= 10;
        }

        // count indicator -> template with group by
        if (template.hasGroupByAggregate() && question.hasProperty(Question.properties.COUNT)) {
            rating += 5;
        }

        // string literals -> template with filter
        if (template.hasLiteralArrayFilter() && question.getLiterals().isEmpty()) {
            return 0;
        } else if (template.hasLiteralArrayFilter() == !question.getLiterals().isEmpty()) {
            rating += 10;
        } else {
            rating -= 10;
        }
        // temporal entity with interval -> template with interval filter
        if (template.hasIntervalFilter() && question.getTimeIntervalEntities().isEmpty()) {
            return 0;
        } else if (template.hasIntervalFilter() == !question.getTimeIntervalEntities().isEmpty()) {
            rating += 10;
        } else {
            rating -= 10;
        }

        if (template.hasLiteralFilter() && question.getTimeEntities().isEmpty()) {
            return 0;
        } else if (template.hasLiteralFilter() == !question.getTimeEntities().isEmpty()) {
            rating += 10;
        } else {
            rating -= 10;
        }


        return rating;
    }


}
