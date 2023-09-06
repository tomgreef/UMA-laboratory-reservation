import { Grid, Accordion, AccordionSummary, Typography, AccordionDetails, Divider, Box } from "@mui/material";
import { FC } from "react";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { ReservationDtoWithError } from "./fileImport.types";

interface ImportErrorsProps {
  errors: ReservationDtoWithError[];
}

const ImportErrors: FC<ImportErrorsProps> = ({ errors }) => {
  return (
    <Grid container sx={{ py: 4, maxWidth: "75vw" }} spacing={2} justifyContent="center">
      {errors.map((reservation) => (
        <Grid item xs={12} key={reservation.rowNumber}>
          <Accordion>
            <AccordionSummary expandIcon={<ExpandMoreIcon />} sx={{ alignContent: "center" }}>
              <Box display="flex" alignItems="center">
                <Typography textAlign="center" pr={4}>
                  Línea {reservation.rowNumber}
                </Typography>
              </Box>
              <Divider orientation="vertical" flexItem />
              <Typography sx={{ wordWrap: "break-word", pl: 4 }}>{reservation.error}</Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Typography sx={{ wordWrap: "break-word" }} variant="body2">
                {JSON.stringify(reservation.dto)}
              </Typography>
            </AccordionDetails>
          </Accordion>
        </Grid>
      ))}
    </Grid>
  );
};

export default ImportErrors;
