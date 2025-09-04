package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.*;

public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "without salary", LocalDateTime.of(2025, 9, 4, 10, 5)));
        save(new Vacancy(0, "Junior Java Developer", "low salary", LocalDateTime.of(2025, 9, 4, 16, 22)));
        save(new Vacancy(0, "Junior+ Java Developer", "average salary", LocalDateTime.of(2025, 9, 1, 12, 55)));
        save(new Vacancy(0, "Middle Java Developer", "average+ salary", LocalDateTime.of(2025, 8, 4, 12, 34)));
        save(new Vacancy(0, "Middle+ Java Developer", "high salary", LocalDateTime.of(2025, 7, 4, 15, 24)));
        save(new Vacancy(0, "Senior Java Developer", "high+ salary", LocalDateTime.of(2025, 6, 20, 9, 0)));
    }

    public static VacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public void deleteById(int id) {
        vacancies.remove(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(
                        oldVacancy.getId(),
                        vacancy.getTitle(),
                        vacancy.getDescription(),
                        vacancy.getCreationDate())
        ) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}
