import {RightsValue} from "@paradicms/models/dist/RightsValue";
import * as React from "react";

// const RIGHTS_URI_TEXTS: {[index: string]: string} = {
//   "https://creativecommons.org/publicdomain/mark/1.0/":
//     "Public Domain Mark 1.0",
//   "https://creativecommons.org/licenses/by-sa/2.0/": "CC BY-SA 2.0",
//   "https://creativecommons.org/licenses/by-sa/3.0/": "CC BY-SA 3.0",
//   "https://creativecommons.org/licenses/by/2.0/": "CC BY 2.0",
//   "https://opendatacommons.org/licenses/by/1-0/": "ODC BY 1.0",
//   "http://rightsstatements.org/vocab/InC/1.0/": "In copyright",
//   "https://rightsstatements.org/vocab/InC/1.0/": "In copyright",
// };

export const RightsValueLink: React.FunctionComponent<{value: RightsValue}> = ({
  value,
}) => {
  let {text, uri} = value;
  if (text && uri) {
    return <a href={uri}>{text}</a>;
  } else if (text) {
    return <span>{text}</span>;
  } else if (uri) {
    return <a href={uri}>{uri}</a>;
  } else {
    return null;
  }
};
