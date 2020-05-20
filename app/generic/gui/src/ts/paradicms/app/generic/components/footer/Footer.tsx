import { Hrefs } from "paradicms/app/generic/Hrefs";
import * as React from "react";
import { Grid, makeStyles } from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  "footerParagraph": {
    textAlign: "center"
  }
}));

export const Footer: React.FunctionComponent = () => {
  const classes = useStyles();
  return (
    <footer>
      <Grid container direction="column">
        <Grid item>
          <p className={classes.footerParagraph}>
            This site is for educational purposes only.
          </p>
        </Grid>
        <Grid item>
          <p className={classes.footerParagraph}>
            <a href={Hrefs.contact}>Contact</a>&nbsp;|&nbsp;<a href={Hrefs.privacy}>Privacy</a>&nbsp;|&nbsp;<a href="https://github.com/minorg/paradicms">GitHub</a>
          </p>
        </Grid>
      </Grid>
    </footer>
  );
}
