# BaSyx Secure Setup

All BaSyx components support role-based access control by using Keycloak as identity provider.
Access rules are defined based on roles. Roles are defined in the Keycloak server.

In this setup, an nginx proxy is used to expose the different BaSyx services under dedicated subdomains.
The main URLs are:

AAS Web UI: http://aasgui.basyx.localhost

Keycloak: http://keycloak.basyx.localhost

Additional service URLs can be found in the docker-compose file.

Modern browsers like Google Chrome, Firefox, and others automatically resolve any URL ending with .localhost to the local address 127.0.0.1.
This means requests to these URLs are directly routed to your own machine, where the nginx instance running inside Docker forwards the requests to the corresponding BaSyx service.
As an alternative for setups where .localhost handling might not work correctly, you could manually map the required domains to 127.0.0.1 by editing your /etc/hosts file.

To start the secure setup execute the following command

```bash
docker-compose up -d
```

This will start the BaSyx components and the Keycloak server. The Keycloak server can be found at http://keycloak.basyx.localhost.
There you can login as admin with username `admin` and password `keycloak-admin`.
![BaSyx Realm User Overview](users.png)

The example comes with an already configured realm `BaSyx` and a user `john.doe` with password `johndoe`.
This user has the `admin` role and can access all BaSyx components and all information about each component.

The entry point for accessing the Asset Administration Shells and their Submodels is the AAS Web UI running at http://aasgui.basyx.localhost.
After opening the page you will be redirected to the Keycloak login page. Use the credentials of user `john.doe` to log in.
![Login to BaSyx using Keycloak](login.png)

From there you can access the AAS and Submodels of the BaSyx components.
The UI shows the login status in the top right corner.
To end your session click on the logout button in the top right corner.
![Logout button in the AAS UI](logout.png)

There are several other user accounts available, each with different roles. You can use them to test the different levels of access. The password for these users is their username without the dots. You can find them in the [Users](http://keycloak.basyx.localhost/admin/master/console/#/BaSyx/users) tab of the BaSyx realm in Keycloak.

## Upload AAS Environment files (AASX/JSON/XML) with RBAC

This secured example also demonstrates the secured upload of AAS Environment files with a specific role `basyx-uploader`. To upload an AAS Environment file using the AAS GUI, please login using below account:

- username: basyx.uploader
- password: basyxuploader

After logging in, use the upload icon to upload the AAS Environment files.

> **Note:** For uploading an AAS Environment file (AASX/JSON/XML), there should be defined rules for the creation, updation, and reading. As AAS Environment combines all repositories, aas-environment-rbac-rules should have rules for CREATE, UPDATE, and READ for targetInformation aas, submodel, and concept-descriptions. The reason is that while uploading the AAS Env files, it interacts with all the repositories and if any appropriate rule is missing then it throws the exception. This is also documented in [here](https://wiki.basyx.org/en/latest/content/user_documentation/basyx_components/v2/aas_environment/features/authorization.html#:~:text=For%20upload%2Drelated,subjects%20under%20consideration.).

