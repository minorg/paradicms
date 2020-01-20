# Paradicms
Multi-paradigm Collections Management System (CMS)

## Graphical User Interface (GUI) implementations

Paradicms is designed to support different user interfaces for different audiences and applications. Common code for the interfaces is gathered into libraries in `lib/ts`. [`lerna`](https://github.com/lerna/lerna) is used to manage the different interfaces' web applications as well as the common libraries.

### One-time setup

In the current directory:

    npm install
    
to install lerna, then

    npm run lerna:bootstrap
    
to link together the `packages` and install their dependencies.

### Running

All of the web applications are structured similarly. After completing the one-time setup, 

    cd app/generic/gui
    npm start
