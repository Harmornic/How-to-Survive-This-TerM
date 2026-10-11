package com.example.tournament.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.tournament.dto.response.TournamentResponse;

public interface TournamentLogoService {

    TournamentResponse upload(Long tournamentId, MultipartFile file);
}
