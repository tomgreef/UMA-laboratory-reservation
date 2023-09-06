import { FC, Fragment, useEffect, useState, useContext } from "react";
import { useCustomQuery } from "../../utils/Query";
import { Semester } from "./semester.types";
import Loading from "../../components/Loading";
import Error from "../../components/Error";
import { Box, Divider, FormControl, FormControlLabel, FormLabel, Grid, Radio, RadioGroup, Typography } from "@mui/material";
import BoxWrapper from "../../components/BoxWrapper";
import CreateSemester from "./SemesterCreateDialog";
import SemesterDisplay from "./SemesterDisplay";
import Confirmation from "../../components/dialog/Confirmation";
import { useCustomMutation } from "../../utils/Mutation";
import { ApplicationContext } from "../../App";

const SemesterList: FC = () => {
  const { applicationContext, setApplicationContext } = useContext(ApplicationContext);
  const [semesters, setSemesters] = useState<Semester[]>();
  const { data, loading, error, refetch } = useCustomQuery<Semester[]>("semesters");
  const [deleteSemester, { error: errorOnDelete, loading: loadingDelete }] = useCustomMutation<Semester[], number>("semesters", "DELETE");
  const [setActiveSemester] = useCustomMutation<Semester, Pick<Semester, "id">>("semesters/active");

  useEffect(() => {
    if (data) {
      setSemesters(data);
      const activeSemester = data.find((semester) => semester.active);
      setApplicationContext((prevState) => {
        return { ...prevState, semester: activeSemester };
      });
      if (activeSemester) {
        localStorage.setItem("semester", JSON.stringify(activeSemester));
      } else {
        localStorage.removeItem("semester");
      }
    }
  }, [data]);

  const handleDelete = (semester: Semester) => {
    deleteSemester(semester.id).then((queryResult) => {
      if (semester.active) {
        setApplicationContext((prevState) => {
          return { ...prevState, semester: undefined };
        });
        localStorage.removeItem("semester");
      }
      setSemesters(queryResult.data);
    });
  };

  const handleSelectActive = (semester: Semester) => {
    setActiveSemester({ id: semester.id }).then(() =>
      setApplicationContext((prevState) => {
        return { ...prevState, semester };
      })
    );
    localStorage.setItem("semester", JSON.stringify(semester));
  };

  if (error || errorOnDelete) {
    return (
      <BoxWrapper>
        <Error />
      </BoxWrapper>
    );
  }

  if (loading || loadingDelete || !semesters) {
    return (
      <BoxWrapper>
        <Loading />
      </BoxWrapper>
    );
  }

  const renderSemesters = () => {
    return semesters.map((semester, index) => (
      <Fragment key={semester.id}>
        <Box display="flex" justifyContent="space-between" alignItems="center" py={2}>
          <SemesterDisplay semester={semester} />
          <Box>
            <Confirmation onConfirm={() => handleDelete(semester)} message="¿Estás seguro de que quieres borrar este cuatrimestre? También borrará todas las reservas y asignaciones realizadas a este cuatrimestre" />
            <FormControlLabel value={semester.id} control={<Radio />} label={undefined} sx={{ mr: 8, ml: 10 }} />
          </Box>
        </Box>
        {index !== semesters.length - 1 && <Divider />}
      </Fragment>
    ));
  };

  return (
    <>
      <Grid container spacing={4}>
        <Grid item xs={9}>
          <BoxWrapper>
            {semesters.length === 0 ? (
              <Typography>No hay cuatrimestres creado</Typography>
            ) : (
              <FormControl fullWidth>
                <FormLabel sx={{ textAlign: "right", fontWeight: "bold" }}>Seleccionado</FormLabel>
                <RadioGroup
                  value={applicationContext.semester?.id || semesters?.find((semester) => semester.active)?.id || ""}
                  onChange={(value) => {
                    const semester = semesters!.find((semester) => semester.id.toString() === value.target.value);
                    handleSelectActive(semester!);
                  }}
                >
                  {renderSemesters()}
                </RadioGroup>
              </FormControl>
            )}
          </BoxWrapper>
        </Grid>
        <Grid item xs={3} textAlign="center" mt={4}>
          <CreateSemester existingSemesters={semesters!} refetchSemesters={refetch} disabled={loading} />
        </Grid>
      </Grid>
    </>
  );
};

export default SemesterList;
