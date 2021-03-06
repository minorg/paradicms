import * as React from "react";
import {GetStaticProps} from "next";
import {
  GuiMetadata,
  Image,
  Images,
  Institution,
  Institutions,
} from "@paradicms/models";
import {Hrefs} from "lib/Hrefs";
import {Data} from "lib/Data";
import {Layout} from "components/Layout";
import {Link} from "@paradicms/material-ui-next";
import {InstitutionsGallery} from "@paradicms/material-ui";

interface StaticProps {
  guiMetadata: GuiMetadata | null;
  imagesByDepictsUri: {[index: string]: readonly Image[]};
  institutions: readonly Institution[];
}

const IndexPage: React.FunctionComponent<StaticProps> = ({
  guiMetadata,
  imagesByDepictsUri,
  institutions,
}) => {
  const joinedInstitutions = React.useMemo(
    () =>
      Institutions.join({
        institutions,
        imagesByDepictsUri,
      }),
    [institutions, imagesByDepictsUri]
  );

  return (
    <Layout documentTitle="Institutions" guiMetadata={guiMetadata}>
      <InstitutionsGallery
        institutions={joinedInstitutions}
        renderInstitutionLink={(institution, children) => (
          <Link
            href={Hrefs.institution(institution.uri).home}
            data-cy={"institution-link-" + institution.uri}
          >
            {children}
          </Link>
        )}
      />
    </Layout>
  );
};

export default IndexPage;

export const getStaticProps: GetStaticProps = async (): Promise<{
  props: StaticProps;
}> => {
  const data = Data.instance;
  const institutions = data.institutions;
  const institutionUris = new Set<string>(
    institutions.map(institution => institution.uri)
  );
  return {
    props: {
      guiMetadata: data.guiMetadata,
      imagesByDepictsUri: Images.indexByDepictsUri(
        data.images.filter(image => institutionUris.has(image.depictsUri))
      ),
      institutions,
    },
  };
};
