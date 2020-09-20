import * as React from "react";
import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableRow,
} from "@material-ui/core";
import {Rights} from "@paradicms/models";
import {RightsValue} from "@paradicms/models/dist/RightsValue";
import {RightsValueLink} from "./RightsValueLink";

const RightsTableRow: React.FunctionComponent<{
  label: string;
  value?: RightsValue | null;
}> = ({label, value}) => {
  if (!value) {
    return null;
  }
  return (
    <TableRow>
      <TableCell>
        <strong>{label}</strong>
      </TableCell>
      <TableCell>
        <RightsValueLink value={value} />
      </TableCell>
    </TableRow>
  );
};

export const RightsTable: React.FunctionComponent<{
  rights: Rights;
}> = ({rights}) => {
  return (
    <TableContainer component={Paper}>
      <Table>
        <TableBody>
          <RightsTableRow label="Rights statement" value={rights.statement} />
          <RightsTableRow label="Rights holder" value={rights.holder} />
          <RightsTableRow label="License" value={rights.license} />
        </TableBody>
      </Table>
    </TableContainer>
  );
};
