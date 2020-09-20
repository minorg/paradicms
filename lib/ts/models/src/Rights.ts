import {RightsValue} from "./RightsValue";

export interface Rights {
  holder?: RightsValue | null;
  license?: RightsValue | null;
  statement?: RightsValue | null;
}
