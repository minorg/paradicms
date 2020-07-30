import * as React from "react";
import {Grid, makeStyles} from "@material-ui/core";
import {graphql, StaticQuery} from "gatsby";

const useStyles = makeStyles(theme => ({
  footerParagraph: {
    textAlign: "center",
  },
}));

export const Footer: React.FunctionComponent = () => {
  const classes = useStyles();
  return (
    <StaticQuery<GatsbyTypes.IndexLayoutQueryQuery>
      query={graphql`
        query IndexLayoutQuery {
          site {
            siteMetadata {
              author {
                email
                name
                url
              }
              gitHubUrl
            }
          }
        }
      `}
      render={(data: {
        site: {
          siteMetadata: {
            author: {
              email: string;
              name: string;
              url: string;
            };
            gitHubUrl: string;
          };
        };
      }) => (
        <footer>
          <Grid container direction="column">
            <Grid item>
              <p className={classes.footerParagraph}>
                This site is for educational purposes only.
              </p>
            </Grid>
            <Grid item>
              <p className={classes.footerParagraph}>
                <a
                  data-cy="contact-link"
                  href={"mailto:" + data.site.siteMetadata.author.email}
                >
                  Contact
                </a>
                &nbsp;|&nbsp;
                <a
                  data-cy="github-link"
                  href={data.site.siteMetadata.gitHubUrl}
                >
                  GitHub
                </a>
              </p>
            </Grid>
            <Grid item>
              <p className={classes.footerParagraph}>
                &copy; Copyright 2020,&nbsp;
                <a href={data.site.siteMetadata.author.url}>
                  {data.site.siteMetadata.author.name}
                </a>
                . All rights reserved
              </p>
            </Grid>
          </Grid>
        </footer>
      )}
    />
  );
};
