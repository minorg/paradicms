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

    cd gui/material-ui-union
    npm run build
    npm start

Then open a web browser to [http://localhost:3000](http://localhost:3000).

## Extract-Transform-Load (ETL) implementations

### Creating a new site hosted by Amazon Web Services (AWS)

`paradicms_etl.aws_site_creator.AwsSiteCreator` is a command line utility for creating sites on AWS. It requires a number of parameters, including AWS credentials. See the command line `--help` for more information.
