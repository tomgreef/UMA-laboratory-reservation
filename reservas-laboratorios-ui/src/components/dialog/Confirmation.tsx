import { Dialog, DialogContent, DialogActions, Button } from "@mui/material";
import { FC, useState } from "react";

type ConfirmationProps = {
  onConfirm: () => void;
  message: string;
  buttonText?: string;
  onCancel?: () => void;
  children?: React.ReactNode;
  smallButton?: boolean;
};

const Confirmation: FC<ConfirmationProps> = ({ onConfirm, message, buttonText, onCancel, smallButton: small }) => {
  const [openDialog, setOpenDialog] = useState(onCancel ? true : false);

  return (
    <>
      <Button onClick={() => setOpenDialog(true)} size={small ? "small" : "medium"}>
        {buttonText || "Borrar"}
      </Button>
      {openDialog && (
        <Dialog open onClose={() => setOpenDialog(false)}>
          <DialogContent>{message}</DialogContent>
          <DialogActions>
            <Button
              onClick={() => {
                onConfirm();
                setOpenDialog(false);
              }}
            >
              Confirmar
            </Button>
            <Button
              onClick={() => {
                onCancel && onCancel();
                setOpenDialog(false);
              }}
            >
              Cancelar
            </Button>
          </DialogActions>
        </Dialog>
      )}
    </>
  );
};

export default Confirmation;
