import { FC, useState } from "react";
import { Assignment } from "../Assignation.types";
import { Box, Typography, AppBar, IconButton, Toolbar } from "@mui/material";
import dayjs, { Dayjs } from "dayjs";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import LaboratorySelector from "./AssignationCalendarLaboratorySelector";
import { Laboratory } from "../../laboratory/Laboratory.types";
import CalendarWeek from "./AssignationCalendarWeek";

interface CalendarProps {
  assignments: Assignment[];
}

const AsignationCalendar: FC<CalendarProps> = ({ assignments }) => {
  const earliestReservation = assignments.reduce((earliestAssignment, assignment) => {
    const startDate = dayjs(assignment.reservation.startDate, "YYYY-MM-DD");
    const earliestDate = dayjs(earliestAssignment?.reservation.startDate, "YYYY-MM-DD");
    return !earliestAssignment || startDate.isBefore(earliestDate) ? assignment : earliestAssignment;
  }, assignments[0]);
  const latestReservation = assignments.reduce((latestAssignment, assignment) => {
    const endDate = dayjs(assignment.reservation.endDate, "YYYY-MM-DD");
    const latestDate = dayjs(latestAssignment?.reservation.endDate, "YYYY-MM-DD");
    return !latestAssignment || endDate.isAfter(latestDate) ? assignment : latestAssignment;
  }, assignments[0]);

  const earliestDate = getFirstDayOfWeek(earliestReservation.reservation.startDate!);
  const latestDate = getFirstDayOfWeek(latestReservation.reservation.endDate!);

  const [selectedLaboratory, setSelectedLaboratory] = useState<Laboratory>(earliestReservation.laboratory);
  const [currentDate, setCurrentDate] = useState<Dayjs>(earliestDate);

  function getFirstDayOfWeek(startDate: string) {
    const date = dayjs(startDate, "YYYY-MM-DD");
    const dayOfWeek = date.day();
    const daysToSubtract = (dayOfWeek + 7 - 1) % 7;
    return date.subtract(daysToSubtract, "day");
  }

  const handlePreviousWeek = () => {
    setCurrentDate((prevDate) => prevDate.subtract(7, "days"));
  };

  const handleNextWeek = () => {
    setCurrentDate((prevDate) => prevDate.add(7, "days"));
  };

  const getUniqueLaboratories = () => {
    const laboratories: Laboratory[] = [];
    assignments.forEach((assignment) => {
      if (!laboratories.map((lab) => lab.id).includes(assignment.laboratory.id)) {
        laboratories.push(assignment.laboratory);
      }
    });
    return laboratories;
  };

  const getReservationsForSelectedLaboratory = assignments.filter((assignment) => assignment.laboratory.id === selectedLaboratory.id).map((assignment) => assignment.reservation);

  return (
    <>
      <AppBar position="static" sx={{ backgroundColor: "primary.main" }}>
        <Toolbar sx={{ display: "flex", justifyContent: "center" }} variant="dense">
          <IconButton edge="start" color="inherit" onClick={handlePreviousWeek} disabled={currentDate.isSame(earliestDate, "day")} size="small">
            <ChevronLeftIcon fontSize="small" />
          </IconButton>
          <Typography>Week of {currentDate.format("MMM DD, YYYY")}</Typography>
          <IconButton edge="end" color="inherit" onClick={handleNextWeek} disabled={currentDate.isSame(latestDate, "day")} size="small">
            <ChevronRightIcon fontSize="small" />
          </IconButton>
        </Toolbar>
      </AppBar>
      <LaboratorySelector laboratories={getUniqueLaboratories()} selectedLaboratoryState={[selectedLaboratory, setSelectedLaboratory]} />
      <Box pt={2}>
        <CalendarWeek currentDate={currentDate} reservations={getReservationsForSelectedLaboratory} />
      </Box>
    </>
  );
};

export default AsignationCalendar;
