import { Box, Grid, TextField, Typography } from "@mui/material";
import { CSSProperties, FC, ReactNode, useContext, useEffect, useState } from "react";
import { useCSVReader } from "react-papaparse";
import Loading from "../../../components/Loading";
import { Data, RESERVATION_DTO_MAPPING, ReservationDtoWithError } from "./fileImport.types";
import Error from "../../../components/Error";
import { createPortal } from "react-dom";
import { useCustomMutation } from "../../../utils/Mutation";
import BoxWrapper from "../../../components/BoxWrapper";
import { useNavigate } from "react-router-dom";
import { PathConstants } from "../../../routes/PathConstants";
import ImportErrors from "./FileImportErrors";
import Confirmation from "../../../components/dialog/Confirmation";
import { Semester } from "../../semester/semester.types";
import { useCustomQuery } from "../../../utils/Query";
import { ApplicationContext } from "../../../App";

const FileImport: FC<{ disabled?: boolean; semester?: Semester }> = ({ disabled, semester }) => {
  const { CSVReader } = useCSVReader();
  const [zoneHover, setZoneHover] = useState(false);
  const [delimiter, setDelimiter] = useState(",");
  const [quoteChar, setQuoteChar] = useState('"');
  const [reservationWithErrors, setReservationWithErrors] = useState<ReservationDtoWithError[]>();
  const [showConfirmationDialog, setShowConfirmationDialog] = useState<ReactNode | null>(null);
  const navigate = useNavigate();
  const { setApplicationContext } = useContext(ApplicationContext);

  const {
    data: existingReservations,
    error,
    refetch,
  } = useCustomQuery<boolean, { semesterId?: number }>("existing-reservations", {
    skip: semester?.id === undefined,
    variables: { semesterId: semester?.id },
  });

  useEffect(() => {
    setApplicationContext((prev) => ({ ...prev, existingReservations: existingReservations }));
  }, [existingReservations, setApplicationContext]);

  useEffect(() => {
    refetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [semester]);

  const [uploadExcel, { error: uploadError }] = useCustomMutation<ReservationDtoWithError[], { [k: string]: string }[]>("upload-reservations");

  /*
  const removeExtraQuotesFromExcel = (csvData: { [k: number]: string }[]) => {
    return csvData.map((row) => {
      return Object.keys(row).reduce((acc: { [key: number]: string }, key: string) => {
        acc[Number(key)] = row[Number(key)].replace(/["\\]/g, "");
        return acc;
      }, {});
    });
  };
*/

  const removeHeadersFromExcel = (csvData: { [k: number]: string }[]) => {
    return csvData.slice(1);
  };

  const openConfirmationDialog = () => {
    return new Promise<boolean>((resolve) => {
      setShowConfirmationDialog(
        <Confirmation
          onConfirm={() => resolve(true)}
          message={"¿Estás seguro que quieres importar estas reservaciones? Esto eliminará todas las reservaciones existentes para este cuatrimestre"}
          onCancel={() => {
            resolve(false);
            setShowConfirmationDialog(null);
          }}
        />
      );
    });
  };

  const handleImport = async (csvData: { [k: number]: string }[]) => {
    if (existingReservations) {
      const shouldImport = await openConfirmationDialog();
      if (!shouldImport) {
        setZoneHover(false);
        return;
      }
    }

    // const processedExcel = removeExtraQuotesFromExcel(csvData);
    const processedExcel = removeHeadersFromExcel(csvData);

    const mappedExcelToHeader = processedExcel.map((row) => {
      const mappedRow: { [k: string]: string } = {};
      Object.values(row).map((value, key) => {
        const headerKey = RESERVATION_DTO_MAPPING[key];
        if (headerKey) {
          mappedRow[headerKey] = value;
        }
      });

      return mappedRow;
    });

    uploadExcel(mappedExcelToHeader).then((result) => {
      if (result.data?.length === 0) {
        setApplicationContext((prev) => ({ ...prev, existingReservations: true }));
        navigate(PathConstants.ASSIGNATIONS);
        return;
      }

      setReservationWithErrors(result.data);
    });
  };

  if (uploadError || error) {
    return <Error errorMessage={error || uploadError} />;
  }

  return (
    <>
      {showConfirmationDialog && createPortal(showConfirmationDialog, document.body)}
      {reservationWithErrors && (
        <BoxWrapper>
          <Typography variant="h6">Errores en el archivo importado</Typography>
          <ImportErrors errors={reservationWithErrors} />
        </BoxWrapper>
      )}
      <BoxWrapper disabled={disabled}>
        <BoxWrapper soft>
          <Box display="flex" justifyContent="center">
            <CSVReader
              config={{ delimiter, quoteChar }}
              onUploadAccepted={(results: Data) => {
                handleImport(results.data);
                setZoneHover(false);
              }}
              onDragOver={(event: DragEvent) => {
                event.preventDefault();
                setZoneHover(true);
              }}
              onDragLeave={(event: DragEvent) => {
                event.preventDefault();
                setZoneHover(false);
              }}
              disabled={disabled}
            >
              {({
                getRootProps,
                acceptedFile,
              }: // eslint-disable-next-line @typescript-eslint/no-explicit-any
              any) => (
                <Box {...getRootProps()} sx={Object.assign({}, styles.zone, zoneHover && styles.zoneHover)}>
                  {acceptedFile ? <Loading /> : "Suelte el archivo CSV aquí o haga clic para cargarlo"}
                </Box>
              )}
            </CSVReader>
          </Box>
        </BoxWrapper>
        <Typography variant="h6">Configuración archivo CSV</Typography>
        <Grid container sx={{ py: 4 }}>
          <Grid item xs={6}>
            <TextField
              label="Delimitador"
              value={delimiter}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                setDelimiter(event.target.value);
              }}
              size="small"
              disabled={disabled}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              label="Caracter de cita"
              value={quoteChar}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                setQuoteChar(event.target.value);
              }}
              size="small"
              disabled={disabled}
            />
          </Grid>
        </Grid>
      </BoxWrapper>
    </>
  );
};

const GREY = "#CCC";
const GREY_DIM = "#686868";

const styles = {
  zone: {
    alignItems: "center",
    border: `2px dashed ${GREY}`,
    borderRadius: 10,
    display: "flex",
    flexDirection: "column",
    height: 60,
    justifyContent: "center",
    padding: 6,
    width: 600,
  } as CSSProperties,
  file: {
    background: "linear-gradient(to bottom, #EEE, #DDD)",
    borderRadius: 20,
    display: "flex",
    height: 120,
    width: 120,
    position: "relative",
    zIndex: 10,
    flexDirection: "column",
    justifyContent: "center",
  } as CSSProperties,
  zoneHover: {
    borderColor: GREY_DIM,
  } as CSSProperties,
  default: {
    borderColor: GREY,
  } as CSSProperties,
};

export default FileImport;
