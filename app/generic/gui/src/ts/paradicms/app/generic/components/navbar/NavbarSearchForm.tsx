import { Button, Form, Input } from "reactstrap";
import * as React from "react";
import { ChangeEvent, FormEvent, useState } from "react";

export const NavbarSearchForm: React.FunctionComponent<{
  className?: string;
  onSearch: (text: string) => void;
}> = ({className, onSearch}) => {
  const [state, setState] = useState<{text: string}>({text: ""});

  const onSubmit = (ev: FormEvent<HTMLFormElement>) => {
    ev.preventDefault(); // Prevent the form from being submitted normally, which messes everything up.
    setState(prevState => Object.assign({}, prevState, {text: ""}));
    onSearch(state.text);
  }
  const onTextChange = (ev: ChangeEvent<HTMLInputElement>) => {
    const text = ev.target.value;
    setState(prevState => Object.assign({}, prevState, {text}));
  };

  return (
    <Form className={className} inline onSubmit={onSubmit}>
      <Input
        className="form-control"
        onChange={onTextChange}
        placeholder="Search"
        style={{width: "32em"}}
        type="search"
        value={state.text}
      />
      <Button className="ml-2 pt-2" size="sm" type="submit">
        Search
      </Button>
    </Form>
  );
};
