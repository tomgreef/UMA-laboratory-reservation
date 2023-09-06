import { Grid } from "@mui/material";
import { Dayjs } from "dayjs";
import { FC } from "react";
import { Reservation } from "../../reservation/Reservation.types";

interface CalendarTimeSlotProps {
  timeslot: Dayjs;
  reservations: Reservation[];
}

const SLOT_MIN_HEIGHT = 70;

const CalendarTimeSlot: FC<CalendarTimeSlotProps> = ({ timeslot, reservations }) => {
  return (
    <Grid container sx={{ minHeight: SLOT_MIN_HEIGHT, height: "100%", border: 1 }}>
      <Grid item xs={12} px={2}>
        {timeslot.format("HH:mm")} - {timeslot.add(105, "minutes").format("HH:mm")}
      </Grid>
      {reservations.map((reservation) => (
        <Grid item xs={12 / (reservations.length > 3 ? 3 : reservations.length)} key={reservation.id + timeslot.format("HH:mm") + timeslot.day()} sx={{ backgroundColor: reservations.length > 1 ? "error.dark" : "primary.dark", display: "flex", justifyContent: "flex-end", px: 2, color: "white" }}>
          {reservation.publicId}
        </Grid>
      ))}
    </Grid>
  );
};

export default CalendarTimeSlot;
