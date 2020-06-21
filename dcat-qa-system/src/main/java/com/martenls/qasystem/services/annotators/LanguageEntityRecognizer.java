package com.martenls.qasystem.services.annotators;

import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.indexers.LabeledURIIndexer;
import com.martenls.qasystem.parsers.LanguagesRDFParser;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.services.ElasticSearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
public class LanguageEntityRecognizer implements QuestionAnnotator{

    @Autowired
    private ElasticSearchService searchService;

    @Value("${es.language_index}")
    private String languageIndex;

    @Value("${languageEntities}")
    private String languageRDFPath;

    @Value("${properties.languages}")
    private String[] languages;


    /**
     * Checks if necessary indices exist and starts parsing and indexing if they are not present.
     */
    @PostConstruct
    private void initIndices() {
        try {
            if (!searchService.checkIndexExistence(languageIndex)) {
                LanguagesRDFParser parser = new LanguagesRDFParser(languages);
                parser.parse(languageRDFPath);
                LabeledURIIndexer indexer = new LabeledURIIndexer(searchService, languageIndex, languages);
                if (!searchService.checkIndexExistence(languageIndex)) {
                    indexer.indexEntities(parser.getParsedEntities());
                }

            } else {
                log.debug("Language-index present, nothing to be done");
            }
        } catch (ESIndexUnavailableException e) {
            log.error("Could not init indices: ESIndex not available");
        }

    }

    /**
     * Annotates the question with all properties and classes that match at least one of the w-shingles.
     * @param question to be annotated
     * @return annotated question
     */
    @Override
    public Question annotate(Question question) {
        if (question.getWShingles() != null) {
            for (String shingle : question.getWShingles()) {
                question.getLanguageEntities().addAll(recognizeLanguageEntities(shingle, question.getLanguage()));
            }
        }
        return question;
    }


    /**
     * Queries the language index for the given word in the given language.
     * @param word to query
     * @param language to query in
     * @return list of matched properties
     */
    private List<String> recognizeLanguageEntities(String word, Language language) {
        try {
            return searchService.queryIndexForLabeledUri("labels" + language.getIsoCode639_1().toString().toUpperCase(), word, languageIndex, 1, "1");
        } catch (ESIndexUnavailableException e) {
            log.error("Could not fetch language entities: ESIndex not available");
            return Collections.emptyList();
        }
    }


}
