package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DataSourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oCandidateRepositoryTest {

    private static Sql2oCandidateRepository sql2oCandidateRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static File file;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oCandidateRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DataSourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oCandidateRepository = new Sql2oCandidateRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        /** нужно сохранить хотя бы один файл, т.к. Vacancy от него зависит
         */
        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clearCandidates() {
        var candidates = sql2oCandidateRepository.findAll();
        candidates.forEach(candidate -> sql2oCandidateRepository.deleteById(candidate.getId()));
    }

    @Test
    public void whenSaveThenGetSame() {
        var creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(new Candidate(0, "Niko Salchak", "Java developer 1 year experience", creationDate, 1, file.getId()));
        var savedCandidate = sql2oCandidateRepository.findById(candidate.getId()).get();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var candidateOne = sql2oCandidateRepository.save(new Candidate(0, "Zaur Tregulov", "fronted developer 3 year experience", creationDate, 1, file.getId()));
        var candidateTwo = sql2oCandidateRepository.save(new Candidate(0, "Marina Ivanova", "dart developer 1 year experience", creationDate, 1, file.getId()));
        var candidateThird = sql2oCandidateRepository.save(new Candidate(0, "0 1", "0001 01100", creationDate, 1, file.getId()));
        var candidateFour = sql2oCandidateRepository.save(new Candidate(0, "Niko Salchak", "Java developer 1 year experience", creationDate, 1, file.getId()));
        var result = sql2oCandidateRepository.findAll();
        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(candidateOne, candidateTwo, candidateThird, candidateFour));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oCandidateRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(sql2oCandidateRepository.findById(0)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oCandidateRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        var creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(new Candidate(0, "Niko Salchak", "Java developer 1 year experience", creationDate, 1, file.getId()));
        var updatedCandidate = new Candidate(candidate.getId(), "Niko Salchak", "Java developer 2 year experience", creationDate, 2, file.getId());
        var isUpdated = sql2oCandidateRepository.update(updatedCandidate);
        var savedCandidate = sql2oCandidateRepository.findById(candidate.getId()).get();
        assertThat(isUpdated).isTrue();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(updatedCandidate);
    }

    @Test
    public void whenUpdateUnExistingCandidateThenGetFalse() {
        var creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = new Candidate(0, "Niko Salchak", "Java developer 1 year experience", creationDate, 1, file.getId());
        var isUpdated = sql2oCandidateRepository.update(candidate);
        assertThat(isUpdated).isFalse();
    }
}
