import { FC } from "react";
import { Semester } from "./semester.types";

const SemesterDisplay: FC<{ semester: Semester }> = ({ semester }) => {
  return (
    <>
      Curso {semester.startYear}-{semester.endYear} | {semester.period === 1 ? "Primer" : "Segundo"} Cuatrimestre
    </>
  );
};

export default SemesterDisplay;
