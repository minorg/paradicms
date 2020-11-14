import * as React from "react";
import {Checkbox, FormControlLabel, List, ListItem} from "@material-ui/core";
import {
  StringFacetValue,
  StringFilter,
  StringFilterState,
} from "@paradicms/models";

export const StringFacetForm: React.FunctionComponent<{
  currentState?: StringFilter; // value id's only
  onChange: (newState?: StringFilter) => void;
  valueUniverse: readonly StringFacetValue[];
}> = ({currentState, onChange, valueUniverse}) => {
  const state = new StringFilterState({
    filter: currentState,
    valueUniverse: valueUniverse.map(value => value.value),
  });

  return (
    <List>
      {valueUniverse
        .concat()
        .sort((left, right) => right.count - left.count)
        .map(value => {
          const onChangeValue = (
            e: React.ChangeEvent<HTMLInputElement>
          ): void => {
            const newChecked = e.target.checked;
            if (newChecked) {
              state.includeValue(value.value);
            } else {
              state.excludeValue(value.value);
            }
            onChange(state.snapshot);
          };

          return (
            <ListItem className="w-100" key={value.value}>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={state.includesValue(value.value)}
                    onChange={onChangeValue}
                  />
                }
                data-cy={"facet-value-" + value.value}
                label={`${value.label ?? value.value} (${value.count})`}
              />
            </ListItem>
          );
        })}
    </List>
  );
};
