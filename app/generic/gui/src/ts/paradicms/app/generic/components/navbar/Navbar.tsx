import { Hrefs } from "paradicms/app/generic/Hrefs";
import * as React from "react";
import { useState } from "react";
import { NavLink, Redirect } from "react-router-dom";
import { CurrentUser } from "paradicms/app/generic/components/navbar/CurrentUser";
import { AppBar, Box, makeStyles, Toolbar, Typography } from "@material-ui/core";
import { NavbarSearchForm } from "paradicms/app/generic/components/navbar/NavbarSearchForm";
import { NavbarUserDropdown } from "paradicms/app/generic/components/navbar/NavbarUserDropdown";

const useStyles = makeStyles((theme) => ({
  brand: {
    marginRight: theme.spacing(4),
  },
  navLink: {
    color: "white",
    fontSize: "larger",
    marginRight: theme.spacing(4),
    textDecoration: "none"
  }
}));

export const Navbar: React.FunctionComponent< {
  currentUser?: CurrentUser;
  onSearch?: (text: string) => void;
}> = ({
  currentUser,
  onSearch: onSearchUserDefined
}) => {
  const [state, setState] = useState<{redirectToSearchText: string | null}>({
    redirectToSearchText: null,
  });

  const classes = useStyles();

  // @ts-ignore
  let onSearch: (text: string) => void;
  if (onSearchUserDefined) {
    onSearch = onSearchUserDefined;
  } else {
    onSearch = (text: string) =>
      setState(prevState => Object.assign({}, prevState, {redirectToSearchText: text}));

    if (state.redirectToSearchText) {
      return <Redirect to={Hrefs.search({text: state.redirectToSearchText})} />;
    }
  }

  return (
    <AppBar data-cy="navbar" position="static">
      <Toolbar>
        <Typography variant="h6" className={classes.brand}>
          Paradicms
        </Typography>
        <NavLink to={Hrefs.home} className={classes.navLink}>
          Home
        </NavLink>
        <NavbarSearchForm onSearch={onSearch} />
        <Box ml="auto">
          <NavbarUserDropdown
            currentUser={currentUser}
            loginHref={Hrefs.login()}
            logoutHref={Hrefs.logout}
          />
        </Box>
      </Toolbar>
    </AppBar>
  );
};
