package com.goltracker.user.repository;

import com.goltracker.user.domain.Role;
import com.goltracker.user.domain.User;
import com.goltracker.user.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetToken(String resetToken);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByStatus(UserStatus status);
    List<User> findByStatusAndRole(UserStatus status, Role role);
    long countByStatusAndRole(UserStatus status, Role role);

    @Query(value = """
            SELECT u.username,
                   COALESCE(SUM(c.points), 0)                             AS totalPoints,
                   COUNT(c.id)                                             AS predicted,
                   SUM(CASE WHEN c.points = 4  THEN 1 ELSE 0 END)         AS exactCount,
                   SUM(CASE WHEN c.points > 0  THEN 1 ELSE 0 END)         AS correctCount
            FROM users u
            LEFT JOIN (
                SELECT user_id, id, points FROM predictions
                UNION ALL
                SELECT user_id, id, points FROM knockout_predictions
            ) c ON c.user_id = u.id
            WHERE u.status = 'ACTIVE' AND u.role = 'USER'
            GROUP BY u.id, u.username
            ORDER BY totalPoints DESC, exactCount DESC, correctCount DESC
            """, nativeQuery = true)
    List<Object[]> findRankingRaw();

    @Query(value = """
            SELECT u.username,
                   COALESCE(SUM(c.points), 0)                             AS totalPoints,
                   COUNT(c.id)                                             AS predicted,
                   SUM(CASE WHEN c.points = 4  THEN 1 ELSE 0 END)         AS exactCount,
                   SUM(CASE WHEN c.points > 0  THEN 1 ELSE 0 END)         AS correctCount
            FROM users u
            LEFT JOIN (
                SELECT user_id, id, points FROM predictions
                WHERE match_id IN (SELECT id FROM matches WHERE tournament_id = :tournamentId)
                UNION ALL
                SELECT user_id, id, points FROM knockout_predictions
            ) c ON c.user_id = u.id
            WHERE u.status = 'ACTIVE' AND u.role = 'USER'
            GROUP BY u.id, u.username
            ORDER BY totalPoints DESC, exactCount DESC, correctCount DESC
            """, nativeQuery = true)
    List<Object[]> findRankingRawByTournament(@Param("tournamentId") Long tournamentId);
}
