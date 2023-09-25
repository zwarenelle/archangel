package org.acme.schooltimetabling;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.acme.schooltimetabling.domain.Lesson;
import org.acme.schooltimetabling.domain.Room;
import org.acme.schooltimetabling.domain.TimeTable;
import org.acme.schooltimetabling.domain.Timeslot;
import org.acme.schooltimetabling.solver.TimeTableConstraintProvider;
import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeTableApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeTableApp.class);

    public static void main(String[] args) {
        SolverFactory<TimeTable> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(TimeTable.class)
                .withEntityClasses(Lesson.class)
                .withConstraintProviderClass(TimeTableConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(5)));

        // Load the problem
        TimeTable problem = generateDemoData();

        // Solve the problem
        Solver<TimeTable> solver = solverFactory.buildSolver();
        TimeTable solution = solver.solve(problem);

        // Visualize the solution
        printTimetable(solution);
    }

    public static TimeTable generateDemoData() {
        List<Timeslot> timeslotList = new ArrayList<>(10);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 00), LocalTime.of(10, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 00), LocalTime.of(12, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(12, 00), LocalTime.of(14, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 00), LocalTime.of(16, 00)));

        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 00), LocalTime.of(10, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 00), LocalTime.of(12, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(12, 00), LocalTime.of(14, 00)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 00), LocalTime.of(16, 00)));

        List<Room> roomList = new ArrayList<>(3);
        roomList.add(new Room("Ploeg A"));
        roomList.add(new Room("Ploeg B"));
        roomList.add(new Room("Ploeg C"));

        List<Lesson> lessonList = new ArrayList<>();
        long id = 0;
        lessonList.add(new Lesson(id++, "Math", "A. Turing", "Gas"));
        lessonList.add(new Lesson(id++, "Math", "A. Turing", "Gas"));
        lessonList.add(new Lesson(id++, "Physics", "M. Curie", "Gas"));
        lessonList.add(new Lesson(id++, "Chemistry", "M. Curie", "Gas"));
        lessonList.add(new Lesson(id++, "History", "I. Jones", "Gas"));
        lessonList.add(new Lesson(id++, "English", "I. Jones", "Gas"));
        lessonList.add(new Lesson(id++, "English", "I. Jones", "Gas"));
        lessonList.add(new Lesson(id++, "Geography", "P. Cruz", "Gas"));

        lessonList.add(new Lesson(id++, "Math", "A. Turing", "Elektra"));
        lessonList.add(new Lesson(id++, "Math", "A. Turing", "Elektra"));
        lessonList.add(new Lesson(id++, "Math", "A. Turing", "Elektra"));
        lessonList.add(new Lesson(id++, "Physics", "M. Curie", "Elektra"));
        lessonList.add(new Lesson(id++, "Chemistry", "M. Curie", "Elektra"));
        lessonList.add(new Lesson(id++, "Geography", "C. Darwin", "Elektra"));
        lessonList.add(new Lesson(id++, "English", "P. Cruz", "Elektra"));
        lessonList.add(new Lesson(id++, "Spanish", "P. Cruz", "Elektra"));

        lessonList.add(new Lesson(id++, "ICT", "T. Slikker", "Water"));
        lessonList.add(new Lesson(id++, "ICT", "T. Slikker", "Water"));

        return new TimeTable(timeslotList, roomList, lessonList);
    }

    private static void printTimetable(TimeTable timeTable) {
        LOGGER.info("");
        List<Room> roomList = timeTable.getRooms();
        List<Lesson> lessonList = timeTable.getLessons();
        Map<Timeslot, Map<Room, List<Lesson>>> lessonMap = lessonList.stream()
                .filter(lesson -> lesson.getTimeslot() != null && lesson.getRoom() != null)
                .collect(Collectors.groupingBy(Lesson::getTimeslot, Collectors.groupingBy(Lesson::getRoom)));
        LOGGER.info("|                      | " + roomList.stream()
                .map(room -> String.format("%-20s", room.getName())).collect(Collectors.joining(" | ")) + " |");
        LOGGER.info("|" + "----------------------|".repeat(roomList.size() + 1));
        for (Timeslot timeslot : timeTable.getTimeslots()) {
            List<List<Lesson>> cellList = roomList.stream()
                    .map(room -> {
                        Map<Room, List<Lesson>> byRoomMap = lessonMap.get(timeslot);
                        if (byRoomMap == null) {
                            return Collections.<Lesson>emptyList();
                        }
                        List<Lesson> cellLessonList = byRoomMap.get(room);
                        if (cellLessonList == null) {
                            return Collections.<Lesson>emptyList();
                        }
                        return cellLessonList;
                    })
                    .collect(Collectors.toList());

            LOGGER.info("| " + String.format("%-20s",
                    timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getStartTime()) + " | "
                    + cellList.stream().map(cellLessonList -> String.format("%-20s",
                            cellLessonList.stream().map(Lesson::getSubject).collect(Collectors.joining(", "))))
                            .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|                      | "
                    + cellList.stream().map(cellLessonList -> String.format("%-20s",
                            cellLessonList.stream().map(Lesson::getTeacher).collect(Collectors.joining(", "))))
                            .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|                      | "
                    + cellList.stream().map(cellLessonList -> String.format("%-20s",
                            cellLessonList.stream().map(Lesson::getStudentGroup).collect(Collectors.joining(", "))))
                            .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|" + "----------------------|".repeat(roomList.size() + 1));
        }
        List<Lesson> unassignedLessons = lessonList.stream()
                .filter(lesson -> lesson.getTimeslot() == null || lesson.getRoom() == null)
                .collect(Collectors.toList());
        if (!unassignedLessons.isEmpty()) {
            LOGGER.info("");
            LOGGER.info("Unassigned lessons");
            for (Lesson lesson : unassignedLessons) {
                LOGGER.info("  " + lesson.getSubject() + " - " + lesson.getTeacher() + " - " + lesson.getStudentGroup());
            }
        }
    }

}