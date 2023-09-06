import { Typography } from "@mui/material";
import { FC } from "react";
import SemesterList from "./SemesterList";

const SemestersPage: FC = () => {
  return (
    <>
      <Typography variant="h5">Cuatrimestres</Typography>
      <SemesterList />
    </>
  );
};

export default SemestersPage;
