package com.example.tournament.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tournament.domain.entity.TournamentTeam;
import com.example.tournament.domain.entity.TournamentTeamId;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TournamentTeamRepository extends JpaRepository<TournamentTeam, TournamentTeamId> {

    List<TournamentTeam> findByTournamentId(Long tournamentId);

    List<TournamentTeam> findByTournamentIdOrderByJoinedAtAsc(Long tournamentId);

    long countByTournamentId(Long tournamentId);

    boolean existsByTeam_Id(Long teamId);

    // โลโก้ใหม่ของทีมให้แสดงในรายการที่ยังไม่จบ ส่วนรายการที่จบแล้วเก็บโลโก้ ณ ตอนนั้นไว้
    @Modifying
    @Query("""
            UPDATE TournamentTeam tt
            SET tt.teamLogoUrl = :logoUrl
            WHERE tt.id.teamId = :teamId
              AND (tt.teamLogoUrl IS NULL
                   OR tt.id.tournamentId IN (
                       SELECT t.id FROM Tournament t
                       WHERE t.status <> com.example.tournament.domain.enums.TournamentStatus.COMPLETED))
            """)
    int refreshTeamLogo(@Param("teamId") Long teamId, @Param("logoUrl") String logoUrl);

    @Query("""
            SELECT COUNT(tt)
            FROM TournamentTeam tt
            WHERE tt.team.id = :teamId
              AND tt.tournament.id <> :tournamentId
              AND tt.tournament.startDate <= :endDate
              AND tt.tournament.endDate >= :startDate
            """)
    long countOverlappingTournaments(
            @Param("teamId") Long teamId,
            @Param("tournamentId") Long tournamentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
