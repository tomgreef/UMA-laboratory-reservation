import { Box, Grid, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import { FC } from "react";
import Loading from "../../components/Loading";
import Error from "../../components/Error";
import BoxWrapper from "../../components/BoxWrapper";
import { useCustomMutation } from "../../utils/Mutation";
import { useCustomQuery } from "../../utils/Query";
import { Laboratory } from "./Laboratory.types";
import UpsertLaboratory from "./LaboratoryUpsertDialog";
import Confirmation from "../../components/dialog/Confirmation";

const LaboratoriesPage: FC = () => {
  const { data, loading, error, refetch } = useCustomQuery<Laboratory[]>("laboratories");
  const [deleteLaboratory, { error: errorOnDelete, loading: loadingDelete }] = useCustomMutation<Laboratory[], number>("laboratories", "DELETE");

  const handleDelete = (laboratory: Laboratory) => {
    deleteLaboratory(laboratory.id).then(() => refetch());
  };

  if (error || errorOnDelete) {
    return (
      <BoxWrapper>
        <Error errorMessage={error || errorOnDelete} />
      </BoxWrapper>
    );
  }

  if (loading || loadingDelete || !data) {
    return (
      <BoxWrapper>
        <Loading />
      </BoxWrapper>
    );
  }

  return (
    <>
      <Box display="flex" justifyContent="space-between">
        <Typography variant="h5">Laboratorios</Typography>
        <UpsertLaboratory refetchLaboratories={refetch} existingLaboratories={data} />
      </Box>
      <BoxWrapper>
        <TableContainer component={Paper} sx={{ maxHeight: "calc(100vh - 210px)", maxWidth: "calc(100vw - 280px)" }}>
          <Table sx={{ minWidth: 650 }} stickyHeader>
            <TableHead>
              <TableRow>
                <TableCell>Nombre</TableCell>
                <TableCell align="right">Capacidad</TableCell>
                <TableCell>Localización</TableCell>
                <TableCell>Sistema operativo</TableCell>
                <TableCell>Equipo adicional</TableCell>
                <TableCell></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {data.map((laboratory) => (
                <TableRow key={laboratory.id}>
                  <TableCell>{laboratory.name}</TableCell>
                  <TableCell align="right">{laboratory.capacity}</TableCell>
                  <TableCell>{laboratory.location}</TableCell>
                  <TableCell>{laboratory.operatingSystem}</TableCell>
                  <TableCell>{laboratory.additionalEquipment}</TableCell>
                  <TableCell>
                    <Grid container justifyContent="space-between" spacing={1}>
                      <Grid item>
                        <UpsertLaboratory refetchLaboratories={refetch} existingLaboratories={data} laboratory={laboratory} />
                      </Grid>
                      <Grid item>
                        <Confirmation smallButton onConfirm={() => handleDelete(laboratory)} message="¿Estás seguro de que quieres borrar este laboratorio? También borrará todas las asignaciones del cuatrimestre a la que esté asignado el laboratorio" />
                      </Grid>
                    </Grid>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </BoxWrapper>
    </>
  );
};

export default LaboratoriesPage;
