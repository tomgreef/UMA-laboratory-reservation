import { Box, Button, Typography } from "@mui/material";
import React from "react";

interface ErrorProps {
  errorMessage?: string;
}

const Error: React.FC<ErrorProps> = ({ errorMessage }) => {
  const handleRefresh = () => {
    window.location.reload();
  };

  return (
    <Box sx={{ width: "100%", p: 4, display: "flex", alignItems: "center", justifyContent: "center", flexDirection: "column" }}>
      <Box display="flex" alignItems="center" mb={3}>
        <Typography color="error" sx={{ mr: 5 }}>
          Se ha producido un error
        </Typography>
        <Button onClick={handleRefresh} size="small">
          Refrescar
        </Button>
      </Box>
      <Typography variant="body2">{errorMessage}</Typography>
    </Box>
  );
};

export default Error;
