import { Box, Container, Toolbar, Typography, useMediaQuery, useTheme } from "@mui/material";
import { FC } from "react";
import { Outlet } from "react-router-dom";
import Topbar from "./LayoutTopbar";
import NavigationSidebar from "./LayoutNavSidebar";

const Layout: FC = () => {
  const isMobile = useMediaQuery(useTheme().breakpoints.down("md"));

  if (isMobile)
    return (
      <Container sx={{ height: "100vh", width: "100vw", display: "flex", justifyContent: "center", alignItems: "center" }}>
        <Typography variant="h2" textAlign="center" color="error">
          La aplicación no está disponible en versión móvil
        </Typography>
      </Container>
    );

  return (
    <Box sx={{ display: "flex" }}>
      <Topbar />
      <NavigationSidebar />
      <Box component="main" sx={{ flexGrow: 1, px: 8, py: 2 }}>
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
};

export default Layout;
