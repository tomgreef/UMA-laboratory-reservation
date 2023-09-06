import { FC } from "react";
import { Reservation, ScheduleType } from "../../reservation/Reservation.types";
import { TableRow, TableCell, Typography, IconButton } from "@mui/material";
import { PathConstants } from "../../../routes/PathConstants";
import { useNavigate } from "react-router-dom";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { generateSpanishDate } from "../Assignation.types";

const ConflictTableRow: FC<{ reservation: Reservation }> = ({ reservation }) => {
  const navigate = useNavigate();

  return (
    <TableRow key={reservation.id} sx={{ backgroundColor: reservation.schedule === ScheduleType.ALTERNATIVE.toString() ? "success.light" : "inherit" }}>
      <TableCell>
        <Typography>
          {generateSpanishDate(reservation.startDate)} - {generateSpanishDate(reservation.endDate)} | {reservation.startTime.slice(0, 5)} - {reservation.endTime.slice(0, 5)}
        </Typography>
      </TableCell>
      <TableCell>
        {reservation.subject.name} - {reservation.subject.group} {reservation.subject.subgroup}
      </TableCell>
      <TableCell align="right">
        <IconButton
          sx={{ fontSize: "body2.fontSize", p: 0 }}
          onClick={() => {
            navigate(`${PathConstants.RESERVATIONS}/${reservation.id}`);
          }}
          disableRipple
        >
          {reservation.publicId}
          <VisibilityIcon sx={{ ml: 1 }} fontSize="small" />
        </IconButton>
      </TableCell>
    </TableRow>
  );
};

export default ConflictTableRow;
