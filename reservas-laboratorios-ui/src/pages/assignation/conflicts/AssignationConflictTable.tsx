import { Table, TableBody, TableCell, TableContainer, TableRow, Typography } from "@mui/material";
import { FC, Fragment } from "react";
import { Conflict, generateSpanishDate } from "../Assignation.types";
import { Laboratory } from "../../laboratory/Laboratory.types";
import ConflictTableRow from "./AssignationConflictRow";

interface ConflictTableProps {
  laboratoryConflicts: Conflict[];
  laboratory: Laboratory;
}

const ConflictTable: FC<ConflictTableProps> = ({ laboratoryConflicts, laboratory }) => {
  return (
    <Fragment key={laboratory.id}>
      <Typography variant="h6">
        {laboratory.name} - {laboratory.location}
      </Typography>
      {laboratoryConflicts.map((conflict) => {
        const { reservation1, reservation2 } = conflict;
        return (
          <TableContainer key={conflict.id} sx={{ my: 2, border: 1, borderColor: "primary.light" }}>
            <Table>
              <TableBody>
                <ConflictTableRow reservation={reservation1} />
                <ConflictTableRow reservation={reservation2} />
                <TableRow>
                  <TableCell colSpan={3}>
                    <Typography color="error.main" textAlign="center" fontWeight="bold">
                      {DaysOfWeek[conflict.day]} en el periodo {conflict.startTime.slice(0, 5)} - {conflict.endTime.slice(0, 5)}. Desde {generateSpanishDate(conflict.startDate)} hasta {generateSpanishDate(conflict.endDate)}
                    </Typography>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </TableContainer>
        );
      })}
    </Fragment>
  );
};

export default ConflictTable;

const DaysOfWeek: Record<string, string> = {
  MONDAY: "Lunes",
  TUESDAY: "Martes",
  WEDNESDAY: "Miércoles",
  THURSDAY: "Jueves",
  FRIDAY: "Viernes",
  SATURDAY: "Sábado",
  SUNDAY: "Domingo",
};
