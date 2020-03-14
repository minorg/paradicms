import * as React from "react";
import {
  Collapse,
  DropdownItem,
  DropdownMenu,
  DropdownToggle,
  Nav,
  NavItem,
  NavLink,
  UncontrolledDropdown,
} from "reactstrap";
import {CurrentUser} from "paradicms/app/generic/components/navbar/CurrentUser";

export const NavbarUserDropdown: React.FunctionComponent<{
  className?: string;
  currentUser?: CurrentUser;
  loginHref: string;
  logoutHref: string;
}> = ({className, currentUser, loginHref, logoutHref}) => {
  let currentUserJsx: React.ReactNode;
  if (currentUser) {
    currentUserJsx = (
      <UncontrolledDropdown nav inNavbar>
        <DropdownToggle nav caret>
          {currentUser.name}
        </DropdownToggle>
        <DropdownMenu right>
          {/* <DropdownItem><Link to={Hrefs.userSettings}>Settings</Link></DropdownItem> */}
          <DropdownItem>
            <a href={logoutHref}>Logout</a>
          </DropdownItem>
        </DropdownMenu>
      </UncontrolledDropdown>
    );
  } else {
    currentUserJsx = (
      <NavItem>
        <NavLink href={loginHref}>Login</NavLink>
      </NavItem>
    );
  }
  return (
    <Collapse navbar>
      <Nav className={className} navbar>
        {currentUserJsx}
      </Nav>
    </Collapse>
  );
};
