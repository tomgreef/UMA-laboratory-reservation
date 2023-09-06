import { FC, useContext, useState } from "react";
import { ApplicationContext } from "../../App";
import { useNavigate } from "react-router-dom";
import AsignationCalendar from "./calendar/AssignationCalendar";
import AssignationConflicts from "./conflicts/AssignationConflicts";
import { useCustomQuery } from "../../utils/Query";
import { AssignmentDto, ExportData, TaskStatus } from "./Assignation.types";
import { CSVLink } from "react-csv";
import Loading from "../../components/Loading";
import Error from "../../components/Error";
import { Box, Button, ToggleButton, ToggleButtonGroup, Typography } from "@mui/material";
import BoxWrapper from "../../components/BoxWrapper";
import { PathConstants } from "../../routes/PathConstants";

type Alignment = "list" | "calendar";

const AssignationPage: FC = () => {
  const { applicationContext } = useContext(ApplicationContext);
  const { semester } = applicationContext;
  const navigate = useNavigate();
  const [alignment, setAlignment] = useState<Alignment>("list");

  const { data, error, loading, refetch } = useCustomQuery<AssignmentDto, { semesterId?: number }>("assignations", { variables: { semesterId: semester?.id }, skip: !semester });

  const getExportData = () => {
    const csvData: ExportData[] = data!.assignments.map((assignment) => {
      return {
        centro: assignment.laboratory.location,
        titulación: assignment.reservation.degree.name,
        asignatura: assignment.reservation.subject.name,
        curso: assignment.reservation.subject.course.toString(),
        grupo: `${assignment.reservation.subject.group} - ${assignment.reservation.subject.subgroup}`,
        fecha_inicio: assignment.reservation.startDate,
        fecha_fin: assignment.reservation.endDate,
        dia: assignment.reservation.day,
        hora_inicio: assignment.reservation.startTime,
        hora_fin: assignment.reservation.endTime,
        aula: assignment.laboratory.name,
        descripción: "",
        tipo: assignment.reservation.type,
      };
    });
    return csvData;
  };

  const handleTogleChange = (_: React.MouseEvent<HTMLElement>, newAlignment: Alignment) => {
    setAlignment(newAlignment);
  };

  if (loading) {
    return (
      <BoxWrapper>
        <Loading />
      </BoxWrapper>
    );
  }

  if (error || !data?.task) {
    return (
      <BoxWrapper>
        <Error errorMessage={error} />
      </BoxWrapper>
    );
  }

  if (data.task.status === TaskStatus.ERROR || data.task.status === TaskStatus.OUTDATED) {
    return (
      <BoxWrapper>
        <Error errorMessage={data.task.status === TaskStatus.OUTDATED ? "El algoritmo ha tardado más de 30 minutos en completarse y la tarea se ha marcado como caducado." : data.task.errorMessages} />
      </BoxWrapper>
    );
  }

  if (data.task.status !== TaskStatus.COMPLETED)
    return (
      <BoxWrapper>
        <Box sx={{ display: "flex", justifyContent: "space-between" }}>
          <Typography>Tus reservas se estan procesando</Typography>
          <Button onClick={() => refetch()}>Refrescar</Button>
        </Box>
      </BoxWrapper>
    );

  return (
    <>
      <Box display="flex" justifyContent="space-between" alignItems="center">
        {alignment === "list" ? <Typography variant="h5">Conflictos</Typography> : <Typography variant="h5">Calendario de asignaciones</Typography>}
        <Box>
          <ToggleButtonGroup value={alignment} exclusive onChange={handleTogleChange} size="small">
            <ToggleButton value="list">Lista</ToggleButton>
            <ToggleButton value="calendar">Calendario</ToggleButton>
          </ToggleButtonGroup>
          {data.conflicts.length !== 0 && (
            <Button onClick={() => navigate(PathConstants.HOME)} sx={{ mr: 2, ml: 4 }}>
              Importar CSV de nuevo
            </Button>
          )}
          <CSVLink data={getExportData()} filename={`Asignaciones ${semester?.startYear}-${semester?.endYear} ${semester?.period === 1 ? "primer" : "segundo"} cuatrimestre`}>
            <Button variant="contained" sx={{ my: 2 }}>
              EXPORTAR
            </Button>
          </CSVLink>
        </Box>
      </Box>
      <BoxWrapper>{alignment === "list" ? <AssignationConflicts conflicts={data.conflicts} /> : <AsignationCalendar assignments={data.assignments} />}</BoxWrapper>
    </>
  );
};

export default AssignationPage;
