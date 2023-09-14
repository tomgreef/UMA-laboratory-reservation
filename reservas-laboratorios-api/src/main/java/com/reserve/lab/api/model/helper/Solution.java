package com.reserve.lab.api.model.helper;

import com.reserve.lab.api.model.ReservationAssignment;
import com.reserve.lab.api.model.type.PenaltyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class Solution {
    private List<ReservationAssignment> assignments;
    private EnumMap<PenaltyType, Integer> penalties;

    public Solution() {
        this.assignments = new ArrayList<>();
        this.penalties = new EnumMap<>(PenaltyType.class);
    }

    public double getPenaltyScore() {
        double score = 0;
        double a1ConstraintPenalty = 0;
        double totalPenalties = 0;

        for (Map.Entry<PenaltyType, Integer> entry : penalties.entrySet()) {
            PenaltyType penalty = entry.getKey();
            Integer occurrences = entry.getValue();

            if (penalty.isA1Constraint()) {
                a1ConstraintPenalty += occurrences;
            }

            totalPenalties += occurrences;
            score += penalty.getScore(occurrences);
        }

        // If a1 constraint penalties are X % (less than 10%) of the total penalties, then the score is reduced by 75 %
        if (a1ConstraintPenalty < totalPenalties * 0.1) {
            score *= 0.25;
        }

        return score;
    }

    public void printPenalties() {
        log.info("Penalties: (Type: Occurrences)");
        for (Map.Entry<PenaltyType, Integer> entry : penalties.entrySet()) {
            PenaltyType key = entry.getKey();
            Integer value = entry.getValue();
            log.info("\t" + key + ": " + value);
        }
    }
}
