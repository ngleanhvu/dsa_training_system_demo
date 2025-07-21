package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.response.ContestParticipantLeaderboard;
import com.ngleanhvu.dsa_training_system.dto.response.ContestParticipantResultResponse;
import com.ngleanhvu.dsa_training_system.entity.Contest;
import com.ngleanhvu.dsa_training_system.entity.ContestParticipant;
import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.exception.InvalidValueException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.ContestParticipantRepo;
import com.ngleanhvu.dsa_training_system.repo.ContestRepo;
import com.ngleanhvu.dsa_training_system.repo.UserRepo;
import com.ngleanhvu.dsa_training_system.service.ContestParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestParticipantServiceImpl implements ContestParticipantService {

    private final UserRepo userRepo;
    private final ContestRepo contestRepo;
    private final ContestParticipantRepo contestParticipantRepo;

    @Transactional
    @Override
    public void enroll(String userId, int contestId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        Contest contest = contestRepo.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest","contestId",String.valueOf(contestId)));

        if (contestParticipantRepo.findByUserIdAndContestId(userId, contestId).isPresent())
            throw new InvalidValueException("You have already enrolled this contest");

        ContestParticipant contestParticipant = ContestParticipant.builder()
                .contest(contest)
                .user(user)
                .status(1)
                .build();

        contestParticipantRepo.save(contestParticipant);
    }

    @Override
    public List<ContestParticipantLeaderboard> getContestParticipantLeaderboard(Integer contestId) {
        List<Object[]> rows = contestParticipantRepo.getRawContestParticipantResults(contestId);

        Map<String, ContestParticipantLeaderboard> map = new LinkedHashMap<>();

        for (Object[] row : rows) {
            String email = (String) row[0];
            String displayName = (String) row[1];
            Integer problemId = (Integer) row[2];
            Boolean solved = (Boolean) row[3];
            Integer wrongCount = ((Number) row[4]).intValue();
            LocalDateTime solvedAtRaw = (LocalDateTime) row[5];

            ContestParticipantResultResponse result = ContestParticipantResultResponse.builder()
                    .problemId(problemId)
                    .solved(solved != null && solved)
                    .wrongCount(wrongCount)
                    .solvedTime(solvedAtRaw)
                    .build();

            map.computeIfAbsent(email, k -> ContestParticipantLeaderboard.builder()
                    .email(email)
                    .displayName(displayName)
                    .contestParticipantResultResponseList(new ArrayList<>())
                    .build());

            map.get(email).getContestParticipantResultResponseList().add(result);
        }

        return new ArrayList<>(map.values());
    }


}
