package com.martenls.qasystem.services.annotators;

import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.parsers.LabeledURIJsonParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ThemeEntityRecognizer extends EntityRecognizer {

    public ThemeEntityRecognizer(@Value("${es.theme_index}") String indexName,
                                 @Value("${data.themeEntities}") String rdfFilePath,
                                 @Value("${properties.languages}") String[] languages) {
        super(indexName, rdfFilePath, languages, new LabeledURIJsonParser(languages));
    }

    /**
     * Annotates the question with all theme entities that match at least one of the w-shingles.
     *
     * @param question to be annotated
     * @return annotated question
     */
    @Override
    public Question annotate(Question question) {
        if (question.getWShingles() != null) {
            for (String shingle : question.getWShingles()) {
                question.getThemeEntities().addAll(recognizeEntities(shingle, question.getLanguage(), 1, "1"));
            }
        }
        return question;
    }

}
