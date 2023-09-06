import { FormControl, InputLabel, Select, MenuItem, SelectChangeEvent, Grid, Typography } from "@mui/material";
import { useContext } from "react";
import { useCustomQuery } from "../../utils/Query";
import { ApplicationContext } from "../../App";
import { useCustomMutation } from "../../utils/Mutation";
import Loading from "../../components/Loading";
import Error from "../../components/Error";
import CreateSemester from "./SemesterCreateDialog";
import { Semester } from "./semester.types";
import SemesterDisplay from "./SemesterDisplay";

const SemesterSelector = () => {
  const { data: semesters, loading, error, refetch } = useCustomQuery<Semester[]>("semesters");
  const [setActiveSemester] = useCustomMutation<Semester, Pick<Semester, "id">>("semesters/active");
  const { applicationContext, setApplicationContext } = useContext(ApplicationContext);
  const { semester } = applicationContext;
  const activeSemester = semester || semesters?.find((semester) => semester.active);

  const handleChange = (event: SelectChangeEvent<number>) => {
    const selectedId = event.target.value as number;

    setActiveSemester({ id: selectedId }).then(({ data: semester }) =>
      setApplicationContext((prevState) => {
        localStorage.setItem("semester", JSON.stringify(semester));
        return { ...prevState, semester };
      })
    );
  };

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return <Error />;
  }

  return (
    <Grid container alignItems="center">
      <Grid item xs={8}>
        {semesters?.length === 0 ? (
          <Typography>No hay cuatrimestres disponibles</Typography>
        ) : (
          <FormControl fullWidth>
            <InputLabel required>Cuatrimestre</InputLabel>
            <Select value={activeSemester?.id || ""} label="Cuatrimestre" onChange={handleChange} required>
              {semesters?.map((semester) => (
                <MenuItem value={semester.id} key={semester.id}>
                  <SemesterDisplay semester={semester} />
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        )}
      </Grid>
      <Grid item xs={4} display="flex" justifyContent="flex-end">
        <CreateSemester existingSemesters={semesters!} refetchSemesters={refetch} />
      </Grid>
    </Grid>
  );
};

export default SemesterSelector;
