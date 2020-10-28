import * as React from "react";
import {
  Collection,
  Image,
  Images,
  Institution,
  JoinedObject,
  Models,
  Object,
  ObjectQuery,
  Objects,
  PropertyDefinition,
} from "@paradicms/models";
import {Layout} from "components/Layout";
import {Data} from "lib/Data";
import {GetStaticProps} from "next";
import {Col, Container, Row} from "reactstrap";
import {ObjectsGallery} from "components/ObjectsGallery";
import {Hrefs} from "lib/Hrefs";
import Link from "next/link";
import {NumberParam, useQueryParam} from "use-query-params";
import {ObjectFacetsContainer} from "components/ObjectFacetsContainer";
import {JsonQueryParamConfig} from "@paradicms/base";
import {useMemo} from "react";
import {ObjectIndex} from "@paradicms/lunr";
import {ObjectFiltersBadges} from "components/ObjectFiltersBadges";

interface StaticProps {
  collection: Collection;
  images: readonly Image[];
  institution: Institution;
  objects: readonly Object[];
  propertyDefinitions: readonly PropertyDefinition[];
}

const IndexPage: React.FunctionComponent<StaticProps> = ({
  collection,
  images,
  institution,
  objects,
  propertyDefinitions,
}) => {
  const [objectQuery, setObjectQueryParam] = useQueryParam<ObjectQuery>(
    "query",
    new JsonQueryParamConfig<ObjectQuery>()
  );
  // console.info("Query:", JSON.stringify(objectQuery));

  const objectIndex = useMemo(
    () => new ObjectIndex(objects, propertyDefinitions),
    [objects]
  );

  const filteredObjects = useMemo(() => {
    let filteredObjects = objects;
    if (objectQuery) {
      if (objectQuery.text) {
        filteredObjects = objectIndex.search(objectQuery.text);
      }
      if (objectQuery.filters) {
        filteredObjects = Objects.filter({
          filters: objectQuery.filters,
          objects: filteredObjects,
        });
      }
    }
    return filteredObjects;
  }, [objectQuery, objects]);

  const objectFacets = useMemo(
    () => Objects.facetize(propertyDefinitions, objects),
    [objects, propertyDefinitions]
  );

  const joinedFilteredObjects: readonly JoinedObject[] = useMemo(
    () =>
      Objects.join({
        collectionsByUri: Models.indexByUri([collection]),
        imagesByDepictsUri: Images.indexByDepictsUri(images),
        institutionsByUri: Models.indexByUri([institution]),
        objects: filteredObjects,
      }),
    [collection, images, institution, filteredObjects]
  );

  let [pageQueryParam, setPage] = useQueryParam<number | null | undefined>(
    "page",
    NumberParam
  );
  const page = pageQueryParam ?? 0;

  return (
    <Layout
      collection={collection}
      onSearch={text => {
        setObjectQueryParam({text});
        setPage(undefined);
      }}
    >
      <Container fluid>
        {joinedFilteredObjects.length > 0 ? (
          <>
            <Row>
              <Col xs={12}>
                <h4 className="d-inline-block">
                  <span>{joinedFilteredObjects.length}</span>&nbsp;
                  <span>
                    {joinedFilteredObjects.length === 1 ? "object" : "objects"}
                  </span>
                  &nbsp;
                  {objectQuery && objectQuery.text ? (
                    <span>
                      matching <i>{objectQuery.text}</i>
                    </span>
                  ) : (
                    <span>matched</span>
                  )}
                </h4>
                {objectQuery?.filters ? (
                  <div className="d-inline-block">
                    <ObjectFiltersBadges
                      objectFilters={objectQuery.filters}
                      propertyDefinitions={propertyDefinitions}
                    />
                  </div>
                ) : null}
              </Col>
            </Row>
            <Row>
              <Col xs={12}>
                <hr />
              </Col>
            </Row>
          </>
        ) : null}
        <Row>
          <Col xs="10">
            {joinedFilteredObjects.length > 0 ? (
              <ObjectsGallery
                objects={joinedFilteredObjects}
                onChangePage={setPage}
                page={page}
                renderObjectLink={(object, children) => (
                  <Link {...Hrefs.object(object.uri)}>
                    <a>{children}</a>
                  </Link>
                )}
              />
            ) : (
              <h3>No matching objects found.</h3>
            )}
          </Col>
          <Col xs="2">
            {joinedFilteredObjects.length > 0 ? (
              <ObjectFacetsContainer
                facets={objectFacets}
                filters={objectQuery?.filters ?? {}}
                onChange={newObjectFilters => {
                  setObjectQueryParam({
                    ...objectQuery,
                    filters: newObjectFilters,
                  });
                  setPage(undefined);
                }}
              />
            ) : null}
          </Col>
        </Row>
      </Container>
    </Layout>
  );
};

export default IndexPage;

export const getStaticProps: GetStaticProps = async (): Promise<{
  props: StaticProps;
}> => {
  const data = new Data();
  return {
    props: {
      collection: data.collection,
      images: data.images,
      institution: data.institution,
      objects: data.objects,
      propertyDefinitions: data.propertyDefinitions,
    },
  };
};
