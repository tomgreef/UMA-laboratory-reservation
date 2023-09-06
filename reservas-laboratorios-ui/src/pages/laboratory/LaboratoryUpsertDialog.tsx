import { Button, Dialog, DialogTitle, DialogContent, Grid, TextField, FormControl, InputLabel, Select, SelectChangeEvent, MenuItem, DialogActions, Box, Chip, OutlinedInput } from "@mui/material";
import { FC, useState } from "react";
import { Laboratory } from "./Laboratory.types";
import { useCustomMutation } from "../../utils/Mutation";
import Autocomplete from "../../components/Autocomplete";

type UpsertLaboratoryProps = {
  refetchLaboratories: () => void;
  existingLaboratories: Laboratory[];
  laboratory?: Laboratory;
  disabled?: boolean;
};

const initializeLaboratory = (): Laboratory => {
  return {
    id: -1,
    name: "",
    capacity: 50,
    location: "",
    operatingSystem: "",
    additionalEquipment: "",
    adjacentLaboratories: [],
  };
};

const UpsertLaboratory: FC<UpsertLaboratoryProps> = ({ refetchLaboratories, existingLaboratories, laboratory: existingLaboratory, disabled }) => {
  const [openDialog, setOpenDialog] = useState(false);
  const [newLaboratory, setNewLaboratory] = useState<Laboratory>(existingLaboratory || initializeLaboratory());
  const [saveLaboratory] = useCustomMutation<Laboratory, Laboratory>("laboratories");
  const { name, capacity, location, operatingSystem, additionalEquipment, adjacentLaboratories } = newLaboratory;

  const nameAlreadyExists = existingLaboratories.filter((laboratory) => laboratory.id !== existingLaboratory?.id).some((laboratory) => laboratory.name.toUpperCase().trim() === name.toUpperCase().trim());
  const saveIsDisabled = !name || !capacity || !location || nameAlreadyExists;

  const distinctLocations = [...new Set(existingLaboratories.map((laboratory) => laboratory.location))];
  const distinctOperatingSystems = [...new Set(existingLaboratories.filter((lab) => lab.operatingSystem).map((laboratory) => laboratory.operatingSystem!))];
  const distinctAdditionalEquipment = [...new Set(existingLaboratories.filter((lab) => lab.additionalEquipment).map((laboratory) => laboratory.additionalEquipment!))];

  const handleSave = () => {
    saveLaboratory(newLaboratory).then(() => {
      refetchLaboratories();
      setOpenDialog(false);
    });
  };

  const handleClose = () => {
    setNewLaboratory(existingLaboratory || initializeLaboratory());
    setOpenDialog(false);
  };

  return (
    <>
      <Button onClick={() => setOpenDialog(true)} disabled={disabled} size="small">
        {existingLaboratory ? "Editar" : "Crear"}
      </Button>
      {openDialog && (
        <Dialog open onClose={handleClose}>
          <DialogTitle>{existingLaboratory ? "Actualizar" : "Crear"} laboratorio</DialogTitle>
          <DialogContent>
            <Grid container pt={4} spacing={4}>
              <Grid item xs={8}>
                <TextField
                  fullWidth
                  label="Nombre"
                  required
                  value={name}
                  onChange={(e) =>
                    setNewLaboratory((prev) => {
                      return { ...prev, name: e.target.value };
                    })
                  }
                  error={nameAlreadyExists}
                  helperText={nameAlreadyExists && "Ya existe un laboratorio con ese nombre"}
                />
              </Grid>
              <Grid item xs={4}>
                <TextField
                  fullWidth
                  label="Capacidad"
                  required
                  type="number"
                  value={capacity}
                  onChange={(e) =>
                    setNewLaboratory((prev) => {
                      return { ...prev, capacity: parseInt(e.target.value) };
                    })
                  }
                />
              </Grid>
              <Grid item xs={12}>
                <Autocomplete
                  label="Localización"
                  onSelect={(value) =>
                    setNewLaboratory((prev) => {
                      return { ...prev, location: value?.label || "" };
                    })
                  }
                  options={distinctLocations.map((location) => ({ label: location }))}
                  initialValue={{ label: location }}
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <Autocomplete
                  label="Sistema operativo"
                  onSelect={(value) =>
                    setNewLaboratory((prev) => {
                      return { ...prev, operatingSystem: value?.label || "" };
                    })
                  }
                  options={distinctOperatingSystems.map((os) => ({ label: os }))}
                  initialValue={{ label: operatingSystem || "" }}
                />
              </Grid>
              <Grid item xs={12}>
                <Autocomplete
                  label="Equipo adicional"
                  onSelect={(value) =>
                    setNewLaboratory((prev) => {
                      return { ...prev, additionalEquipment: value?.label || "" };
                    })
                  }
                  options={distinctAdditionalEquipment.map((equipment) => ({ label: equipment }))}
                  initialValue={{ label: additionalEquipment || "" }}
                />
              </Grid>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel>Laboratorios adjacentes</InputLabel>
                  <Select
                    multiple
                    value={adjacentLaboratories}
                    onChange={(event: SelectChangeEvent<number[]>) => {
                      const value = event.target.value;
                      if (typeof value === "string") {
                        return;
                      }

                      setNewLaboratory((prev) => {
                        return { ...prev, adjacentLaboratories: value };
                      });
                    }}
                    input={<OutlinedInput label="Laboratorios adjacentes" />}
                    renderValue={(selected) => (
                      <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
                        {selected.map((value) => (
                          <Chip key={value} label={existingLaboratories.find((laboratory) => laboratory.id === value)?.name} />
                        ))}
                      </Box>
                    )}
                  >
                    {existingLaboratories
                      .filter((l) => l.id !== existingLaboratory?.id)
                      .map((laboratory, index) => (
                        <MenuItem key={name + laboratory.id + index} value={laboratory.id} sx={{ mx: 2, my: 1, borderRadius: 2, "&.Mui-selected": { display: "flex", gap: 0.5, color: "white", backgroundColor: "primary.dark", fontWeight: "bold", ":hover": { backgroundColor: "primary.main" } } }}>
                          {laboratory.name}
                        </MenuItem>
                      ))}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleSave} disabled={saveIsDisabled}>
              Guardar
            </Button>
            <Button onClick={handleClose}>Cancelar</Button>
          </DialogActions>
        </Dialog>
      )}
    </>
  );
};

export default UpsertLaboratory;
