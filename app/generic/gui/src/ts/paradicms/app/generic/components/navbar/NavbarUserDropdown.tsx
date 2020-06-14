import * as React from "react";
import { CurrentUser } from "paradicms/app/generic/components/navbar/CurrentUser";
import { Link, makeStyles, Menu, MenuItem } from "@material-ui/core";
import { Hrefs } from "paradicms/app/generic/Hrefs";

const useStyles = makeStyles((theme) => ({
  navLink: {
    color: "white",
    fontSize: "larger",
    textDecoration: "none"
  }
}));

export const NavbarUserDropdown: React.FunctionComponent<{
  currentUser?: CurrentUser;
  loginHref?: string;
  logoutHref?: string;
}> = ({currentUser, loginHref, logoutHref}) => {
  const [anchorEl, setAnchorEl] = React.useState<HTMLSpanElement | null>(null);
  const open = Boolean(anchorEl);
  const classes = useStyles();

  if (!currentUser) {
    return (
      <Link className={classes.navLink} href={loginHref ? loginHref : Hrefs.login()}>Login</Link>
    );
  }

  return (
    <React.Fragment>
      <div className={classes.navLink} onClick={(event) => setAnchorEl(event.currentTarget)}>
        {currentUser.name}
      </div>
      <Menu
        anchorEl={anchorEl}
        anchorOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
        keepMounted
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
        open={open}
        onClose={() => setAnchorEl(null)}
      >
        <MenuItem><Link href={logoutHref ? logoutHref : Hrefs.logout}>Logout</Link></MenuItem>
      </Menu>
    </React.Fragment>
  );
};
