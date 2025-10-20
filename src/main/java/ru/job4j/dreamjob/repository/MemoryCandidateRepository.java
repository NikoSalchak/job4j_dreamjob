package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final ConcurrentHashMap<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0,
                "Salchak Niko",
                "junior java developer",
                LocalDateTime.of(2025, 8, 3, 12, 0),
                3,
                0)
        );
        save(new Candidate(0,
                "Tregulov Zaur",
                "Senior java developer",
                LocalDateTime.of(2025, 6, 28, 13, 44),
                3,
                0)
        );
        save(new Candidate(0,
                "Ivanov Ivan",
                "fronted developer",
                LocalDateTime.of(2025, 6, 8, 13, 4),
                1,
                0)
        );
        save(new Candidate(0,
                "Petrov Stas",
                "ios developer",
                LocalDateTime.of(2025, 7, 5, 13, 55),
                1,
                0)
        );
        save(new Candidate(0,
                "Petrov Emil",
                "mobile developer",
                LocalDateTime.of(2025, 6, 15, 13, 4),
                1,
                0)
        );
        save(new Candidate(0,
                "Borisova Marina",
                "mobile developer",
                LocalDateTime.of(2025, 9, 1, 13, 24),
                2,
                0)
        );
    }

    @Override
    public Candidate save(Candidate candidate) {
        return candidates.computeIfAbsent(nextId.get(), key ->
                new Candidate(
                        nextId.getAndIncrement(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate(),
                        candidate.getCityId(),
                        candidate.getFileId()
                )
        );
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
                        candidate.getCreationDate(),
                        candidate.getCityId(),
                        candidate.getFileId()
                )
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
