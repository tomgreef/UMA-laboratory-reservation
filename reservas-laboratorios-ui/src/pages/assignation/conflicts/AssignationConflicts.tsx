import { FC, Fragment } from "react";
import { Conflict } from "../Assignation.types";
import { Box, Divider, Paper, Typography } from "@mui/material";
import ConflictTable from "./AssignationConflictTable";

interface ConflictsProps {
  conflicts: Conflict[];
}
const AssignationConflicts: FC<ConflictsProps> = ({ conflicts }) => {
  const conflictsPerLaboratory: Record<number, Conflict[]> = {};

  conflicts.forEach((conflict) => {
    // Separate conflicts by laboratory
    const { laboratory } = conflict;
    if (!conflictsPerLaboratory[laboratory.id]) {
      conflictsPerLaboratory[laboratory.id] = [];
    }
    conflictsPerLaboratory[laboratory.id].push(conflict);
  });

  const renderConflictsByLaboratory = () => {
    return Object.keys(conflictsPerLaboratory).map((laboratoryId) => {
      const laboratoryConflicts = conflictsPerLaboratory[parseInt(laboratoryId)];

      const laboratory = conflicts.find((conflict) => conflict.laboratory.id === parseInt(laboratoryId))?.laboratory;

      if (!laboratory) {
        return null;
      }

      return (
        <Fragment key={laboratory.id}>
          <Divider />
          <Box sx={{ py: 4 }}>
            <ConflictTable laboratoryConflicts={laboratoryConflicts} laboratory={laboratory} />
          </Box>
        </Fragment>
      );
    });
  };

  return (
    <Paper sx={{ p: 4 }}>
      <Typography textAlign="center" mb={2} fontWeight="bold">
        Número de conflictos: {conflicts.length}
      </Typography>
      <Divider />
      {conflicts.length === 0 ? (
        <Typography variant="h6" textAlign="center" p={6}>
          🎉 No hay conflictos 🎉
        </Typography>
      ) : (
        <Box sx={{ maxHeight: "80%" }}>{renderConflictsByLaboratory()}</Box>
      )}
    </Paper>
  );
};

export default AssignationConflicts;
