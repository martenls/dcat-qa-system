package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Question;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class TimeEntityRecognizer implements QuestionAnnotator {


    /**
     * Extracts the time entities from the NLP annotations and adds them to the question.
     * @param question to be annotated
     * @return annotated question
     */
    @Override
    public Question annotate(Question question) {
        CoreDocument document = question.getNlpAnnotations();

        for (CoreEntityMention entityMention : document.entityMentions()) {
            if ("DATE".equals(entityMention.entityType())) {
                Timex timex = entityMention.coreMap().get(TimeAnnotations.TimexAnnotation.class);
                try {
                    if (timex.getRange() != null) {
                        question.getTimeIntervalEntities().add(timex.getRange());
                    } else if (timex.getDate() != null) {
                        question.getTimeEntities().add(timex.getDate());
                    }
                } catch (RuntimeException e) {
                    // skip if timex could not be read
                    continue;
                }
            }
        }
        return question;
    }


}
