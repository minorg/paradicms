'use strict';

// Adapted from https://www.contentful.com/blog/2019/01/23/combining-apis-graphql-schema-stitching-part-1/
const {ApolloServer} = require('apollo-server');
const {HttpLink} = require('apollo-link-http');
const {setContext} = require('apollo-link-context');
const {
    introspectSchema,
    makeRemoteExecutableSchema,
    mergeSchemas
} = require('graphql-tools');
const fetch = require('node-fetch');

const genericLink = new HttpLink({uri: "http://generic-service:9000/api/graphql", fetch});

async function startServer() {
    const genericRemoteSchema = await introspectSchema(genericLink);

    const genericSchema = makeRemoteExecutableSchema({
        schema: genericRemoteSchema,
        link: genericLink,
    });

    // const contentfulRemoteSchema = await introspectSchema(contentfulLink);
    //
    // const contentfulSchema = makeRemoteExecutableSchema({
    //     schema: contentfulRemoteSchema,
    //     link: contentfulLink,
    // });

    const schema = mergeSchemas({
        schemas: [
            genericSchema
            // gitHubSchema,
            // contentfulSchema
        ]
    });

    const server = new ApolloServer({schema});

    return await server.listen();
}

startServer().then(({ url }) => {
    console.log(`Server ready at ${url}`);
});
