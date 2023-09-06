import { Dispatch, FC, SetStateAction, createContext, useState } from "react";
import { Route, Routes } from "react-router-dom";
import NotFoundPage from "./pages/NotFound";
import Layout from "./pages/layout/Layout";
import { ThemeProvider } from "@mui/material/styles";
import { PathConstants } from "./routes/PathConstants";
import routes from "./routes";
import { theme } from "./theme";
import { Semester } from "./pages/semester/semester.types";

type ApplicationContextType = {
  semester?: Semester;
  existingReservations?: boolean;
};

export const ApplicationContext = createContext<{
  applicationContext: ApplicationContextType;
  setApplicationContext: Dispatch<SetStateAction<ApplicationContextType>>;
  // eslint-disable-next-line @typescript-eslint/no-empty-function
}>({ applicationContext: {}, setApplicationContext: () => {} });

const App: FC = () => {
  const localStorageSemester = localStorage.getItem("semester") !== "undefined" ? localStorage.getItem("semester") : null;
  const [applicationContext, setApplicationContext] = useState<ApplicationContextType>({ semester: localStorageSemester ? (JSON.parse(localStorageSemester) as Semester) : undefined });

  return (
    <ThemeProvider theme={theme}>
      <ApplicationContext.Provider value={{ applicationContext, setApplicationContext }}>
        <Routes>
          <Route path={PathConstants.HOME} element={<Layout />}>
            {routes.map((route) => {
              if (route.path === PathConstants.HOME) {
                return <Route index element={route.element} key={route.name} />;
              }
              return <Route key={route.path} path={route.path} element={route.element} />;
            })}

            <Route path="*" element={<NotFoundPage />} />
          </Route>
        </Routes>
      </ApplicationContext.Provider>
    </ThemeProvider>
  );
};

export default App;
