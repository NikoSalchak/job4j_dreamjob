package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Salchak Niko", "junior java developer", LocalDateTime.of(2025, 8, 3, 12, 0)));
        save(new Candidate(0, "Tregulov Zaur", "Senior java developer", LocalDateTime.of(2025, 6, 28, 13, 44)));
        save(new Candidate(0, "Ivanov Ivan", "fronted developer", LocalDateTime.of(2025, 6, 8, 13, 4)));
        save(new Candidate(0, "Petrov Stas", "ios developer", LocalDateTime.of(2025, 7, 5, 13, 55)));
        save(new Candidate(0, "Petrov Emil", "mobile developer", LocalDateTime.of(2025, 6, 15, 13, 4)));
        save(new Candidate(0, "Borisova Marina", "mobile developer", LocalDateTime.of(2025, 9, 1, 13, 24)));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(
                        oldCandidate.getId(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate())
        ) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
