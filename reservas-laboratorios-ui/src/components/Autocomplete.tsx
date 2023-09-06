import { TextField, Autocomplete as MuiAutocomplete, createFilterOptions } from "@mui/material";
import { FC, useState } from "react";

const filter = createFilterOptions<Option>();

interface AutocompleteProps {
  label: string;
  options: Option[];
  onSelect: (value: Option | null) => void;
  initialValue?: Option | null;
  required?: boolean;
}

interface Option {
  inputValue?: string;
  label: string;
}

const Autocomplete: FC<AutocompleteProps> = ({ label, options, onSelect, required, initialValue }) => {
  const [value, setValue] = useState<Option | null>(initialValue || null);

  const setOption = (option: Option | null) => {
    setValue(option);
    onSelect(option);
  };

  return (
    <MuiAutocomplete
      value={value}
      onChange={(_, newValue) => {
        if (typeof newValue === "string") {
          setOption({
            label: newValue,
          });
        } else if (newValue && newValue.inputValue) {
          // Create a new value from the user input
          setOption({
            label: newValue.inputValue,
          });
        } else {
          setOption(newValue);
        }
      }}
      filterOptions={(options, params) => {
        const filtered = filter(options, params);

        const { inputValue } = params;
        // Suggest the creation of a new value
        const isExisting = options.some((option) => inputValue === option.label);
        if (inputValue !== "" && !isExisting) {
          filtered.push({
            inputValue,
            label: `Crear "${inputValue}"`,
          });
        }

        return filtered;
      }}
      selectOnFocus
      clearOnBlur
      handleHomeEndKeys
      options={options}
      getOptionLabel={(option) => {
        // Value selected with enter, right from the input
        if (typeof option === "string") {
          return option;
        }
        // Add "xxx" option created dynamically
        if (option.inputValue) {
          return option.inputValue;
        }
        // Regular option
        return option.label;
      }}
      renderOption={(props, option) => <li {...props}>{option.label}</li>}
      freeSolo
      renderInput={(params) => <TextField {...params} label={label} required={required} />}
    />
  );
};

export default Autocomplete;
