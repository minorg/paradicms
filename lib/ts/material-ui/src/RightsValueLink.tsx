import {RightsValue} from "@paradicms/models/dist/RightsValue";
import * as React from "react";

export const RightsValueLink: React.FunctionComponent<{value: RightsValue}> = ({
  value,
}) => {
  let {text, uri} = value;
  if (text && uri) {
    return <a href={uri}>{text}</a>;
  } else if (text) {
    return <span>{text}</span>;
  } else if (uri) {
    switch (uri) {
      case "https://creativecommons.org/publicdomain/mark/1.0/": {
        text = "Public Domain Mark 1.0";
        break;
      }
      case "https://opendatacommons.org/licenses/by/1-0/": {
        text = "ODC-BY v1.0";
        break;
      }
      case "https://rightsstatements.org/page/InC/1.0/": {
        text = "In copyright";
        break;
      }
      default: {
        text = uri;
        break;
      }
    }
    return <a href={uri}>{text}</a>;
  } else {
    return null;
  }
};
