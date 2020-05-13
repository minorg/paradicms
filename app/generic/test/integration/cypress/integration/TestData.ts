const objects = [...Array(10)].map((_, i) => ({
  subject: "Test subject " + i,
  title: "Test object " + i,
  uri: "http://example.com/object" + i
}));

export const TestData = {
  collection: {
    name: "Test collection",
    uri: "http://example.com/collection"
  },
  institution: {
    name: "Test institution",
    uri: "http://example.com/institution"
  },
  object: objects[0],
  objects
}
