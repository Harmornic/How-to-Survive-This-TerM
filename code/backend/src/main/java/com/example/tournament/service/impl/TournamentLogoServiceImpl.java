package com.example.tournament.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tournament.domain.entity.Tournament;
import com.example.tournament.dto.response.TournamentResponse;
import com.example.tournament.exception.ResourceNotFoundException;
import com.example.tournament.repository.TournamentRepository;
import com.example.tournament.service.TournamentLogoService;
import com.example.tournament.service.TournamentService;
import com.example.tournament.service.storage.FileStorageService;

@Service
public class TournamentLogoServiceImpl implements TournamentLogoService {

    private final TournamentRepository tournaments;
    private final TournamentService tournamentService;
    private final FileStorageService files;

    public TournamentLogoServiceImpl(TournamentRepository tournaments, TournamentService tournamentService,
            FileStorageService files) {
        this.tournaments = tournaments;
        this.tournamentService = tournamentService;
        this.files = files;
    }

    @Override
    @Transactional
    public TournamentResponse upload(Long tournamentId, MultipartFile file) {
        Tournament tournament = tournaments.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found: " + tournamentId));
        String filename = files.storeLogo(file);
        tournament.setLogoUrl("/api/v1/logos/" + filename);
        try {
            tournaments.saveAndFlush(tournament);
            return tournamentService.getById(tournamentId);
        } catch (RuntimeException exception) {
            try {
                files.deleteLogo(filename);
            } catch (RuntimeException cleanupError) {
                exception.addSuppressed(cleanupError);
            }
            throw exception;
        }
    }
}
