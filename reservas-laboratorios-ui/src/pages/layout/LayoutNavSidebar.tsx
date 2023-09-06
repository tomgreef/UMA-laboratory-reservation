import { Drawer, List, ListItem, ListItemButton, ListItemText, Toolbar, Tooltip } from "@mui/material";
import { FC, useContext, useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import routes from "../../routes";
import { ApplicationContext } from "../../App";
import { useCustomQuery } from "../../utils/Query";

const DRAWER_WIDTH = 150;

const NavigationSidebar: FC = () => {
  const { applicationContext, setApplicationContext } = useContext(ApplicationContext);
  const navigate = useNavigate();
  const { semester } = applicationContext;
  const location = useLocation();
  const [selectedIndex, setSelectedIndex] = useState(0);

  useEffect(() => {
    const index = routes.findIndex((route) => route.path === location.pathname);
    setSelectedIndex(index);
  }, [location]);

  const { data: existingReservations, refetch } = useCustomQuery<boolean, { semesterId?: number }>("existing-reservations", {
    skip: semester?.id === undefined,
    variables: { semesterId: semester?.id },
  });

  useEffect(() => {
    setApplicationContext((prev) => ({ ...prev, existingReservations: existingReservations }));
  }, [existingReservations, setApplicationContext]);

  useEffect(() => {
    refetch();
  }, [semester]);

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: DRAWER_WIDTH,
        flexShrink: 0,
        [`& .MuiDrawer-paper`]: { width: DRAWER_WIDTH, boxSizing: "border-box" },
      }}
    >
      <Toolbar />
      <List>
        {routes
          .filter((route) => !route.ignoreInMenu)
          .map((route, index) => {
            const disabled = route.requiresSemester && (semester === undefined || (existingReservations === false && applicationContext.existingReservations === false));

            const listItem = (
              <ListItem disablePadding key={route.name} divider={index === 2}>
                <ListItemButton onClick={() => navigate(route.path)} disabled={disabled} selected={selectedIndex === index}>
                  <ListItemText sx={{ px: 2, py: 1, borderRadius: 1 }} primary={route.name} />
                </ListItemButton>
              </ListItem>
            );

            if (disabled) {
              return (
                <Tooltip title="Selecciona un cuatrimestre y importe un archivo para habilitar esta opción" key={route.name}>
                  {listItem}
                </Tooltip>
              );
            }
            return listItem;
          })}
      </List>
    </Drawer>
  );
};

export default NavigationSidebar;
