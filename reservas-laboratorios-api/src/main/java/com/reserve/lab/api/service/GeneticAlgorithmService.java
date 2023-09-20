package com.reserve.lab.api.service;

import com.reserve.lab.api.config.AlgorithmProperties;
import com.reserve.lab.api.model.*;
import com.reserve.lab.api.model.helper.DateSlot;
import com.reserve.lab.api.model.helper.Solution;
import com.reserve.lab.api.model.type.PenaltyType;
import com.reserve.lab.api.model.type.ScheduleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeneticAlgorithmService {
    private static final double ALLOWED_PERCENTAGE_OF_CONFLICTS_PER_SUBJECT = 0.2;
    private final Random rand;
    private final AlgorithmProperties properties;
    private final ReservationService reservationService;
    private final LaboratoryService laboratoryService;
    private final ReservationAssignmentService reservationAssignmentService;
    private final ReservationConflictService reservationConflictService;

    public GeneticAlgorithmService(AlgorithmProperties properties, ReservationService reservationService, LaboratoryService laboratoryService, ReservationAssignmentService reservationAssignmentService, ReservationConflictService reservationConflictService) {
        this.properties = properties;
        this.reservationService = reservationService;
        this.laboratoryService = laboratoryService;
        this.reservationAssignmentService = reservationAssignmentService;
        this.reservationConflictService = reservationConflictService;
        this.rand = new Random();
    }


    public void runAlgorithm(Semester semester) {
        log.info("Running task for semester {} - {} | {}", semester.getStartYear(), semester.getEndYear(), semester.getPeriod());
        long algorithmStartTime = System.nanoTime();
        // Step 0: Remove any previous assignments to this semester
        reservationAssignmentService.deleteAllBySemester(semester);
        reservationConflictService.deleteAllBySemester(semester);

        // Step 1: Generate an initial population
        long startTime = System.nanoTime();
        List<Solution> population = generateInitialPopulation(semester);
        long endTime = System.nanoTime();
        log.info("Initial population generated ({})\t\t {} seconds", properties.getPopulationSize(), calculateTimeInSeconds(startTime, endTime));

        // Step 2: Evaluate fitness for each solution in the population
        startTime = System.nanoTime();
        evaluateFitness(population);
        endTime = System.nanoTime();
        log.info("Evaluated initial population \t\t\t {} seconds", calculateTimeInSeconds(startTime, endTime));

        // Step 3 to 8: Perform the Genetic Algorithm loop for a certain number of generations
        for (int generation = 1; generation <= properties.getMaxGeneration(); generation++) {
            // Step 3: Selection
            startTime = System.nanoTime();
            List<Solution> selectedSolutions = selection(population);
            endTime = System.nanoTime();
            log.info("Generation {} \t Selected solutions \t {} seconds", generation, calculateTimeInSeconds(startTime, endTime));

            // Step 4: Crossover
            startTime = System.nanoTime();
            List<Solution> offspring = crossover(selectedSolutions);
            endTime = System.nanoTime();
            log.info("Generation {} \t Offspring generated \t {} seconds", generation, calculateTimeInSeconds(startTime, endTime));

            // Step 5: Mutation
            startTime = System.nanoTime();
            mutate(offspring);
            endTime = System.nanoTime();
            log.info("Generation {} \t Offspring mutated \t\t {} seconds", generation, calculateTimeInSeconds(startTime, endTime));

            // Step 6: Evaluate fitness for offspring
            startTime = System.nanoTime();
            evaluateFitness(offspring);
            endTime = System.nanoTime();
            log.info("Generation {} \t Evaluated offspring \t {} seconds", generation, calculateTimeInSeconds(startTime, endTime));

            // Step 7: Replacement
            population = replacement(population, offspring);
            log.info("Generation {} \t Fitness evaluation \t {}\n", generation, getBestSolution(offspring).getPenaltyScore());

            // Step 8: stop early if a termination condition is met
            if (population.stream().anyMatch(solution -> solution.getPenaltyScore() == 0)) {
                break;
            }
        }

        // Step 9: Select the best solution
        Solution bestSolution = getBestSolution(population);

        // Step 10: Save the best solution and potential conflicts
        saveSolution(bestSolution, semester);
        log.info("Algorithm finished in {} seconds", calculateTimeInSeconds(algorithmStartTime, System.nanoTime()));
    }

    private List<Solution> generateInitialPopulation(Semester semester) {
        List<Reservation> reservations = reservationService.findAllBySemester(semester);
        List<Laboratory> laboratories = laboratoryService.findAll();
        List<Solution> population = new ArrayList<>();
        for (int i = 0; i < properties.getPopulationSize(); i++) {
            // Create a new random solution and add it to the population
            Solution solution = createRandomSolution(reservations, laboratories);
            population.add(solution);
        }
        return population;
    }

    public Solution createRandomSolution(List<Reservation> reservations, List<Laboratory> laboratories) {
        // Create a new empty solution
        Solution solution = new Solution();
        solution.setAssignments(new ArrayList<>());

        // Shuffle the reservations randomly to assign time slots
        List<Reservation> shuffledReservations = new ArrayList<>(reservations);
        Collections.shuffle(shuffledReservations);

        // Iterate through the shuffled reservations to assign and laboratories
        for (Reservation reservation : shuffledReservations) {
            List<Laboratory> suitableLaboratory = laboratories.stream().filter(l -> l.isSuitableFor(reservation)).toList();
            ReservationAssignment assignment = new ReservationAssignment(reservation, suitableLaboratory.get(rand.nextInt(suitableLaboratory.size())));
            solution.getAssignments().add(assignment);
        }

        // Calculate and set the penalty score for this random solution
        EnumMap<PenaltyType, Integer> penaltyOccurrences = getPenaltyOccurrences(solution);
        solution.setPenalties(penaltyOccurrences);

        return solution;
    }

    public EnumMap<PenaltyType, Integer> getPenaltyOccurrences(Solution solution) {
        EnumMap<PenaltyType, Integer> penaltyCount = new EnumMap<>(PenaltyType.class);
        List<ReservationAssignment> assignments = solution.getAssignments();
        HashMap<Subject, List<ReservationAssignment>> assignmentsPerSubjectGroup = new HashMap<>();

        solution.getAssignments().forEach(reservationAssignment -> {
            Subject subject = reservationAssignment.getReservation().getSubject();
            if (assignmentsPerSubjectGroup.containsKey(subject)) {
                assignmentsPerSubjectGroup.get(subject).add(reservationAssignment);
            } else {
                assignmentsPerSubjectGroup.put(subject, new ArrayList<>(List.of(reservationAssignment)));
            }
        });

        calculateHardRestrictionsPenalty(assignments, penaltyCount, assignmentsPerSubjectGroup);
        calculateSoftRestrictionsPenalty(assignments, penaltyCount, assignmentsPerSubjectGroup);

        return penaltyCount;
    }

    private void calculateHardRestrictionsPenalty(List<ReservationAssignment> assignments, EnumMap<PenaltyType, Integer> penaltyCount, HashMap<Subject, List<ReservationAssignment>> assignmentsPerSubjectGroup) {
        calculatePenaltyPerConflict(assignments, penaltyCount);

        // It would be preferred to have the reservations of the same subject in adjacent laboratories, otherwise apply penalty
        assignmentsPerSubjectGroup.forEach((subject, assignmentList) -> {
            if (assignmentList.size() == 1) {
                return;
            }

            List<Laboratory> listOfLaboratories = assignmentList.stream().map(ReservationAssignment::getLaboratory).toList();
            // Check if the laboratories are adjacent
            if (!listOfLaboratories.stream().allMatch(laboratory -> laboratory.getAdjacentLaboratories().stream().anyMatch(listOfLaboratories::contains))) {
                penaltyCount.put(PenaltyType.RESERVATIONS_OF_SAME_SUBJECT_NOT_IN_ADJACENT_LABORATORIES, penaltyCount.getOrDefault(PenaltyType.RESERVATIONS_OF_SAME_SUBJECT_NOT_IN_ADJACENT_LABORATORIES, 0) + 1);
            }
        });

        assignments.forEach(assignment -> {
            Laboratory laboratory = assignment.getLaboratory();
            Reservation reservation = assignment.getReservation();

            // If the reservation has a preferred laboratory, apply penalty if it is not assigned
            if (!reservation.getLaboratoryPreference().isEmpty() && !reservation.getLaboratoryPreference().contains(laboratory.getName())) {
                penaltyCount.put(PenaltyType.RESERVATION_PREFERRED_LABORATORY_NOT_ASSIGNED, penaltyCount.getOrDefault(PenaltyType.RESERVATION_PREFERRED_LABORATORY_NOT_ASSIGNED, 0) + 1);
            }

            // The reservations location needs to match the laboratories location
            if (reservation.getLocation() != null && !laboratory.getLocation().contains(reservation.getLocation())) {
                penaltyCount.put(PenaltyType.LABORATORY_IN_DIFFERENT_LOCALIZATION, penaltyCount.getOrDefault(PenaltyType.LABORATORY_IN_DIFFERENT_LOCALIZATION, 0) + 1);
            }

            // If any reservation requires specialized equipment and the assigned laboratory does not have them, apply penalty
            if (reservation.getAdditionalEquipment() != null && (laboratory.getAdditionalEquipment() == null || !laboratory.getAdditionalEquipment().contains(reservation.getAdditionalEquipment()))) {
                penaltyCount.put(PenaltyType.RESERVATION_ADDITIONAL_EQUIPMENT_NOT_AVAILABLE, penaltyCount.getOrDefault(PenaltyType.RESERVATION_ADDITIONAL_EQUIPMENT_NOT_AVAILABLE, 0) + 1);
            }

            // The reservations operating system needs to match the laboratories operating system
            if (reservation.getOperatingSystem() != null && (laboratory.getOperatingSystem() == null || !laboratory.getOperatingSystem().contains(reservation.getOperatingSystem()))) {
                penaltyCount.put(PenaltyType.LABORATORY_WITH_DIFFERENT_OPERATING_SYSTEM, penaltyCount.getOrDefault(PenaltyType.LABORATORY_WITH_DIFFERENT_OPERATING_SYSTEM, 0) + 1);
            }
        });
    }

    private void calculatePenaltyPerConflict(List<ReservationAssignment> assignments, EnumMap<PenaltyType, Integer> penaltyCount) {
        List<ReservationConflict> conflicts = reservationConflictService.findConflicts(assignments);

        conflicts.forEach(conflict -> {
            Reservation reservation1 = conflict.getReservation1();
            Reservation reservation2 = conflict.getReservation2();

            DateSlot dateSlot1 = new DateSlot(reservation1.getStartDate(), reservation1.getEndDate());
            DateSlot dateSlot2 = new DateSlot(reservation2.getStartDate(), reservation2.getEndDate());
            int weeksBetween = dateSlot1.getOverlappingWeeks(dateSlot2);

            if (reservation1.getScheduleType().equals(ScheduleType.ALTERNATIVE) || reservation2.getScheduleType().equals(ScheduleType.ALTERNATIVE)) {
                penaltyCount.put(PenaltyType.RESERVATION_CONFLICT_WITH_ALTERNATIVE_SCHEDULE, penaltyCount.getOrDefault(PenaltyType.RESERVATION_CONFLICT_WITH_ALTERNATIVE_SCHEDULE, 0) + 1);
                penaltyCount.put(PenaltyType.RESERVATION_CONFLICT_WITH_ALTERNATIVE_SCHEDULE_PER_WEEK, penaltyCount.getOrDefault(PenaltyType.RESERVATION_CONFLICT_WITH_ALTERNATIVE_SCHEDULE_PER_WEEK, 0) + weeksBetween);
            } else {
                penaltyCount.put(PenaltyType.RESERVATION_CONFLICT, penaltyCount.getOrDefault(PenaltyType.RESERVATION_CONFLICT, 0) + 1);
                penaltyCount.put(PenaltyType.RESERVATION_CONFLICT_PER_WEEK, penaltyCount.getOrDefault(PenaltyType.RESERVATION_CONFLICT_PER_WEEK, 0) + weeksBetween);
            }
        });

        // Make a list of reservations that have conflicts and check if they are distributed equally among the subjects
        List<Reservation> reservationsWithConflicts = new ArrayList<>(conflicts.stream().map(ReservationConflict::getReservation1).toList());
        reservationsWithConflicts.addAll(conflicts.stream().map(ReservationConflict::getReservation2).toList());

        List<Subject> subjectsWithConflicts = reservationsWithConflicts.stream().map(Reservation::getSubject).distinct().toList();
        subjectsWithConflicts.forEach(subject -> {
            List<Reservation> reservationsPerSubject = reservationsWithConflicts.stream().filter(reservation -> reservation.getSubject().getId().equals(subject.getId())).toList();
            double percentageOfConflictsForSubject = (double) reservationsWithConflicts.size() / reservationsPerSubject.size();

            if (percentageOfConflictsForSubject > ALLOWED_PERCENTAGE_OF_CONFLICTS_PER_SUBJECT) {
                penaltyCount.put(PenaltyType.RESERVATION_CONFLICTS_NOT_DISTRIBUTED_EQUALLY_ACROSS_SUBJECTS, penaltyCount.getOrDefault(PenaltyType.RESERVATION_CONFLICTS_NOT_DISTRIBUTED_EQUALLY_ACROSS_SUBJECTS, 0) + 1);
            }

        });
    }

    private void calculateSoftRestrictionsPenalty(List<ReservationAssignment> assignments, EnumMap<PenaltyType, Integer> penaltyCount, HashMap<Subject, List<ReservationAssignment>> assignmentsPerSubjectGroup) {
        // For a specific small group there cannot be more than one reservation
        assignmentsPerSubjectGroup.forEach((subject, assignmentsForSubject) -> {
            if (assignmentsForSubject.size() > 1) {
                penaltyCount.put(PenaltyType.MORE_THAN_ONE_RESERVATION_PER_GROUP, penaltyCount.getOrDefault(PenaltyType.MORE_THAN_ONE_RESERVATION_PER_GROUP, 0) + 1);
            }

            // Check if we are using the same laboratory for each group, otherwise apply penalty
            if (assignmentsForSubject.stream().map(ReservationAssignment::getLaboratory).map(Laboratory::getId).distinct().count() > 1) {
                penaltyCount.put(PenaltyType.RESERVATIONS_OF_SAME_SUBJECT_NOT_IN_SAME_LABORATORY, penaltyCount.getOrDefault(PenaltyType.RESERVATIONS_OF_SAME_SUBJECT_NOT_IN_SAME_LABORATORY, 0) + 1);
            }
        });

        // Apply penalty for those assignments that have matching reservation publicId and have different laboratories and different timeslots
        Map<Integer, List<ReservationAssignment>> assignmentsPerPublicId = assignments.stream().collect(Collectors.groupingBy(assignment -> assignment.getReservation().getPublicId()));
        assignmentsPerPublicId.forEach((publicId, assignmentsForPublicId) -> {
            if (assignmentsForPublicId.size() <= 1) {
                return;
            }

            if (assignmentsForPublicId.stream().map(ReservationAssignment::getLaboratory).map(Laboratory::getId).distinct().count() > 1) {
                for (int i = 0; i < assignmentsForPublicId.size() - 1; i++) {
                    for (int j = i + 1; j < assignmentsForPublicId.size(); j++) {
                        Reservation reservation1 = assignmentsForPublicId.get(i).getReservation();
                        Reservation reservation2 = assignmentsForPublicId.get(j).getReservation();

                        if (reservationConflictService.isReservationOverlapping(reservation1, reservation2)) {
                            penaltyCount.put(PenaltyType.RESERVATIONS_WITH_SAME_PUBLIC_ID_NOT_IN_SAME_LABORATORY, penaltyCount.getOrDefault(PenaltyType.RESERVATIONS_WITH_SAME_PUBLIC_ID_NOT_IN_SAME_LABORATORY, 0) + 1);
                        }
                    }
                }
            }
        });
    }

    private void evaluateFitness(List<Solution> population) {
        for (Solution solution : population) {
            EnumMap<PenaltyType, Integer> penalties = getPenaltyOccurrences(solution);
            solution.setPenalties(penalties);
        }
    }

    private List<Solution> selection(List<Solution> population) {
        // Step 1: Elitism Selection - Preserve a portion of the best solutions
        int elitismCount = (int) (population.size() * properties.getElitismSelectionRate());
        List<Solution> elitismSolutions = population.stream()
                .sorted(Comparator.comparingDouble(Solution::getPenaltyScore))
                .limit(elitismCount)
                .toList();

        List<Solution> selectedSolutions = new ArrayList<>(elitismSolutions);

        // Step 2: Rank-Based Selection - Select the rest based on ranking
        int remainingCount = properties.getPopulationSize() - elitismCount;
        if (remainingCount > 0) {
            double totalPenaltyScore = population.stream().mapToDouble(Solution::getPenaltyScore).sum();
            for (int i = 0; i < remainingCount; i++) {
                double sum = 0;
                double randomValue = rand.nextDouble();
                for (Solution solution : population) {
                    sum += (solution.getPenaltyScore() / totalPenaltyScore);
                    if (sum >= randomValue) {
                        selectedSolutions.add(solution);
                        break;
                    }
                }
            }
        }

        return selectedSolutions;
    }

    private List<Solution> crossover(List<Solution> selectedSolutions) {
        // Implement crossover between selected solutions to create new offspring
        List<Solution> offspring = new ArrayList<>();
        int populationSize = selectedSolutions.size();

        // Perform crossover for each pair of selected solutions
        for (int i = 0; i < populationSize - 1; i += 2) {
            Solution parent1 = selectedSolutions.get(i);
            Solution parent2 = selectedSolutions.get(i + 1);

            // Determine if crossover should occur based on the crossover rate
            if (rand.nextDouble() < properties.getCrossoverRate()) {
                // Create a new solution by combining reservations from parents
                Solution newOffspring = new Solution();
                List<ReservationAssignment> newReservations = new ArrayList<>();

                // Choose a random point to perform crossover
                int crossoverPoint = rand.nextInt(parent1.getAssignments().size());

                // Sort reservations within parents by reservation ID
                parent1.getAssignments().sort(Comparator.comparingLong(a -> a.getReservation().getId()));
                parent2.getAssignments().sort(Comparator.comparingLong(a -> a.getReservation().getId()));

                // Combine reservations up to crossover point from parent 1
                newReservations.addAll(parent1.getAssignments().subList(0, crossoverPoint));

                // Combine reservations after crossover point from parent 2
                newReservations.addAll(parent2.getAssignments().subList(crossoverPoint, parent2.getAssignments().size()));

                newOffspring.setAssignments(newReservations);

                // Calculate penalty score for the new offspring
                newOffspring.setPenalties(getPenaltyOccurrences(newOffspring));

                offspring.add(newOffspring);
            } else {
                // If crossover doesn't occur, pass parents directly as offspring
                offspring.add(parent1);
                offspring.add(parent2);
            }
        }

        return offspring;
    }

    private void mutate(List<Solution> offspring) {
        List<Laboratory> laboratories = laboratoryService.findAll();
        // Do a double mutation type, one for the whole solution and another for the reservations with conflicts
        // Implement a mutation method to introduce small random changes in the offspring solutions
        for (Solution solution : offspring) {
            for (ReservationAssignment reservationAssignment : solution.getAssignments()) {
                if (rand.nextDouble() < properties.getMutationRate()) {
                    assignNewLaboratory(reservationAssignment, laboratories);
                }
            }

            // Repair mutation: mutate the assignations with  conflicts by changing the assigned laboratory of the reservation to another available laboratory
            List<ReservationConflict> conflicts = reservationConflictService.findConflicts(solution.getAssignments());
            List<ReservationAssignment> assignmentsWithConflict = solution.getAssignments().stream().filter(assignment ->
                    hasMatchingReservation(assignment, conflicts)).toList();
            for (ReservationAssignment reservationAssignment : assignmentsWithConflict) {
                if (rand.nextDouble() < properties.getMutationRepairRate()) {
                    assignNewLaboratory(reservationAssignment, laboratories);
                }
            }
        }
    }

    private boolean hasMatchingReservation(ReservationAssignment reservationAssignment, List<ReservationConflict> conflicts) {
        return conflicts.stream().anyMatch(conflict ->
                conflict.getReservation1().getId().equals(reservationAssignment.getReservation().getId()) ||
                        conflict.getReservation2().getId().equals(reservationAssignment.getReservation().getId()));
    }

    private void assignNewLaboratory(ReservationAssignment reservationAssignment, List<Laboratory> laboratories) {
        List<Laboratory> availableLaboratories = laboratories.stream().filter(l -> l.isSuitableFor(reservationAssignment.getReservation()) && !Objects.equals(l.getId(), reservationAssignment.getLaboratory().getId())).toList();
        if (availableLaboratories.isEmpty()) {
            return;
        }

        Laboratory newLaboratory = availableLaboratories.get(rand.nextInt(availableLaboratories.size()));
        reservationAssignment.setLaboratory(newLaboratory);
    }

    private List<Solution> replacement(List<Solution> population, List<Solution> offspring) {
        List<Solution> newPopulation = new ArrayList<>();
        // Implement a replacement method (e.g., elitism, generational replacement) to form the next generation population
        // This method should combine solutions from the parent population and the offspring
        // based on their fitness scores

        // We use elitism method
        // We select the best solution from the parent population and the best solution from the offspring
        // And we add them to the new population
        Solution bestParentSolution = getBestSolution(population);
        Solution bestOffspringSolution = getBestSolution(offspring);

        newPopulation.add(bestParentSolution);
        newPopulation.add(bestOffspringSolution);

        // We add the rest of the solutions from the parent population and the offspring
        // to the new population
        population.remove(bestParentSolution);
        offspring.remove(bestOffspringSolution);

        newPopulation.addAll(population);
        newPopulation.addAll(offspring);

        // Take out the worst solutions from the new population to keep the population size constant
        // We sort the solutions by their penalty score in ascending order
        newPopulation.sort(Comparator.comparingDouble(Solution::getPenaltyScore));

        // We remove the worst solutions from the new population
        while (newPopulation.size() > properties.getPopulationSize()) {
            newPopulation.remove(newPopulation.size() - 1);
        }

        // Randomize the order
        Collections.shuffle(newPopulation);

        return newPopulation;
    }

    private Solution getBestSolution(List<Solution> population) {
        // Find and return the solution with the lowest penalty score (the best solution)
        return population.stream().min(Comparator.comparingDouble(Solution::getPenaltyScore)).orElse(population.get(0));
    }

    private void saveSolution(Solution solution, Semester semester) {
        // Implement this method to save the best solution and potential conflicts
        // You can use the log.info() method to print the solution and conflicts to the console
        List<ReservationAssignment> reservationAssignments = solution.getAssignments();
        log.info("Solution with penalty score {}", solution.getPenaltyScore());
        solution.printPenalties();
        reservationAssignments.forEach(assignment -> assignment.setSemester(semester));
        reservationAssignmentService.saveSolution(reservationAssignments);
        reservationConflictService.findAndSaveConflicts(reservationAssignments);
    }

    private String calculateTimeInSeconds(long startTime, long endTime) {
        // Round up to 3 decimals
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format((endTime - startTime) / 1000000000.0);
    }
}
