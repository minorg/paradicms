import * as React from "react";
import * as FontFaceObserver from "fontfaceobserver";

export const Fonts: React.FunctionComponent = () => {
  React.useEffect(() => {
    const link = document.createElement("link");
    link.href =
      "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,900";
    link.rel = "stylesheet";

    document.head.appendChild(link);

    const roboto = new FontFaceObserver("Roboto");

    roboto.load().then(() => {
      document.documentElement.classList.add("roboto");
    });
  }, []);
  return null;
};
