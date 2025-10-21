package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleCandidateService implements CandidateService {

    private final CandidateRepository candidateRepository;

    private final FileService fileService;

    @Autowired
    public SimpleCandidateService(CandidateRepository sql2oCandidateRepository, FileService fileService) {
        this.candidateRepository = sql2oCandidateRepository;
        this.fileService = fileService;
    }

    @Override
    public Candidate save(Candidate candidate, FileDto fileDto) {
        saveNewFile(candidate, fileDto);
        return candidateRepository.save(candidate);
    }

    private void saveNewFile(Candidate candidate, FileDto fileDto) {
        File file = fileService.save(fileDto);
        candidate.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Candidate> optCandidate = candidateRepository.findById(id);
        boolean isDeleted = candidateRepository.deleteById(id);
        optCandidate.ifPresent(candidate -> fileService.deleteId(candidate.getFileId()));
        return isDeleted;
    }

    @Override
    public boolean update(Candidate candidate, FileDto fileDto) {
        boolean isNewFileEmpty = fileDto.getContent().length == 0;
        if (isNewFileEmpty) {
            return candidateRepository.update(candidate);
        }
        int oldFileId = candidate.getFileId();
        saveNewFile(candidate, fileDto);
        boolean isUpdated = candidateRepository.update(candidate);
        fileService.deleteId(oldFileId);
        return isUpdated;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}
