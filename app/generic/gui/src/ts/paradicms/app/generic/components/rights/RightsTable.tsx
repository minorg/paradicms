import { CollectionOverviewInitialQuery_collectionByUri_rights } from "paradicms/app/generic/api/queries/types/CollectionOverviewInitialQuery";
import * as React from "react";
import { Uris } from "@paradicms/base";
import { makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableRow } from "@material-ui/core";

type Rights = CollectionOverviewInitialQuery_collectionByUri_rights;

const useStyles = makeStyles((theme) => ({
  cell: {
    paddingLeft: theme.spacing(2),
    paddingRight: theme.spacing(2)
  },
  table: {
    width: "100%"
  }
}));

export const RightsTable: React.FunctionComponent<{className?: string; rights: Rights}> = ({
  className,
  rights,
}) => {
  const classes = useStyles();

  return (
    <TableContainer component={Paper}>
      <Table className={classes.table}>
        <TableBody>
          {(rights.text || rights.statementUri) ?
            <TableRow>
              <TableCell className={classes.cell}>
                <strong>Rights</strong>
              </TableCell>
              <TableCell className={classes.cell}>{(rights.text && rights.statementUri) ?
                <a href={rights.statementUri}>{rights.text}</a> :
                <React.Fragment>{rights.text}</React.Fragment>}</TableCell>
            </TableRow> : null}
          {rights.holder ? (
            <TableRow>
              <TableCell className={classes.cell}>
                <strong>Holder</strong>
              </TableCell>
              <TableCell className={classes.cell}>{rights.holder}</TableCell>
            </TableRow>
          ) : null}
          {rights.license ? (
            <TableRow>
              <TableCell className={classes.cell}>
                <strong>License</strong>
              </TableCell>
              <TableCell className={classes.cell}>
                {Uris.isUrl(rights.license) ? (
                  <a href={rights.license}>{rights.license}</a>
                ) : (
                  <React.Fragment>{rights.license}</React.Fragment>
                )}
              </TableCell>
            </TableRow>
          ) : null}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
