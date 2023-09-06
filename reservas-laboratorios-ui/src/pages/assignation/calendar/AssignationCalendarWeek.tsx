import { FC } from "react";
import dayjs, { Dayjs } from "dayjs";
import CalendarDay from "./AssignationCalendarDay";
import { Grid } from "@mui/material";
import { Reservation } from "../../reservation/Reservation.types";

interface CalendarWeekProps {
  currentDate: Dayjs;
  reservations: Reservation[];
}

const CalendarWeek: FC<CalendarWeekProps> = ({ currentDate, reservations }) => {
  const renderCalendarWeek = () => {
    const weekDays = [];
    let currentDay = currentDate;

    for (let i = 0; i < 5; i++) {
      weekDays.push(currentDay);
      currentDay = currentDay.add(1, "day");
    }

    return weekDays;
  };

  const getReservationsForDay = (day: Dayjs) => {
    return reservations.filter((reservation) => DaysNumberOfWeek[reservation.dayType] === day.day() && dayjs(reservation.startDate, "YYYY-MM-DD").isBefore(day, "day") && dayjs(reservation.endDate, "YYYY-MM-DD").isAfter(day, "day"));
  };

  return (
    <Grid container spacing={2}>
      {renderCalendarWeek().map((day) => (
        <Grid item key={day.day()} xs={12 / renderCalendarWeek().length}>
          <CalendarDay day={day} reservations={getReservationsForDay(day)} />
        </Grid>
      ))}
    </Grid>
  );
};

export default CalendarWeek;

const DaysNumberOfWeek: Record<string, number> = {
  MONDAY: 1,
  TUESDAY: 2,
  WEDNESDAY: 3,
  THURSDAY: 4,
  FRIDAY: 5,
};
