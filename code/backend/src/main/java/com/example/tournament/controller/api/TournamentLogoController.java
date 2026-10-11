package com.example.tournament.controller.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tournament.dto.response.TournamentResponse;
import com.example.tournament.service.TournamentLogoService;

@RestController
@RequestMapping("/api/v1/tournaments/{tournamentId}/logo")
public class TournamentLogoController {

    private final TournamentLogoService logos;

    public TournamentLogoController(TournamentLogoService logos) {
        this.logos = logos;
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TournamentResponse upload(@PathVariable Long tournamentId, @RequestPart("file") MultipartFile file) {
        return logos.upload(tournamentId, file);
    }
}
