import { Hrefs } from "paradicms/app/generic/Hrefs";
import * as React from "react";
import { Redirect } from "react-router-dom";

import { Exception } from "@paradicms/base";
import { FatalErrorModal } from "paradicms/app/generic/components/error/FatalErrorModal";

export const GenericErrorHandler: React.FunctionComponent<{
  error?: any;
  exception?: Exception;
  onClosed?: () => void;
}> = ({error, exception}) => {
  if (error) {
    if (error.response) {
      if (error.response.status === 401) {
        return <Redirect to={Hrefs.home}></Redirect>;
      }
      // } else if (this.props.error.request) {
      //   // The request was made but no response was received
      //   // `error.request` is an instance of XMLHttpRequest in the browser and an instance of
      //   // http.ClientRequest in node.js
      //   console.log(error.request);
    }

    return <FatalErrorModal error={error}/>;
  } else if (exception) {
    return <FatalErrorModal exception={exception}/>;
  } else {
    throw new RangeError();
  }
}
