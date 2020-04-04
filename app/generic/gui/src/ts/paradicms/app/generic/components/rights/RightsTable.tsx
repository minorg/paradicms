import { CollectionOverviewQuery_collectionByUri_rights } from "paradicms/app/generic/api/queries/types/CollectionOverviewQuery";
import * as React from "react";
import { Uris } from "paradicms-base";

type Rights = CollectionOverviewQuery_collectionByUri_rights;

export const RightsTable: React.FunctionComponent<{className?: string; rights: Rights}> = ({
  className,
  rights,
}) => (
  <table className={className + " w-100"}>
    <tbody>
    {(rights.text || rights.statementUri) ?
      <tr>
        <td className="px-2">
          <strong>Rights</strong>
        </td>
        <td className="px-2">{(rights.text && rights.statementUri) ? <a href={rights.statementUri}>{rights.text}</a> : <React.Fragment>{rights.text}</React.Fragment>}</td>
      </tr> : null}
      {rights.holder ? (
        <tr>
          <td className="px-2">
            <strong>Holder</strong>
          </td>
          <td className="px-2">{rights.holder}</td>
        </tr>
      ) : null}
      {rights.license ? (
        <tr>
          <td className="px-2">
            <strong>License</strong>
          </td>
          <td className="px-2">
            {Uris.isUrl(rights.license) ? (
              <a href={rights.license}>{rights.license}</a>
            ) : (
              <React.Fragment>{rights.license}</React.Fragment>
            )}
          </td>
        </tr>
      ) : null}
    </tbody>
  </table>
);
