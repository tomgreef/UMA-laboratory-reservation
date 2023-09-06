import { Box, Divider, Typography } from "@mui/material";
import { FC, useContext } from "react";
import FileImport from "./fileImport/FileImport";
import SemesterSelector from "../semester/SemesterSelector";
import { ApplicationContext } from "../../App";
import BoxWrapper from "../../components/BoxWrapper";

const LandingPage: FC = () => {
  const { applicationContext } = useContext(ApplicationContext);
  const { semester } = applicationContext;

  return (
    <>
      <Typography variant="h5">Importar CSV</Typography>
      <Box sx={{ py: 4, display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column", width: "100%", maxWidth: "100%" }}>
        <BoxWrapper>
          <SemesterSelector />
        </BoxWrapper>
        <Divider />
        <FileImport disabled={!semester} semester={semester} />
      </Box>
    </>
  );
};

export default LandingPage;
