import {useQueryParam, NumberParam} from "use-query-params";
import {JsonQueryParamConfig} from "@paradicms/base";
import {
  Collection,
  GuiMetadata,
  Image,
  Images,
  Institution,
  Object,
  ObjectQuery,
  PropertyDefinition,
} from "@paradicms/models";
import * as React from "react";
import {Layout} from "components/Layout";
import {Data} from "lib/Data";
import {GetStaticProps} from "next";
import {
  ObjectFacetedSearchGrid,
  THUMBNAIL_TARGET_DIMENSIONS,
} from "@paradicms/material-ui";
import {Link} from "@paradicms/material-ui-next";
import {Hrefs} from "lib/Hrefs";

interface StaticProps {
  collections: readonly Collection[];
  guiMetadata: GuiMetadata | null;
  images: readonly Image[];
  institutions: readonly Institution[];
  objects: readonly Object[];
  propertyDefinitions: readonly PropertyDefinition[];
}

const SearchPage: React.FunctionComponent<StaticProps> = ({
  collections,
  guiMetadata,
  images,
  institutions,
  objects,
  propertyDefinitions,
}) => {
  // if (typeof window === "undefined") {
  //   return null; // Don't render on the server
  // }

  const [page, setPage] = useQueryParam<number | null | undefined>(
    "page",
    NumberParam
  );
  const [query, setQuery] = useQueryParam<ObjectQuery>(
    "query",
    new JsonQueryParamConfig<ObjectQuery>()
  );

  return (
    <Layout
      cardTitle={
        query?.text ? (
          <span>
            <span>Search results for</span>
            &nbsp;
            <i data-cy="query-text">{query.text}</i>
          </span>
        ) : (
          "Search results"
        )
      }
      documentTitle={
        query?.text ? `Search results for "${query.text}"` : "Search results"
      }
      guiMetadata={guiMetadata}
      onSearch={text => setQuery({text})}
    >
      <ObjectFacetedSearchGrid
        collections={collections}
        images={images}
        institutions={institutions}
        objects={objects}
        page={page ?? 0}
        onChangeFilters={filters => setQuery({...query, filters})}
        onChangePage={setPage}
        propertyDefinitions={propertyDefinitions}
        renderInstitutionLink={(institution, children) => (
          <Link href={Hrefs.institution(institution.uri).home}>{children}</Link>
        )}
        renderObjectLink={(object, children) => (
          <Link
            href={Hrefs.institution(object.institution.uri).object(object.uri)}
          >
            {children}
          </Link>
        )}
        query={query ?? {}}
      />
    </Layout>
  );
};

export default SearchPage;

export const getStaticProps: GetStaticProps = async (): Promise<{
  props: StaticProps;
}> => {
  const data = Data.instance;

  const objectThumbnails: Image[] = [];
  for (const object of data.objects) {
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
    objectThumbnails.push(objectThumbnail);
  }

  return {
    props: {
      collections: data.collections,
      guiMetadata: data.guiMetadata,
      images: objectThumbnails,
      institutions: data.institutions,
      objects: data.objects,
      propertyDefinitions: data.propertyDefinitions,
    },
  };
};
