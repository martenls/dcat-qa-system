package com.martenls.qasystem.services.annotators;

import com.github.pemistahl.lingua.api.Language;
import com.martenls.qasystem.exceptions.ESIndexUnavailableException;
import com.martenls.qasystem.indexers.LabeledURIIndexer;
import com.martenls.qasystem.models.Question;
import com.martenls.qasystem.parsers.ThemesRDFParser;
import com.martenls.qasystem.services.ElasticSearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Log4j2
@Service
public class ThemeEntityRecognizer implements QuestionAnnotator {

    @Autowired
    private ElasticSearchService searchService;

    @Value("${es.theme_index}")
    private String themeIndex;

    @Value("${themeEntities}")
    private String themesRDFPath;

    @Value("${properties.languages}")
    private String[] languages;


    /**
     * Checks if necessary indices exist and starts parsing and indexing if they are not present.
     */
    @PostConstruct
    private void initIndices() {
        try {
            if (!searchService.checkIndexExistence(themeIndex)) {
                ThemesRDFParser parser = new ThemesRDFParser(languages);
                parser.parse(themesRDFPath);
                LabeledURIIndexer indexer = new LabeledURIIndexer(searchService, themeIndex, languages);
                if (!searchService.checkIndexExistence(themeIndex)) {
                    indexer.indexEntities(parser.getParsedEntities());
                }

            } else {
                log.debug("Theme-index present, nothing to be done");
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
                question.getThemeEntities().addAll(recognizeThemeEntities(shingle, question.getLanguage()));
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
    private List<String> recognizeThemeEntities(String word, Language language) {
        try {
            return searchService.queryIndexForLabeledUri("labels" + language.getIsoCode639_1().toString().toUpperCase(), word, themeIndex, 1, "1");
        } catch (ESIndexUnavailableException e) {
            log.error("Could not fetch theme entities: ESIndex not available");
            return Collections.emptyList();
        }
    }
}
