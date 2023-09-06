import { Box, CircularProgress } from "@mui/material";
import { FC } from "react";

const Loading: FC = () => {
  return (
    <Box sx={{ display: "flex", justifyContent: "center" }}>
      <CircularProgress />
    </Box>
  );
};

export default Loading;
