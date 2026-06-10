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
    long countByStatusAndRole(UserStatus status, Role role);

    @Query(value = """
            SELECT u.username,
                   COALESCE(SUM(p.points), 0)                             AS totalPoints,
                   COUNT(p.id)                                             AS predicted,
                   SUM(CASE WHEN p.points = 4  THEN 1 ELSE 0 END)         AS exactCount,
                   SUM(CASE WHEN p.points > 0  THEN 1 ELSE 0 END)         AS correctCount
            FROM users u
            LEFT JOIN predictions p ON p.user_id = u.id
            WHERE u.status = 'ACTIVE' AND u.role = 'USER'
            GROUP BY u.id, u.username
            ORDER BY totalPoints DESC, predicted DESC
            """, nativeQuery = true)
    List<Object[]> findRankingRaw();

    @Query(value = """
            SELECT u.username,
                   COALESCE(SUM(p.points), 0)                             AS totalPoints,
                   COUNT(p.id)                                             AS predicted,
                   SUM(CASE WHEN p.points = 4  THEN 1 ELSE 0 END)         AS exactCount,
                   SUM(CASE WHEN p.points > 0  THEN 1 ELSE 0 END)         AS correctCount
            FROM users u
            LEFT JOIN predictions p ON p.user_id = u.id
                   AND p.match_id IN (SELECT id FROM matches WHERE tournament_id = :tournamentId)
            WHERE u.status = 'ACTIVE' AND u.role = 'USER'
            GROUP BY u.id, u.username
            ORDER BY totalPoints DESC, predicted DESC
            """, nativeQuery = true)
    List<Object[]> findRankingRawByTournament(@Param("tournamentId") Long tournamentId);
}
