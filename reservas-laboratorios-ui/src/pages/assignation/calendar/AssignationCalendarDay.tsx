import { Grid, Typography } from "@mui/material";
import dayjs, { Dayjs } from "dayjs";
import { FC } from "react";
import { START_TIME, END_TIME, BREAK_START_TIME, BREAK_END_TIME, DURATION_HOURS, WeekDays } from "../Assignation.types";
import CalendarTimeSlot from "./AssignationCalendarTimeSlot";
import { Reservation } from "../../reservation/Reservation.types";

interface CalendarDayProps {
  day: Dayjs;
  reservations: Reservation[];
}

const CalendarDay: FC<CalendarDayProps> = ({ day, reservations }) => {
  const dayNumber: 1 | 2 | 3 | 4 | 5 = day.day() as 1 | 2 | 3 | 4 | 5;

  const generateTimeSlots = () => {
    const timeSlots = [];
    let currentTime = START_TIME;

    while (currentTime.isBefore(END_TIME)) {
      // Skip the break time
      if (currentTime.isAfter(BREAK_START_TIME) && currentTime.isBefore(BREAK_END_TIME)) {
        currentTime = BREAK_END_TIME;
      }

      // Add the current time slot to the array
      timeSlots.push(currentTime);

      // Move to the next time slot
      currentTime = currentTime.add(DURATION_HOURS, "hours");
    }

    return timeSlots;
  };

  const createDateWithNewTime = (newTime: string) => {
    const datePart = day.format("YYYY-MM-DD");
    const newDateTime = `${datePart}T${newTime}`;
    return dayjs(newDateTime);
  };

  const getReservationsForTimeSlot = (timeSlot: Dayjs) => {
    return reservations.filter((reservation) => createDateWithNewTime(reservation.startTime!).hour() === timeSlot.hour());
  };

  return (
    <Grid container spacing={1.5}>
      <Grid item xs={12}>
        <Typography textAlign="center">{WeekDays[dayNumber]}</Typography>
      </Grid>
      {generateTimeSlots().map((timeSlot) => (
        <Grid item key={timeSlot.format("hh")} xs={12}>
          <CalendarTimeSlot timeslot={timeSlot} reservations={getReservationsForTimeSlot(timeSlot)} />
        </Grid>
      ))}
    </Grid>
  );
};

export default CalendarDay;
