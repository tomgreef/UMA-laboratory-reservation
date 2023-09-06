import { createTheme } from "@mui/material";

export const theme = createTheme({
  typography: {
    fontSize: 14,
  },
  palette: {
    primary: {
      dark: "#053C5E",
      main: "#1F7A8C",
      light: "#BFDBF7",
    },
    secondary: {
      dark: "#498469",
      main: "#ABB58E",
      light: "#FB9039",
    },
    background: {
      default: "#DFDFFD",
    },
  },
  spacing: 4,
  components: {
    MuiButton: {
      defaultProps: {
        variant: "contained",
      },
    },
  },
});
