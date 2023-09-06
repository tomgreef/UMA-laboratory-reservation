import { AppBar, Button, Toolbar, Typography } from "@mui/material";
import { FC, useContext, useEffect } from "react";
import { ApplicationContext } from "../../App";
import { useNavigate } from "react-router-dom";
import SemesterDisplay from "../semester/SemesterDisplay";
import { useCustomQuery } from "../../utils/Query";
import { Semester } from "../semester/semester.types";
import { PathConstants } from "../../routes/PathConstants";

const Topbar: FC = () => {
  const { applicationContext, setApplicationContext } = useContext(ApplicationContext);
  const { semester } = applicationContext;
  const { data: activeSemester, loading: loadingActiveSemester } = useCustomQuery<Semester>("semesters/active");

  const navigate = useNavigate();

  useEffect(() => {
    if (!loadingActiveSemester) {
      setApplicationContext((prevState) => {
        return { ...prevState, semester: activeSemester };
      });
      if (activeSemester) {
        localStorage.setItem("semester", JSON.stringify(activeSemester));
      } else {
        localStorage.removeItem("semester");
      }
    }
  }, [activeSemester, loadingActiveSemester]);

  return (
    <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1, pl: 2 }}>
          Reservas Laboratorios
        </Typography>
        {semester ? (
          <Typography sx={{ backgroundColor: "primary", cursor: "pointer", mr: 5 }} onClick={() => navigate(PathConstants.SEMESTERS)}>
            <SemesterDisplay semester={semester} />
          </Typography>
        ) : (
          <Button onClick={() => navigate(PathConstants.SEMESTERS)} color="secondary" sx={{ mr: 5 }}>
            Seleccionar Cuatrimestre
          </Button>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Topbar;
