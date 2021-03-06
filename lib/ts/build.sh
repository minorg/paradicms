#!/bin/bash

set -e

cd "$(dirname "$0")"

cd base
npm run build

cd ../models
npm run build

cd ../bootstrap
npm run build

cd ../lunr
npm run build

cd ../material-ui
npm run build

cd ../next
npm run build

cd ../material-ui-next
npm run build
