import { Paper } from "@mui/material";
import { FC, ReactNode } from "react";

const BoxWrapper: FC<{ children: ReactNode; soft?: boolean; disabled?: boolean }> = ({ children, soft, disabled }) => {
  return <Paper sx={{ width: "100%", maxWidth: "100%", p: 8, my: 4, backgroundColor: soft ? "common.white" : "primary.light", opacity: disabled ? 0.6 : 1 }}>{children}</Paper>;
};

export default BoxWrapper;
