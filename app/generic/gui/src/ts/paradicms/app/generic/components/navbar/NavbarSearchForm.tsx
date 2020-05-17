import * as React from "react";
import { ChangeEvent, FormEvent, useState } from "react";
import { fade, IconButton, InputBase, makeStyles } from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";

const useStyles = makeStyles((theme) => ({
  inputRoot: {
    color: 'inherit',
  },
  inputInput: {
    padding: theme.spacing(1, 1, 1, 0),
    // vertical padding + font size from searchIcon
    paddingLeft: "1em",
    transition: theme.transitions.create('width'),
    width: '100%',
  },
  search: {
    position: 'relative',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: fade(theme.palette.common.white, 0.15),
    '&:hover': {
      backgroundColor: fade(theme.palette.common.white, 0.25),
    },
    paddingLeft: theme.spacing(2)
  }
}));


export const NavbarSearchForm: React.FunctionComponent<{
  className?: string;
  onSearch: (text: string) => void;
}> = ({className, onSearch}) => {
  const [state, setState] = useState<{text: string}>({text: ""});
  const classes = useStyles();

  const onClickSearchButton = () => {
    console.info("On click search button");
    setState(prevState => Object.assign({}, prevState, {text: ""}));
    onSearch(state.text);
  };
  const onSubmit = (ev: FormEvent<HTMLFormElement>) => {
    ev.preventDefault(); // Prevent the form from being submitted normally, which messes everything up.
    onClickSearchButton();
  }
  const onTextChange = (ev: ChangeEvent<HTMLInputElement>) => {
    const text = ev.target.value;
    setState(prevState => Object.assign({}, prevState, {text}));
  };

  return (
    <form className={classes.search} onSubmit={onSubmit} style={{ display: "inline-block" }}>
      <InputBase
        placeholder="Search"
        classes={{
          root: classes.inputRoot,
          input: classes.inputInput,
        }}
        onChange={onTextChange}
        inputProps={{ 'aria-label': 'search' }}
      />
      <IconButton onClick={onClickSearchButton}>
        <SearchIcon/>
      </IconButton>
    </form>
  );
};
