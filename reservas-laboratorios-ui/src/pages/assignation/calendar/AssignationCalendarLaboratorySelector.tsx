import { Box, FormControl, MenuItem, Select, SelectChangeEvent } from "@mui/material";
import { FC } from "react";
import { Laboratory } from "../../laboratory/Laboratory.types";

interface LaboratorySelectorProps {
  laboratories: Laboratory[];
  selectedLaboratoryState: [Laboratory, React.Dispatch<React.SetStateAction<Laboratory>>];
}

const LaboratorySelector: FC<LaboratorySelectorProps> = ({ laboratories, selectedLaboratoryState }) => {
  const [selectedLaboratory, setSelectedLaboratory] = selectedLaboratoryState;

  const handleChange = (event: SelectChangeEvent<number>) => {
    const selectedId = event.target.value;
    setSelectedLaboratory(laboratories.find((laboratory) => laboratory.id === selectedId)!);
  };

  return (
    <Box sx={{ display: "flex", justifyContent: "center", p: 2, backgroundColor: "secondary.light" }}>
      <FormControl sx={{ width: 200 }}>
        <Select value={selectedLaboratory.id} onChange={handleChange} required variant="standard" disableUnderline sx={{ fontSize: "body2.fontSize" }}>
          {laboratories?.map((laboratory) => (
            <MenuItem value={laboratory.id} key={laboratory.name}>
              {laboratory.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Box>
  );
};

export default LaboratorySelector;
