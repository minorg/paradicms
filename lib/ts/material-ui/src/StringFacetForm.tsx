import * as React from "react";
import {Checkbox, FormControlLabel, List, ListItem} from "@material-ui/core";
import {StringFilter, StringFilterState} from "@paradicms/models";

export const StringFacetForm: React.FunctionComponent<{
  currentState?: StringFilter; // value id's only
  onChange: (newState?: StringFilter) => void;
  valueUniverse: {[index: string]: string}; // value id: value label
}> = ({currentState, onChange, valueUniverse}) => {
  const state = new StringFilterState({
    filter: currentState,
    valueUniverse: Object.keys(valueUniverse),
  });

  return (
    <List>
      {Object.keys(valueUniverse).map(valueId => {
        const valueLabel = valueUniverse[valueId];

        const onChangeValue = (
          e: React.ChangeEvent<HTMLInputElement>
        ): void => {
          const newChecked = e.target.checked;
          if (newChecked) {
            state.includeValue(valueId);
          } else {
            state.excludeValue(valueId);
          }
          onChange(state.snapshot);
        };

        return (
          <ListItem className="w-100" key={valueId}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={state.includesValue(valueId)}
                  onChange={onChangeValue}
                />
              }
              data-cy={"facet-value-" + valueId}
              label={valueLabel}
            />
          </ListItem>
        );
      })}
    </List>
  );
};
