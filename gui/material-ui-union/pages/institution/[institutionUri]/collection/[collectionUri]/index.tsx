import {NumberParam, useQueryParam} from "use-query-params";
import {
  decodeFileName,
  encodeFileName,
  JsonQueryParamConfig,
} from "@paradicms/base";
import * as React from "react";
import {Layout} from "components/Layout";
import {
  Collection,
  GuiMetadata,
  Image,
  Images,
  Institution,
  Object,
  ObjectFilters,
  PropertyDefinition,
} from "@paradicms/models";
import {Data} from "lib/Data";
import {GetStaticPaths, GetStaticProps} from "next";
import {
  ObjectFacetedSearchGrid,
  THUMBNAIL_TARGET_DIMENSIONS,
} from "@paradicms/material-ui";
import {Link} from "@paradicms/material-ui-next";
import {Hrefs} from "lib/Hrefs";

interface StaticProps {
  collection: Collection;
  collectionImages: readonly Image[];
  collectionObjects: readonly Object[];
  guiMetadata: GuiMetadata | null;
  institution: Institution;
  propertyDefinitions: readonly PropertyDefinition[];
}

const CollectionPage: React.FunctionComponent<StaticProps> = ({
  collection,
  collectionImages,
  collectionObjects,
  guiMetadata,
  institution,
  propertyDefinitions,
}) => {
  // if (typeof window === "undefined") {
  //   return null; // Don't render on the server
  // }

  const [filters, setFilters] = useQueryParam<ObjectFilters>(
    "filters",
    new JsonQueryParamConfig<ObjectFilters>()
  );
  const [page, setPage] = useQueryParam<number | null | undefined>(
    "page",
    NumberParam
  );

  return (
    <Layout
      breadcrumbs={{collection, institution}}
      cardTitle={
        <span>
          <span>
            Collection&nbsp;&mdash;&nbsp;
            <span data-cy="collection-title">{collection.title}</span>
          </span>
        </span>
      }
      documentTitle={"Collection - " + collection.title}
      guiMetadata={guiMetadata}
    >
      <ObjectFacetedSearchGrid
        collections={[collection]}
        images={collectionImages}
        institutions={[institution]}
        objects={collectionObjects}
        onChangeFilters={setFilters}
        onChangePage={setPage}
        page={page ?? 0}
        propertyDefinitions={propertyDefinitions}
        renderObjectLink={(object, children) => (
          <Link
            href={Hrefs.institution(object.institution.uri).object(object.uri)}
          >
            {children}
          </Link>
        )}
        query={{filters}}
      />
    </Layout>
  );
};

export default CollectionPage;

export const getStaticPaths: GetStaticPaths = async () => {
  const data = Data.instance;
  const paths: {params: {collectionUri: string; institutionUri: string}}[] = [];
  for (const institution of data.institutions) {
    const encodedInstitutionUri = encodeFileName(institution.uri);
    for (const collection of data.collectionsByInstitutionUri[
      institution.uri
    ] ?? []) {
      paths.push({
        params: {
          collectionUri: encodeFileName(collection.uri),
          institutionUri: encodedInstitutionUri,
        },
      });
    }
  }

  return {
    fallback: false,
    paths,
  };
};

export const getStaticProps: GetStaticProps = async ({
  params,
}): Promise<{props: StaticProps}> => {
  const collectionUri = decodeFileName(params!.collectionUri as string);
  const institutionUri = decodeFileName(params!.institutionUri as string);

  const data = Data.instance;
  const collection = data.collectionByUri(collectionUri);
  const institution = data.institutionByUri(institutionUri);
  const collectionObjects = data.objectsByCollectionUri[collectionUri] ?? [];

  const collectionObjectThumbnails: Image[] = [];
  for (const object of collectionObjects) {
    const objectImages = data.imagesByDepictsUri[object.uri];
    if (!objectImages) {
      continue;
    }
    const objectThumbnail = Images.selectThumbnail({
      images: objectImages,
      targetDimensions: THUMBNAIL_TARGET_DIMENSIONS,
    });
    if (!objectThumbnail) {
      continue;
    }
    collectionObjectThumbnails.push(objectThumbnail);
  }

  return {
    props: {
      collection,
      collectionImages: collectionObjectThumbnails,
      collectionObjects,
      guiMetadata: data.guiMetadata,
      institution,
      propertyDefinitions: data.propertyDefinitions,
    },
  };
};
