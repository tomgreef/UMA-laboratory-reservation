export type SemesterDto = Omit<Semester, "id" | "active">;

export type Semester = {
  id: number;
  startYear: number;
  endYear: number;
  period: 1 | 2;
  active: boolean;
};
