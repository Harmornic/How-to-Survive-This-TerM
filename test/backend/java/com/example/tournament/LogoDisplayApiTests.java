package com.example.tournament;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.tournament.service.TournamentTeamService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.storage.logo-dir=target/test-logos")
class LogoDisplayApiTests {

    private static final byte[] PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/l7sAAAAASUVORK5CYII=");

    @Autowired private JdbcTemplate jdbc;
    @Autowired private MockMvc mvc;
    @Autowired private TournamentTeamService registrations;

    private final ObjectMapper json = new ObjectMapper();

    @Test
    void teamLogoUploadedAfterJoiningShowsInTournamentRoster() throws Exception {
        Long teamId = jdbc.queryForObject(
                "INSERT INTO teams (name, game_id) VALUES (?, 4) RETURNING id", Long.class, "logo-" + UUID.randomUUID());
        Long tournamentId = tournament();
        registrations.addTeam(tournamentId, teamId);

        String logoUrl = upload("/api/v1/teams/{id}/logo", teamId);

        mvc.perform(get("/api/v1/tournaments/{id}/teams", tournamentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamLogoUrl").value(logoUrl));
    }

    @Test
    void uploadsTournamentLogo() throws Exception {
        Long tournamentId = tournament();

        String logoUrl = upload("/api/v1/tournaments/{id}/logo", tournamentId);

        assertTrue(logoUrl.startsWith("/api/v1/logos/"));
        mvc.perform(get("/api/v1/tournaments/{id}", tournamentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logoUrl").value(logoUrl));
        mvc.perform(get(logoUrl)).andExpect(status().isOk());
    }

    private String upload(String path, Long id) throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "logo.png", "image/png", PNG);
        byte[] body = mvc.perform(multipart(path, id)
                .file(file)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        return json.readTree(body).get("logoUrl").asText();
    }

    private Long tournament() {
        LocalDate start = LocalDate.now().plusDays(30);
        return jdbc.queryForObject("""
                INSERT INTO tournaments (name, start_date, end_date, status, game_id, format)
                VALUES (?, ?, ?, 'UPCOMING', 4, 'SINGLE_ELIMINATION')
                RETURNING id
                """, Long.class, "logo-" + UUID.randomUUID(), start, start.plusDays(2));
    }
}
