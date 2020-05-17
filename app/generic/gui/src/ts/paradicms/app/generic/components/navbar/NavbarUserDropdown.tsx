import * as React from "react";
import { NavLink } from "reactstrap";
import { CurrentUser } from "paradicms/app/generic/components/navbar/CurrentUser";
import { Link, Menu, MenuItem, Typography } from "@material-ui/core";
import { Hrefs } from "paradicms/app/generic/Hrefs";

export const NavbarUserDropdown: React.FunctionComponent<{
  currentUser?: CurrentUser;
  loginHref?: string;
  logoutHref?: string;
}> = ({currentUser, loginHref, logoutHref}) => {
  const [anchorEl, setAnchorEl] = React.useState<HTMLSpanElement | null>(null);
  const open = Boolean(anchorEl);

  if (!currentUser) {
    return (
      <NavLink href={loginHref ? loginHref : Hrefs.login()}>Login</NavLink>
    );
  }

  return (
    <React.Fragment>
      <Typography onClick={(event) => setAnchorEl(event.currentTarget)} variant="h6">
        {currentUser.name}
      </Typography>
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
