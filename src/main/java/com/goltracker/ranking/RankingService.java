package com.goltracker.ranking;

import com.goltracker.ranking.dto.RankingEntryDto;
import com.goltracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<RankingEntryDto> getRanking() {
        List<Object[]> rows = userRepository.findRankingRaw();
        List<RankingEntryDto> result = new ArrayList<>(rows.size());
        for (int i = 0; i < rows.size(); i++) {
            Object[] row = rows.get(i);
            result.add(new RankingEntryDto(
                    i + 1,
                    (String)  row[0],
                    ((Number) row[1]).longValue(),
                    ((Number) row[2]).longValue(),
                    ((Number) row[3]).longValue(),
                    ((Number) row[4]).longValue()
            ));
        }
        return result;
    }
}
