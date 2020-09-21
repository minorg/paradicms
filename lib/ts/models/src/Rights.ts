import {RightsValue} from "./RightsValue";

export interface Rights {
  creator?: RightsValue | null;
  holder?: RightsValue | null;
  license?: RightsValue | null;
  statement?: RightsValue | null;
}
