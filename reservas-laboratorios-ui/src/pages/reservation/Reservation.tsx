import { Box, Link, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip, Typography } from "@mui/material";
import { FC, useContext } from "react";
import Loading from "../../components/Loading";
import { useCustomQuery } from "../../utils/Query";
import { ApplicationContext } from "../../App";
import BoxWrapper from "../../components/BoxWrapper";
import Error from "../../components/Error";
import { Reservation } from "./Reservation.types";
import { useParams } from "react-router-dom";

const ReservationsPage: FC = () => {
  const { applicationContext } = useContext(ApplicationContext);
  const { id: paramId } = useParams();
  const { semester } = applicationContext;
  const { data, loading, error } = useCustomQuery<Reservation[], { semesterId?: number }>("reservations", {
    skip: !semester,
    variables: { semesterId: semester?.id },
  });

  if (error) {
    return (
      <BoxWrapper>
        <Error errorMessage={error} />
      </BoxWrapper>
    );
  }

  if (loading || !data) {
    return (
      <BoxWrapper>
        <Loading />
      </BoxWrapper>
    );
  }

  return (
    <>
      <Typography variant="h5">Reservas</Typography>
      <BoxWrapper>
        {data.length === 0 ? (
          <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "75vh" }}>
            <Typography variant="h5">No hay reservas, prueba importarlos</Typography>
          </Box>
        ) : (
          <TableContainer sx={{ maxHeight: "calc(100vh - 210px)", maxWidth: "calc(100vw - 280px)" }} component={Paper}>
            <Table stickyHeader size="small">
              <TableHead sx={{ backgroundColor: "secondary.main" }}>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Codigo</TableCell>
                  <TableCell>Tipo</TableCell>
                  <TableCell>Prioridad</TableCell>
                  <TableCell>Matricula</TableCell>
                  <TableCell>Asignatura</TableCell>
                  <TableCell>Profesor</TableCell>
                  <TableCell>Responsable</TableCell>
                  <TableCell>Fecha Inicio</TableCell>
                  <TableCell>Fecha Fin</TableCell>
                  <TableCell>Dia</TableCell>
                  <TableCell>Tiempo Inicio</TableCell>
                  <TableCell>Tiempo Final</TableCell>
                  <TableCell>Docencia</TableCell>
                  <TableCell>Preferencia Laboratorio</TableCell>
                  <TableCell>Ubicación</TableCell>
                  <TableCell align="right">Nº Alumnos</TableCell>
                  <TableCell>Sistema Operativo</TableCell>
                  <TableCell>Equipo Adicional</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.map((reservation) => (
                  <TableRow key={reservation.id} sx={{ backgroundColor: reservation.id.toString() === paramId ? "primary.light" : "inherit" }}>
                    <TableCell>{reservation.id}</TableCell>
                    <TableCell>{reservation.publicId}</TableCell>
                    <TableCell>{reservation.type}</TableCell>
                    <TableCell>{reservation.schedule}</TableCell>
                    <TableCell>{reservation.degree.name}</TableCell>
                    <TableCell>{reservation.subject.name}</TableCell>
                    <TableCell>
                      <Link href={`mailto:${reservation.professor.email}`}>{reservation.professor.name}</Link>
                    </TableCell>
                    <TableCell>
                      <Tooltip title={reservation.responsible.phone}>
                        <span>{reservation.responsible.name}</span>
                      </Tooltip>
                    </TableCell>
                    <TableCell>{reservation.startDate}</TableCell>
                    <TableCell>{reservation.endDate}</TableCell>
                    <TableCell>{reservation.day}</TableCell>
                    <TableCell>{reservation.startTime}</TableCell>
                    <TableCell>{reservation.endTime}</TableCell>
                    <TableCell>{reservation.teachingType}</TableCell>
                    <TableCell>{reservation.laboratoryPreference?.map((lab, index) => (index !== reservation.laboratoryPreference!.length - 1 ? `${lab} | ` : lab))}</TableCell>
                    <TableCell>{reservation.location}</TableCell>
                    <TableCell align="right">{reservation.studentsNumber}</TableCell>
                    <TableCell>{reservation.operatingSystem}</TableCell>
                    <TableCell>{reservation.additionalEquipment}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </BoxWrapper>
    </>
  );
};

export default ReservationsPage;
