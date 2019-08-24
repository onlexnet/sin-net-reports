import React from "react";
import { Navbar, Button, Alignment } from "@blueprintjs/core";
import { IconNames } from "@blueprintjs/icons";
import { HeaderAuth } from "./HeaderAuth";

export const Header: React.FC = props => {
  return (
    <Navbar>
      <Navbar.Group align={Alignment.LEFT}>
        <Navbar.Heading>Blueprint</Navbar.Heading>
        <Navbar.Divider />
        <Button className="bp3-minimal" icon="home" text="Home" />
        <Button className="bp3-minimal" icon="document" text="Files" />
      </Navbar.Group>
      <Navbar.Group align={Alignment.RIGHT}>
        <HeaderAuth />
      </Navbar.Group>
    </Navbar>
  );
};

export default Header;
