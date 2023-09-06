import { Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, Grid, InputLabel, MenuItem, Select, SelectChangeEvent, TextField, debounce } from "@mui/material";
import { FC, useCallback, useContext, useState } from "react";
import { useCustomMutation } from "../../utils/Mutation";
import { ApplicationContext } from "../../App";
import { Semester, SemesterDto } from "./semester.types";

const YEAR_REG_EXP = new RegExp(/^\d{4}$/);

type CreateSemesterProps = {
  existingSemesters: Semester[];
  refetchSemesters: () => void;
  disabled?: boolean;
};

const CreateSemester: FC<CreateSemesterProps> = ({ existingSemesters, refetchSemesters, disabled }) => {
  const { setApplicationContext } = useContext(ApplicationContext);
  const [openDialog, setOpenDialog] = useState(false);
  const [startYear, setStartYear] = useState<string>();
  const [endYear, setEndYear] = useState("");
  const [period, setPeriod] = useState<1 | 2>(1);

  const [yearHelper, setYearHelper] = useState<string>();
  const [yearError, setYearError] = useState<string>();

  const [saveSemester] = useCustomMutation<Semester, SemesterDto>("semesters");

  const handleStartYearChange = useCallback(
    debounce((e: React.ChangeEvent<HTMLInputElement>) => {
      const startYear = e.target.value;

      if (startYear.length !== 4 || !YEAR_REG_EXP.test(startYear)) {
        setYearHelper("El año debe tener 4 dígitos");
        return;
      }

      setStartYear(startYear);
      setYearHelper(undefined);

      setEndYear((parseInt(startYear) + 1).toString());
    }, 500),
    [startYear, endYear]
  );

  const handleSave = () => {
    const existingSemester = existingSemesters.find((semester) => semester.startYear === parseInt(startYear!) && semester.period === period!);
    if (existingSemester && existingSemester.period === period) {
      setYearError("Ya existe un cuatrimestre con ese año de inicio y periodo");
      return;
    }
    setYearError(undefined);

    saveSemester({ startYear: parseInt(startYear!), endYear: parseInt(endYear!), period: period! }).then(({ data: semester }) => {
      setApplicationContext((prevState) => {
        return { ...prevState, semester };
      });
      localStorage.setItem("semester", JSON.stringify(semester));
      refetchSemesters();
      setOpenDialog(false);
    });
  };

  return (
    <>
      <Button onClick={() => setOpenDialog(true)} disabled={disabled}>
        Crear cuatrimestre
      </Button>
      {openDialog && (
        <Dialog open onClose={() => setOpenDialog(false)}>
          <DialogTitle>Crear cuatrimestre</DialogTitle>
          <DialogContent>
            <Grid container pt={4} spacing={4}>
              <Grid container item xs={12} spacing={4}>
                <Grid item xs={6}>
                  <TextField fullWidth required label="Año de inicio" onChange={handleStartYearChange} helperText={yearHelper || yearError} error={!!yearError} />
                </Grid>
                <Grid item xs={6}>
                  <TextField fullWidth label="Año de fin" value={endYear} disabled />
                </Grid>
              </Grid>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel required>Periodo</InputLabel>
                  <Select label="Periodo" required value={period} onChange={(e: SelectChangeEvent<1 | 2>) => setPeriod(e.target.value as 1 | 2)}>
                    <MenuItem value={1}>Primero</MenuItem>
                    <MenuItem value={2}>Segundo</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button disabled={!startYear || !endYear || !period || !!yearHelper} onClick={handleSave}>
              Crear
            </Button>
            <Button onClick={() => setOpenDialog(false)}>Cancelar</Button>
          </DialogActions>
        </Dialog>
      )}
    </>
  );
};

export default CreateSemester;
